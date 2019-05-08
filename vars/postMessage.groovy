import components.Slack

def call(MESSAGE) {
    def slack = new Slack(this);
    
    withCredentials([string(credentialsId: "slack-token", variable: "TOKEN")]) {
        slack.postMessage(TOKEN, MESSAGE);
    }
}
