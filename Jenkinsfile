#!/usr/bin/env groovy
/* This is an example Jenkinsfile to show how the jenkins-library may be used */

@Library("jenkins-library@development")

/* Import Environment in order to access env */
import hudson.model.Environment;

newMain(this.&main);

def main() {
    newStage("Awesome first stage", this.&awesomeFirstStage);
    newStage("Wonderful final stage", this.&wonderfulFinalStage);
}

def awesomeFirstStage() {
    echo("This is where I do stuff.");

    slackUpdate("Deploying the stack");
}

def wonderfulFinalStage() {
    echo("Happy day - it is all working as expected.");

    slackUpdate("About ready to eat :pizza:.");
}
