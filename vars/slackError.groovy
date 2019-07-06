def call(err) {
    env.SUCCESS = "false";

    /* Red */
    color = "#db1412";

    /* Truncate error message to 100 chars*/
    errMessage = err.toString();
    if (errMessage.length() > 100)
        errMessage = errMessage.take(100) + "...";

    if (env.GIT_BRANCH_NAME && env.GIT_REPO_NAME) {
        attachment = [
            [
                color: color,
                fields: [
                    [
                        title: "Error",
                        value: errMessage
                    ]
                ],
                markdown: ["pretext"]
            ],
            [
                color: color,
                fallback: "",
                actions: [
                    [
                        type: "button",
                        text: "Jenkins",
                        url: RUN_DISPLAY_URL
                    ]
                ]
            ]
        ];
    } else {
        attachment = [
            [
                color: color,
                fields: [
                    [
                        title: "Message",
                        value: "@here, build <${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> failed :face_with_monocle:"
                    ],
                    [
                        title: "Error",
                        value: errMessage
                    ]
                ],
                markdown: ["pretext"]
            ],
            [
                color: color,
                fallback: "",
                actions: [
                    [
                        type: "button",
                        text: "Jenkins",
                        url: RUN_DISPLAY_URL
                    ]
                ]
            ]
        ];
    }

    /* Post message in Slack thread and broadcast to channel */
    postSlackAttachment(attachment);

    echo("Pipeline Failed: ${err}");

    // Return build start date
    return new Date(BUILD_START);
}
