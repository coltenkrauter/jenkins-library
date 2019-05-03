package components;

import groovy.json.JsonSlurperClassic;

class GitHub {
    def pipeline;
    def debug = true;

    // Constructor
    GitHub(pipeline) {
        this.pipeline = pipeline;

        if (!pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED) {
            // This will only be executed the first time Slack class is instantiated thanks to setting this env variable
            pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED = "true";
            pipeline.env.BUILD_LOG_SLACK_CHANNEL = "build-log";
            pipeline.env.BUILD_LOG_SLACK_THREAD = "";
            pipeline.env.SUCCESS = "true";
        }
    }
    
    def setEnvVars(commitVars) {
        try {
            pipeline.env.GITHUB_API_URL = "https://api.github.com";
            pipeline.env.GITHUB_TOKEN_CRED_ID = "build-server-github-integration";
            pipeline.env.GITHUB_URL = "https://github.com";

            pipeline.withCredentials([pipeline.string(credentialsId: pipeline.env.GITHUB_TOKEN_CRED_ID, variable: 'token')]) {
                pipeline.env.GIT_COMMIT = commitVars.GIT_COMMIT
                def tokens = commitVars.GIT_URL.replace('.git', '').replace('https://', '').split('/')
                pipeline.env.GIT_OWNER = tokens[1]
                pipeline.env.GIT_REPO_NAME = tokens[2]

                response = httpRequest (
                    consoleLogResponseBody: false, 
                    contentType: 'APPLICATION_JSON', 
                    httpMode: 'GET', 
                    url: "${pipeline.env.GITHUB_API_URL}/repos/${pipeline.env.GIT_OWNER}/${pipeline.env.GIT_REPO_NAME}/commits/${pipeline.env.GIT_COMMIT}?access_token=$token", 
                    validResponseCodes: '200'
                )

                def json = []
                json = new JsonSlurperClassic().parseText(response.content)

                pipeline.env.GIT_COMMITTER_NAME = json.commit.author.name
                pipeline.env.GIT_COMMITTER_EMAIL = json.commit.author.email
                pipeline.env.GIT_COMMITTER_USERNAME = pipeline.env.GIT_COMMITTER_EMAIL.split("@")[0]
                pipeline.env.GIT_BRANCH_NAME = pipeline.env.BRANCH_NAME
                pipeline.env.GIT_REPO_URL = "${pipeline.env.GITHUB_URL}/${pipeline.env.GIT_OWNER}/${pipeline.env.GIT_REPO_NAME}"
                pipeline.env.GIT_BRANCH_URL = "${pipeline.env.GIT_REPO_URL}/tree/${pipeline.env.BRANCH_NAME}"
                pipeline.env.GIT_COMMIT_SHORT = pipeline.env.GIT_COMMIT.take(7)
                pipeline.env.GIT_COMMIT_URL = "${pipeline.env.GIT_REPO_URL}/commit/${pipeline.env.GIT_COMMIT}}"
            }
        } catch (err) {
            echo "common.groovy() failed: ${err}"
            throw err
        }
    }
    def echo(String message) {
        pipeline.echo(message);
    }

    def debug(String message) {
        if (debug) {
            echo(message);
        }
    }
}
