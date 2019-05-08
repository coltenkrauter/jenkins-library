import components.Slack

def call(CHANNEL, ATTACHMENT) {
    def slack = new Slack(this);
    
    withCredentials([string(credentialsId: "slack-token", variable: "TOKEN")]) {
        slack.postAttachment(TOKEN, CHANNEL, ATTACHMENT);
    }
}
