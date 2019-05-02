package library

import groovy.json.JsonSlurperClassic

def set_git_vars(gitVars) {
    try {

        env.GITHUB_API_URL = "https://api.github.com"
        env.GITHUB_TOKEN_CRED_ID = "build-server-github-integration"
        env.GITHUB_URL = "https://github.com"

        withCredentials([string(credentialsId: GITHUB_TOKEN_CRED_ID, variable: 'token')]) {
            env.GIT_COMMIT = gitVars.GIT_COMMIT
            git_url_tokens = gitVars.GIT_URL.replace('.git', '').replace('https://', '').split('/')
            env.GIT_OWNER = git_url_tokens[1]
            env.GIT_REPO_NAME = git_url_tokens[2]

            response = httpRequest (
                consoleLogResponseBody: false, 
                contentType: 'APPLICATION_JSON', 
                httpMode: 'GET', 
                url: "${env.GITHUB_API_URL}/repos/${env.GIT_OWNER}/${env.GIT_REPO_NAME}/commits/${env.GIT_COMMIT}?access_token=$token", 
                validResponseCodes: '200'
            )

            def json = []
            json = new JsonSlurperClassic().parseText(response.content)

            env.GIT_COMMITTER_NAME = json.commit.author.name
            env.GIT_COMMITTER_EMAIL = json.commit.author.email
            env.GIT_COMMITTER_USERNAME = env.GIT_COMMITTER_EMAIL.split("@")[0]
            env.GIT_BRANCH_NAME = env.BRANCH_NAME
            env.GIT_REPO_URL = "${env.GITHUB_URL}/${env.GIT_OWNER}/${env.GIT_REPO_NAME}"
            env.GIT_BRANCH_URL = "${env.GIT_REPO_URL}/tree/${env.BRANCH_NAME}"
            env.GIT_COMMIT_SHORT = env.GIT_COMMIT.take(7)
            env.GIT_COMMIT_URL = "${env.GIT_REPO_URL}/commit/${env.GIT_COMMIT}}"

            echo sh(script: 'env|sort', returnStdout: true)
            echo "Made it here"
        }
    } catch (err) {
        echo "common.groovy() failed: ${err}"
        throw err
    }
}

return this
