import components.Slack;

def call(STAGE_NAME, START_DATE, END_DATE) {
    def slack = new Slack(this);
    DURATION = slack.getDurationString(START_DATE, END_DATE);
    postSlackText("Stage *${STAGE_NAME}* finished in ${DURATION}");

    return new Date();
}
