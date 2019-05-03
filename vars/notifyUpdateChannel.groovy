import components.Slack

def call(MESSAGE) {
    def slack = new Slack(this);
    
    slack.postMessage(env.BUILD_LOG_SLACK_CHANNEL, MESSAGE);

    return new Date();
}
