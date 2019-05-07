import components.Slack

def call(err) {
    def slack = new Slack(this);

    env.SUCCESS = "false";

    /* Truncate error message to 100 chars*/
    errMessage = err.toString();
    if (errMessage.length() > 100)
        errMessage = errMessage.take(100) + "...";

    if (env.GIT_REPO_NAME) {
        attachment = [
            [
                color: "danger",
                fields: [
                    [
                        title: "Message",
                        value: "@here, *<${env.GIT_REPO_URL}|${env.GIT_REPO_NAME}>/<${env.GIT_BRANCH_URL}|${env.GIT_BRANCH_NAME}>* - <${env.RUN_DISPLAY_URL}|build #${env.BUILD_NUMBER}> failed :face_with_monocle:",
                        short: true
                    ],
                    [
                        title: "Error",
                        value: errMessage,
                        short: true
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
                        value: "@here, <${env.RUN_DISPLAY_URL}|build #${env.BUILD_NUMBER}> failed :face_with_monocle:",
                        short: true
                    ],
                    [
                        title: "Error",
                        value: errMessage,
                        short: true
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
    env.BUILD_LOG_SLACK_THREAD = slack.postAttachment(env.BUILD_LOG_SLACK_THREAD, attachment);

    echo("Pipeline Failed: ${err}");
    throw(err);

    // Return build start date
    return new Date(env.BUILD_START);
}
