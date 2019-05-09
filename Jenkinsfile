#!/usr/bin/env groovy

/* This is an example Jenkinsfile to show how the jenkins-library may be used */
@Library("jenkins-library@master")

import hudson.model.*;

env.BUILD_START = new Date();
env.SUCCESS = "true";

node {    
    main();
}

def main() {
    try {
        /* Post a message to Slack which will be overwritten when slackBuildStart() is executed */
        slackPipelineStart();
        
        /* Checkout SCM and set up environment (set env variables) */
        checkoutGit();
        /* Must be called after the environment is all set up */
        slackBuildStart();

        newStage("Awesome first stage", this.&awesomeFirstStage);
        newStage("Wonderful final stage", this.&wonderfulFinalStage);

    } catch (err) {
        /* Notify slack with error message */
        slackError(err);
        throw(err);

    } catch(Throwable err) {
        /* Notify slack with error message */
        slackError(err);
        throw(err);
        
    } finally {
        /* Notify slack */
        slackBuildEnd();
    }
}

/* Wrap stage functions with the Slack notifications*/
def newStage(stageName, Closure stageFunction) {
    stage(stageName) {
        startTime = slackStageStart(stageName);
        stageFunction();
        slackStageEnd(stageName, startTime, new Date());
    }
}

def awesomeFirstStage() {
    echo("This is where I do stuff.");

    slackUpdate("Deploying the stack");
}

def wonderfulFinalStage() {
    echo("Happy day - it is all working as expected.");

    slackUpdate("About ready to eat :pizza:.");
}
