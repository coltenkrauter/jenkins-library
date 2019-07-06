import components.Slack;

def call() {
    /* Instantiating Slack in order to set some environment vars */
    def slack = new Slack(this);

    build_user = GIT_COMMITTER_NAME;
    wrap([$class: 'BuildUser']) {
        if (env.BUILD_USER) {
            build_user = BUILD_USER;
        }
    }
    env.BUILD_TRIGGER_USER = build_user;

    buildStartAttachment = getBuildStartAttachment();
    
    if (!BUILD_LOG_SLACK_CHANNEL_ID) {
        // Post message to Slack
        postSlackAttachment(buildStartAttachment);
    } else {
        modifyFirstPost(buildStartAttachment);    
    }
    
    // Return build start date
    return new Date(BUILD_START);
}
