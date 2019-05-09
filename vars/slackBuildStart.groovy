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

    buildStartAttachment = getBuildStartAttachment();
    
    if (!env.BUILD_LOG_SLACK_CHANNEL_ID) {
        // Post message to Slack
        postSlackAttachment(buildStartAttachment);
    } else {
        modifyFirstPost(buildStartAttachment);    
    }
    
    // Post message in Slack thread
    postMessage("Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started");

    // Return build start date
    return new Date(env.BUILD_START);
}
