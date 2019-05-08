import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    
    echo("About to send slack post");
    slack.postMessage();
    echo("Sent slack post");
}
