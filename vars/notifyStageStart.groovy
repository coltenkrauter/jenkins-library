import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    
    slack.postMessage(env.BUILD_LOG_SLACK_THREAD, "*[${STAGENAME}]* started");

    return new Date();
}
