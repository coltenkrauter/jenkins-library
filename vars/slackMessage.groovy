import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    
    echo("About to send slack post");
    slack.post();
    echo("Sent slack post");
}
