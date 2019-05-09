import components.Slack

def call() {
    def slack = new Slack(this);
    DURATION = slack.getDurationString(new Date(env.BUILD_START), new Date());

    /* Red */
    color = "#e84118";
    slackMessage = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed";
    slackMessageFooter = "<${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> triggered by ${env.BUILD_TRIGGER_USER} failed :face_with_monocle:";
    logMessage = "Build ${env.BUILD_NUMBER} failed";

    if (env.GIT_BRANCH_NAME && env.GIT_REPO_NAME) {
        if (env.SUCCESS && env.SUCCESS == "true") {
            /* Green */
            color = "#2ecc71";
            slackMessage = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> finished successfully";
            slackMessageFooter = "<${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> triggered by ${env.BUILD_TRIGGER_USER} finished successfully";
            logMessage = "Build ${env.BUILD_NUMBER} finished successfully";
        }

        attachments = [
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

        postSlackAttachment(attachments);

        buildStartAttachment = getBuildStartAttachment();
        buildStartAttachment[0].color = color;
        buildStartAttachment[0].footer = slackMessageFooter;
        buildStartAttachment[1].color = color;

        modifyFirstPost(buildStartAttachment);
    }
    
    echo(logMessage);

    return new Date();
}
