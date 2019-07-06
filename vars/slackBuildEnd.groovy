import components.Slack;

def call() {
    def slack = new Slack(this);
    DURATION = slack.getDurationString(new Date(BUILD_START), new Date());

    /* Red */
    color = "#db1412";
    message = "Build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> failed";
    footer = "<${RUN_DISPLAY_URL}|First build> failed :face_with_monocle:";
    logMessage = "Build #${BUILD_NUMBER} failed";

    if(env.BUILD_TRIGGER_USER) {
        if(BUILD_NUMBER != "1") {
            footer = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER} failed :face_with_monocle:";
        }
    } 

    if (env.GIT_BRANCH_NAME && env.GIT_REPO_NAME && env.BUILD_TRIGGER_USER && (!env.NO_BUILD || env.NO_BUILD == "false")) {
        if (SUCCESS && SUCCESS == "true") {
            /* Green */
            color = "#2eb885";
            message = "Build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> finished successfully";
            footer = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER} finished successfully";
            if(BUILD_NUMBER == "1") {
                footer = "<${RUN_DISPLAY_URL}|First build> finished successfully :right-facing_fist::left-facing_fist:";
            }

            logMessage = "Build #${BUILD_NUMBER} finished successfully";
        }

        attachments = [
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

        postSlackAttachment(attachments);

        buildStartAttachment = getBuildStartAttachment();
        buildStartAttachment[0].color = color;
        buildStartAttachment[0].footer = footer;
        buildStartAttachment[0].fields.push(
            [
                title: "Duration",
                value: DURATION,
                short: true
            ]
        )
        
        buildStartAttachment[1].color = color;
        modifyFirstPost(buildStartAttachment);
    } else if (env.NO_BUILD && env.NO_BUILD == "true" && env.BUILD_TRIGGER_USER) {

        /* Blue */
        color = "#4593ff";
        footer = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER} has been skipped";
        logMessage = "Build #${BUILD_NUMBER} has been skipped";

        buildStartAttachment = getBuildStartAttachment();
        buildStartAttachment[0].color = color;
        buildStartAttachment[0].footer = footer;
        buildStartAttachment[0].fields.push(
            [
                title: "Duration",
                value: DURATION,
                short: true
            ]
        )

        buildStartAttachment[1].color = color;
        modifyFirstPost(buildStartAttachment);
    }
    
    echo(logMessage);

    return new Date();
}
