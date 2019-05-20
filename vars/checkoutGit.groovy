import components.GitHub;

def call() {
    def gitHub = new GitHub(this);
    def commitVars = checkout scm;

    withCredentials([string(credentialsId: GITHUB_TOKEN_CRED_ID, variable: 'TOKEN')]) {
        gitHub.setEnvVars(commitVars, TOKEN);
    }

    return new Date();
}
