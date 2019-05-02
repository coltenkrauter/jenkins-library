package library

import groovy.json.JsonSlurperClassic

def get_git_payload(gitVars, config) {
    env.GITIT_TEST = "This is a test"
    try {
        GITHUB_URL = config.GITHUB_URL
        GITHUB_API_URL = config.GITHUB_API_URL
        GITHUB_TOKEN_CRED_ID = config.GITHUB_TOKEN_CRED_ID

        branch = env.BRANCH_NAME

        echo(GITHUB_TOKEN_CRED_ID);
        withCredentials([string(credentialsId: GITHUB_TOKEN_CRED_ID, variable: 'token')]) {
            git_commit = gitVars.GIT_COMMIT
            git_url_tokens = gitVars.GIT_URL.replace('.git', '').replace('https://', '').split('/')
            git_owner = git_url_tokens[1]
            git_repo = git_url_tokens[2]

            response = httpRequest (
                consoleLogResponseBody: false, 
                contentType: 'APPLICATION_JSON', 
                httpMode: 'GET', 
                url: "$GITHUB_API_URL/repos/$git_owner/$git_repo/commits/$git_commit?access_token=$token", 
                validResponseCodes: '200'
            )

            def json = []
            json = new JsonSlurperClassic().parseText(response.content)

            echo json

            env.GIT_REPO_NAME = "${git_repo}"
            env.GIT_REPO_URL = "$GITHUB_URL/$git_owner/$git_repo"
            env.GIT_BRANCH_NAME = "${branch}"
            env.GIT_BRANCH_URL = "$GITHUB_URL/$git_owner/$git_repo/tree/$branch"
            env.GIT_COMMIT_NAME = "${git_commit.take(7)}"
            env.GIT_COMMIT_URL = "$GITHUB_URL/$git_owner/$git_repo/commit/$git_commit"
            env.GIT_OWNER = "${git_owner}"
        }
    } catch (err) {
        echo "common.groovy() failed: ${err}"
        throw err
    }
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
