import components.Slack

def call(ATTACHMENT) {
    def slack = new Slack(this);
    
    withCredentials([string(credentialsId: "slack-token", variable: "TOKEN")]) {
        slack.modifyFirstPost(TOKEN, ATTACHMENT);
    }
}
