def call() {
    
    env.GIT_REPO_NAME = JOB_NAME.split("/")[1];
    
    postSlackText("Build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> of *${GIT_REPO_NAME}/${BRANCH_NAME}* has started");

    return new Date();
}
