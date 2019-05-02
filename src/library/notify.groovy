package library

import groovy.json.JsonOutput
import java.time.Instant;

class Globals {
   static String threadId = ""
   static Object githubData = []
   static stages = [:]
   static common = null
   static config = null
   static buildStart = null
   static success = true
}

def init(git_vars, buildStart) {
    // Load config file
    // Note: readYaml requires the "Pipeline Utility Steps" plugin
    Globals.config = readYaml file: "jenkins/config.yaml"
    
    Globals.common = new common()

    // Get GitHub commit payload
    Globals.githubData = Globals.common.get_git_payload(git_vars, Globals.config)
    Globals.buildStart = buildStart;

    
}

def slack(message, status) {
    try {
        // Return if mute is true
        if (Globals.config.MUTE_SLACK) {
            echo("Slack messages have been muted")
            return
        }
        
        if (Globals.threadId == "" && status != "START") {
            echo("You must use notify.start() first.")
            return
        }

        channel = Globals.config.BUILD_LOG_SLACK_CHANNEL
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
                    color: Globals.config.PROJECT_COLOR,
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
                    color: Globals.config.PROJECT_COLOR,
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
            Globals.threadId = slackSend(channel: channel, attachments: JsonOutput.toJson(attachment)).threadId

            // Send first thread message
            update("Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started")
        } else if (status == "UPDATE") {
            slackSend(channel: Globals.threadId, message: message)
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

            slackSend(channel: Globals.threadId, replyBroadcast: true, attachments: JsonOutput.toJson(attachment))
        } else if (status == "END") {
            color = "good"
            if (!Globals.success)
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
                            value: Globals.common.get_duration_string(Globals.buildStart, new Date())
                        ]
                    ],
                ]
            ]

            slackSend(channel: Globals.threadId, attachments: JsonOutput.toJson(attachment))
        }
    } catch (err) {
        echo "notify.slack() failed: ${err}"
        throw err
    }
}

def buildStart() {
    message = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> started"
    slack(message, "START")
}

def build_end() {
    messages = "Build <${env.RUN_DISPLAY_URL}|#${env.BUILD_NUMBER}> failed"
        if (Globals.success)
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
    duration = Globals.common.get_duration_string(start_date, end_date)
    message = "Stage \"$stage\" finished in $duration"
    return update(message)
}

def error(message) {
    Globals.success = false
    slack(message, "ERROR")
}

return this
