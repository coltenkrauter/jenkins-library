import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    
    echo("About to send slack post");
    withCredentials([string(credentialsId: "slack-token", variable: 'TOKEN')]) {
        slack.postMessageAPI(TOKEN);
    }
    
    echo("Sent slack post");
}
