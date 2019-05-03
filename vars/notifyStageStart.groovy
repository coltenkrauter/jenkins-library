import components.Slack

def call() {
    def slack = new Slack(this);
    slack.echo("Heyyy");
}

def call("test") {
    def slack = new Slack(this);
    slack.echo("Heyyy");
}
