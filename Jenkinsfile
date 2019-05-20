#!/usr/bin/env groovy
/* This is an example Jenkinsfile to show how the jenkins-library may be used */

@Library("jenkins-library@stable")

/* Import Environment in order to access env */
import hudson.model.Environment;

newMain(this.&main);

def main() {
    /* Post a message to Slack which will be overwritten when slackBuildStart() is executed */
    slackPipelineStart();
    
    /* Checkout SCM and set up environment (set env variables) */
    checkoutGit();
    
    /* Must be called after the environment is all set up */
    slackBuildStart();

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
