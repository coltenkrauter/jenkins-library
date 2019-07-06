import components.Config;

def call() {
    /* The initialize stage sets environment variables which can be used during the build */
    stage("Initialize") {
        /* Send build started message */
        slackPipelineStart();
        def config = new Config();

        env.GIT_REPO_NAME = env.JOB_NAME.split("/")[1];
        env.SOURCE_CODE_DIR = "${GIT_REPO_NAME}-${BUILD_NUMBER}";
        env.SOURCE_CODE = "${WORKSPACE}/${SOURCE_CODE_DIR}";
        env.ANSIBLE_REPO = config.get("ANSIBLE_REPO");
        env.GITHUB_OWNER = config.get("GITHUB_OWNER");
        env.ANSIBLE_SSH_PRIVATE_KEY_ID = config.get("ANSIBLE_SSH_PRIVATE_KEY_ID");
        env.ANSIBLE_VAULT_PASSWORD_ID = config.get("ANSIBLE_VAULT_PASSWORD_ID");
        
        /* Needed for ansible plays to recognize configuration file */
        env.ANSIBLE_CONFIG = "${WORKSPACE}/${ANSIBLE_REPO}/ansible.cfg";

        /* Set $ENVIRONMENT to one of (dev, cert, prod or test) */
        env.ENVIRONMENT = 
            env.BRANCH_NAME == "development" ? 
            "dev" : 
            env.BRANCH_NAME == "stable" ? 
            "cert" : 
            env.TAG != "" ? 
            "prod" : "test";

        /* Set which branch of ansible repository to clone */
        env.ANSIBLE_CLONE_BRANCH =
            env.BRANCH_NAME == "development" ? 
                env.BRANCH_NAME : "stable"
    }

    stageName = "Clone ${GITHUB_OWNER}/${GIT_REPO_NAME} repository";
    stage(stageName) {
        startTime = slackStageStart(stageName);

        /* Checks out the code */
        dir (env.SOURCE_CODE_DIR) {
            checkoutGit();
            /* Set tag env var (this will be an empty string if it is not tagged) */
            env.TAG = sh(returnStdout: true, script: "git tag --contains | head -1").trim();
            env.NO_BUILD = env.GIT_COMMIT_MESSAGE.contains("no-build")
        }
        slackUpdate();
        slackStageEnd(stageName, startTime, new Date());
    }

    /* Must be called after checkoutGit */
    slackBuildStart();
    
    if (NO_BUILD && NO_BUILD == "true") {
        throw new Exception("no-build")
    }

    stageName = "Clone ${GITHUB_OWNER}/${ANSIBLE_REPO} repository";
    stage(stageName) {
        startTime = slackStageStart(stageName);

        /* Clone ICC ansible git repository */
        withCredentials([string(credentialsId: GITHUB_TOKEN_CRED_ID, variable: "TOKEN")]) {
            sh "git clone -b ${ANSIBLE_CLONE_BRANCH} https://${TOKEN}@${GITHUB_URL}/${GITHUB_OWNER}/${ANSIBLE_REPO}.git";
        }
        
        slackStageEnd(stageName, startTime, new Date());
    }
}