def call() {
    /* Yellow */
    color = "#f4e004";

    footer = "<${RUN_DISPLAY_URL}|#${BUILD_NUMBER}> triggered by ${BUILD_TRIGGER_USER}"
    if(BUILD_NUMBER == "1") {
        footer = "<${RUN_DISPLAY_URL}|First build!> :right-facing_fist::left-facing_fist:"
    }

    return [
        [
            color: color,
            fallback: "${GIT_BRANCH_NAME} execution #${BUILD_NUMBER}",
            fields: [
                [
                    title: "Repository",
                    value: "<${GIT_REPO_URL}|${GIT_REPO_NAME}>",
                    short: true
                ],
                [
                    title: "Branch",
                    value: "<${GIT_BRANCH_URL}|${GIT_BRANCH_NAME}>",
                    short: true
                ],
                [
                    title: "Commiter",
                    value: GIT_COMMITTER_NAME,
                    short: true
                ]
            ],
            footer: footer,
            ts: (new Date(BUILD_START)).getTime() / 1000
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
