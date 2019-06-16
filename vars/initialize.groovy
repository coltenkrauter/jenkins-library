import components.Config;

def call() {
    stage("Initialize") {
        def config = new Config();

        /* Sends build started message */
        slackPipelineStart();

        /* Formulate the docker repo name from the jenkins job name 
           TODO: A better way to get the git repo name? */
        env.GIT_REPO_NAME = env.JOB_NAME.split("/")[1];
        env.SOURCE_CODE_DIR = "${GIT_REPO_NAME}-${BUILD_NUMBER}";
        env.SOURCE_CODE = "${WORKSPACE}/${SOURCE_CODE_DIR}";
        env.ANSIBLE_REPO = config.get("ANSIBLE_REPO");

        /* Checks out the code */
        dir (env.SOURCE_CODE_DIR) {
            checkoutGit();
            /* Get Git tag (this will be an empty string if it is not tagged) */
            env.TAG = sh(returnStdout: true, script: "git tag --contains | head -1").trim();
        }

        /* Sets $ENVIRONMENT to one of (dev, cert, prod or feature) */
        env.ENVIRONMENT = 
            env.BRANCH_NAME == "dev" ? 
            "dev" : 
            env.BRANCH_NAME == "master" ? 
            "cert" : 
            env.TAG != "" ? 
            "prod" : "feature";


        /* Clone specific branch of ansible repository */
        env.ANSIBLE_CLONE_BRANCH =
            env.BRANCH_NAME == "dev" ? 
                env.BRANCH_NAME : "master"

        /* Clone ICC ansible/docker git repositories */
        withCredentials([string(credentialsId: env.GITHUB_TOKEN_CRED_ID, variable: "TOKEN")]) {

            sh "git clone -b ${ANSIBLE_CLONE_BRANCH} https://${TOKEN}@${GITHUB_URL}/${GITHUB_OWNER}/${ANSIBLE_REPO}.git";
        }

        /* Needed for ansible plays to recognize configuration file */
        env.ANSIBLE_CONFIG = "${WORKSPACE}/${ANSIBLE_REPO}/ansible.cfg";

        /* Must be called after the environment is all set up */
        slackBuildStart();
    }
}