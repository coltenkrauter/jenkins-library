import components.Slack

def call(STAGENAME, START_DATE, END_DATE) {
    def slack = new Slack(this);
    DURATION = slack.get_duration_string(START_DATE, END_DATE);
    slack.postMessage(env.BUILD_LOG_SLACK_THREAD, "*[${STAGENAME}]* finished in ${DURATION}");

    return new Date();
}
