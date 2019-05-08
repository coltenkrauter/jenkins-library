import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    
    echo("About to send slack post");
    withCredentials([string(credentialsId: "slack-token", variable: 'TOKEN')]) {
        slack.postMessage(TOKEN);
    }
    
    echo("Sent slack post");
}
