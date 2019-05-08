def call() {
    build_user = env.GIT_COMMITTER_NAME;
    wrap([$class: 'BuildUser']) {
        if (env.BUILD_USER) {
            build_user = BUILD_USER;
        }
    }
    env.BUILD_TRIGGER_USER = build_user;

    attachment = [
        [
            color: env.PROJECT_COLOR,
            fallback: "${env.GIT_BRANCH_NAME} execution #${env.BUILD_NUMBER}",
            fields: [
                [
                    title: "Build",
                    value: "<${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}>",
                    short: true
                ],
                [
                    title: "Commiter",
                    value: env.GIT_COMMITTER_NAME,
                    short: true
                ],
                [
                    title: "Repository",
                    value: "<${env.GIT_REPO_URL}|${env.GIT_REPO_NAME}>",
                    short: true
                ],
                [
                    title: "Branch",
                    value: "<${env.GIT_BRANCH_URL}|${env.GIT_BRANCH_NAME}>",
                    short: true
                ]
            ],
            footer: "<${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> triggered by ${env.BUILD_TRIGGER_USER}",
            ts: (new Date(env.BUILD_START)).getTime() / 1000
        ],
        [
            color: env.PROJECT_COLOR,
            fallback: "",
            actions: [
                [
                    type: "button",
                    text: "Jenkins",
                    url: env.RUN_DISPLAY_URL
                ],
                [
                    type: "button",
                    text: "Commit",
                    url: env.GIT_COMMIT_URL
                ],
                [
                    type: "button",
                    text: "Changes",
                    url: env.RUN_CHANGES_DISPLAY_URL
                ]
            ]
        ]
    ];

    // Post message to Slack
    postAttachment(env.BUILD_LOG_SLACK_CHANNEL, attachment);

    // Post message in Slack thread
    postMessage(env.BUILD_LOG_SLACK_THREAD, "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started");

    // Return build start date
    return new Date(env.BUILD_START);
}
