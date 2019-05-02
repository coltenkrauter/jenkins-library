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
        
        

        if (status == "START") {
            attachment = [
                [
                    color: Globals.config.PROJECT_COLOR,
                    fallback: "$branch_name execution #$build_number",
                    fields: [
                        [
                            title: "Build",
                            value: "<$build_url|#$build_number>",
                            short: true
                        ],
                        [
                            title: "Commiter",
                            value: name,
                            short: true
                        ],
                        [
                            title: "Repository",
                            value: "<$repo_url|$repo_name>",
                            short: true
                        ],
                        [
                            title: "Branch",
                            value: "<$branch_url|$branch_name>",
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
                            url: build_url
                        ],
                        [
                            type: "button",
                            text: "Commit",
                            url: commit_url
                        ],
                        [
                            type: "button",
                            text: "Changes",
                            url: changes_url
                        ]
                    ]
                ]
            ]

            // Send start slack message to channel
            Globals.threadId = slackSend(channel: channel, attachments: JsonOutput.toJson(attachment)).threadId

            // Send first thread message
            update("Build <$build_url|#$build_number> started")
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
                            value: "@here, *<$repo_url|$repo_name>/<$branch_url|$branch_name>* - <$build_url|build #$build_number> failed :face_with_monocle:"
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
                            url: build_url
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
    message = "Build <$build_url|#$build_number> started"
    slack(message, "START")
}

def build_end() {
    messages = "Build <$build_url|#$build_number> failed"
        if (Globals.success)
        // Send last slack message
        message = "Build <$build_url|#$build_number> finished successfully"
    
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
