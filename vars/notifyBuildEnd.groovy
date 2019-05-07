import components.Slack

def call() {
    def slack = new Slack(this);
    DURATION = slack.get_duration_string(new Date(env.BUILD_START), new Date());

    color = "danger";
    slackMessage = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed";
    logMessage = "Build ${env.BUILD_NUMBER} failed";

    if (env.SUCCESS && env.SUCCESS == "true") {
        color = "good";
        slackMessage = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> finished successfully";
        logMessage = "Build ${env.BUILD_NUMBER} finished successfully";
    }

    attachment = [
        [
            color: color,
            fields: [
                [
                    title: "Message",
                    value: slackMessage
                ],
                [
                    title: "Duration",
                    value: DURATION
                ]
            ],
        ]
    ];

    slack.postAttachment(env.BUILD_LOG_SLACK_THREAD, attachment);
    echo(logMessage);

    return new Date();
}
