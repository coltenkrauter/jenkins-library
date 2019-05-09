def call() {
    postSlackText("Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started...");

    return new Date();
}
