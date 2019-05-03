import components.GitHub

def call() {
    def gitHub = new GitHub(this);
    def commitVars = checkout scm;
    gitHub.set_git_vars(commitVars);

    return new Date();
}
