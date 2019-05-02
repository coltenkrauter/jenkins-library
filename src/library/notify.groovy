package library

import groovy.json.JsonOutput
import java.time.Instant;

env.BUILD_LOG_SLACK_THREAD = ""

def slack(message, status) {
    env.BUILD_LOG_SLACK_CHANNEL = "build-log"

    try {
        if (env.BUILD_LOG_SLACK_THREAD == "" && status != "START") {
            echo("You must use notify.start() first.")
            return
        }

        channel = env.BUILD_LOG_SLACK_CHANNEL
        build_user = env.GIT_COMMITTER_NAME

        try {
            wrap([$class: 'BuildUser']) {
              build_user = BUILD_USER
            //  build_user_id = BUILD_USER_ID
            //  build_user_email = BUILD_USER_EMAIL
            }
        } catch (err) {

        } finally {

        }


        if (status == "START") {
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
                    ]
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
            ]

            // Send start slack message to channel
            env.BUILD_LOG_SLACK_THREAD = slackSend(channel: channel, attachments: JsonOutput.toJson(attachment)).threadId

            // Send first thread message
            update("Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started")
        } else if (status == "UPDATE") {
            slackSend(channel: env.BUILD_LOG_SLACK_THREAD, message: message)
            echo message
            return new Date()

        } else if (status == "ERROR") {
            attachment = [
                [
                    color: "danger",
                    fields: [
                        [
                            value: "@here, *<${env.GIT_REPO_URL}|${env.GIT_REPO_NAME}>/<${env.GIT_BRANCH_URL}|${env.GIT_BRANCH_NAME}>* - <${env.RUN_DISPLAY_URL}|build #${env.BUILD_NUMBER}> failed :face_with_monocle:"
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
            ]

            slackSend(channel: env.BUILD_LOG_SLACK_THREAD, replyBroadcast: true, attachments: JsonOutput.toJson(attachment))
        } else if (status == "END") {
            color = "good"
            if (!env.SUCCESS)
                color = "danger"

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
                            value: get_duration_string(new Date(env.BUILD_START), new Date())
                        ]
                    ],
                ]
            ]

            slackSend(channel: env.BUILD_LOG_SLACK_THREAD, attachments: JsonOutput.toJson(attachment))
        }
    } catch (err) {
        echo "notify.slack() failed: ${err}"
        throw err
    }
}

def buildStart() {
    message = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started"
    slack(message, "START")

    return new Date(env.BUILD_START)
}

def build_end() {
    messages = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed"
        if (env.SUCCESS)
        // Send last slack message
        message = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> finished successfully"
    
    slack(message, "END")
}

def stage_start(stage) {
    return update("Stage \"$stage\" started")
}

def update(message) {
    return slack(message, "UPDATE")
}

def stage_end(stage, start_date) {
    return stage_end(stage, start_date, new Date())
}

def stage_end(stage, start_date, end_date) {
    duration = get_duration_string(start_date, end_date)
    message = "Stage \"$stage\" finished in $duration"
    return update(message)
}

def error(message) {
    env.SUCCESS = false
    slack(message, "ERROR")
}


def get_duration(start, end) {
    def duration = groovy.time.TimeCategory.minus(
      end,
      start
    )

    values = [
        "seconds" : duration.seconds,
        "minutes" : duration.minutes,
        "hours" : duration.hours,
        "days" : duration.days,
        "ago" : duration.ago,
    ]

    return values
}

def get_duration_string(start, end) {
    values = get_duration(start, end)

    seconds = values["seconds"]
    message = "$seconds second" + plural(seconds)

    minutes = values["minutes"]
    if (minutes)
        message = "$minutes minute" + plural(minutes) + ", $message"

    hours = values["hours"]
    if (hours)
        message = "$hours hour" + plural(hours) + ", $message"

    days = values["days"]
    if (days)
        message = "$days day" + plural(days) + ", $message"

    message = message.replace(", 0 seconds", "")

    if (message == "0 seconds") {
        message = "<1 second"
    }

    return message
}

def plural(value) {
    return (value.toInteger() == 0 || value.toInteger() > 1) ? "s" : ""
}

return this
