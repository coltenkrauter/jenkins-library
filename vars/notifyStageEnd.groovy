import components.Slack

def call(STAGENAME, START_DATE, END_DATE) {
    def slack = new Slack(this);
    DURATION = slack.getDurationString(START_DATE, END_DATE);
    postMessage("Stage *${STAGENAME}* finished in ${DURATION}");

    return new Date();
}
