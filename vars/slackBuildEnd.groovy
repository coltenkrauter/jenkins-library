import components.Slack;

def call() {
    def slack = new Slack(this);
    DURATION = slack.getDurationString(new Date(BUILD_START), new Date());

    /* Red */
    color = "#e84118";
    slackMessage = "Build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> failed";
    slackMessageFooter = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER} failed :face_with_monocle:";
    logMessage = "Build #${BUILD_NUMBER} failed";

    if (GIT_BRANCH_NAME && GIT_REPO_NAME) {
        if (SUCCESS && SUCCESS == "true") {
            /* Green */
            color = "#2ecc71";
            slackMessage = "Build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> finished successfully";
            slackMessageFooter = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER} finished successfully";
            logMessage = "Build #${BUILD_NUMBER} finished successfully";
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
