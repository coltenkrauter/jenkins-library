import components.GitHub

def call() {
    def gitHub = new GitHub(this);
    def commitVars = checkout scm;

    pipeline.env.GITHUB_API_URL = "https://api.github.com";
    pipeline.env.GITHUB_TOKEN_CRED_ID = "build-server-github-integration";
    pipeline.env.GITHUB_URL = "https://github.com";

    withCredentials([string(credentialsId: env.GITHUB_TOKEN_CRED_ID, variable: 'TOKEN')]) {
    	gitHub.setEnvVars(commitVars, TOKEN);
    }

    return new Date();
}
