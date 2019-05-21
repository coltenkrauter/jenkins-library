import components.Config;

def call(String... args) {
    stage("Initialize") {
        /* Sends build started message */
        slackPipelineStart();

        /* Clears the Jenkins WORKSPACE directory */
        clean();

        def config = new Config();
        env.GITHUB_URL = config.get("GITHUB_URL");
        env.GITHUB_OWNER = config.get("GITHUB_OWNER");

        /* Formulate the docker repo name from the jenkins job name 
           TODO: A better way to get the git repo name? */
        env.GIT_REPO_NAME = env.JOB_NAME.split("/")[1];
        env.SOURCE_CODE_DIR = "${GIT_REPO_NAME}-${BUILD_NUMBER}";
        env.SOURCE_CODE = "${WORKSPACE}/${SOURCE_CODE_DIR}";

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


        /* Clone ICC ansible/docker git repositories */
        withCredentials([string(credentialsId: config.get("GITHUB_TOKEN_CRED_ID"), variable: "TOKEN")]) {
            for (String ARG : args) {
                sh "git clone -b master https://${TOKEN}@${GITHUB_URL.replace('https://', '')}/${GITHUB_OWNER}/${ARG}.git";

                if (ARG == "ansible") {
                    env.ANSIBLE_CONFIG = "${WORKSPACE}/ansible/ansible.cfg";
                }
            }
        }

        /* Must be called after the environment is all set up */
        slackBuildStart();
    }
}
