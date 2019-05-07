import components.Slack

def call() {
    echo("Start error");
    env.SUCCESS = "false";

    echo(env.SUCCESS);

    if (env.GIT_REPO_NAME) {
        def slack = new Slack(this);

        attachment = [
            [
                color: "danger",
                fields: [
                    [
                        value: "@here, *<${env.GIT_REPO_URL}|${env.GIT_REPO_NAME}>/<${env.GIT_BRANCH_URL}|${env.GIT_BRANCH_NAME}>* - build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed :face_with_monocle:"
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

        // Post message in Slack thread and broadcast to channel
        slack.postAttachment(env.BUILD_LOG_SLACK_THREAD, attachment);
        } else {
            slackSend(message: "@here, build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed :face_with_monocle:", color: "danger");
        }

    echo(env.SUCCESS);
    echo("End error");
    // Return build start date
    return new Date(env.BUILD_START);
}
