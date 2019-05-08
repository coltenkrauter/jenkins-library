import components.Slack

def call() {
    /* Instantiating Slack in order to set some environment vars */
    def slack = new Slack(this);

    build_user = env.GIT_COMMITTER_NAME;
    wrap([$class: 'BuildUser']) {
        if (env.BUILD_USER) {
            build_user = BUILD_USER;
        }
    }
    env.BUILD_TRIGGER_USER = build_user;

    attachment = getBuildStartMessage();

    // Post message to Slack
    postAttachment(attachment);

    // Post message in Slack thread
    postMessageInThread("Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started");

    // Return build start date
    return new Date(env.BUILD_START);
}
