import components.GitHub

def call() {
    def gitHub = new GitHub(this);
    def commitVars = checkout scm;
    gitHub.setEnvVars(commitVars);

    return new Date();
}
