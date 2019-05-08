def call(err) {
    env.SUCCESS = "false";

    /* Truncate error message to 100 chars*/
    errMessage = err.toString();
    if (errMessage.length() > 100)
        errMessage = errMessage.take(100) + "...";

    if (env.GIT_BRANCH_NAME && env.GIT_REPO_NAME) {
        attachment = [
            [
                color: "danger",
                fields: [
                    [
                        title: "Error",
                        value: errMessage
                    ]
                ],
                markdown: ["pretext"]
            ],
            [
                color: "danger",
                fallback: "",
                actions: [
                    [
                        type: "button",
                        text: "Jenkins",
                        url: env.RUN_DISPLAY_URL
                    ]
                ]
            ]
        ];
    } else {
        attachment = [
            [
                color: "danger",
                fields: [
                    [
                        title: "Message",
                        value: "@here, build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed :face_with_monocle:"
                    ],
                    [
                        title: "Error",
                        value: errMessage
                    ]
                ],
                markdown: ["pretext"]
            ],
            [
                color: "danger",
                fallback: "",
                actions: [
                    [
                        type: "button",
                        text: "Jenkins",
                        url: env.RUN_DISPLAY_URL
                    ]
                ]
            ]
        ];
    }

    /* Post message in Slack thread and broadcast to channel */
    postAttachmentInThread(attachment);

    echo("Pipeline Failed: ${err}");

    // Return build start date
    return new Date(env.BUILD_START);
}
