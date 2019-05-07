import components.Slack

def call(STAGENAME) {
    def slack = new Slack(this);
    DURATION = slack.get_duration_string(new Date(env.BUILD_START), new Date());

    color = "danger";
    message = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed";
    
    if (env.SUCCESS && env.SUCCESS == "true") {
    	color = "good";
        message = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> finished successfully";
    }

    attachment = [
        [
            color: color,
            fields: [
                [
                    title: "Message",
                    value: message
                ],
                [
                    title: "Duration",
                    value: DURATION
                ]
            ],
        ]
    ];

    slack.postAttachment(env.BUILD_LOG_SLACK_THREAD, attachment);

    return new Date();
}
