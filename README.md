# Jenkins Library

## Setup
These instructions are intended to assist in setting up a jenkins shared library.

1. Create a GitHub repository per [Jenkins Guidelines](https://jenkins.io/doc/book/pipeline/shared-libraries/) regarding shared libraries
    - Currently using: jenkins-library
    - Groovy function files are located in `/vars`

2. Configure Jenkins
    - In Jenkins instance, navigate to Manage Jenkins > Configure System and scroll down to the *Global Pipeline Libraries* section
    - Key Configuration settings:
        - checkoutGit
        - getBuildStartAttachment
        - getGitHubFile
        - modifyFirstPost
        - postSlackAttachment
        - postSlackText
        - slackBuildEnd
        - slackBuildStart
        - slackError
        - slackPipelineStart
        - slackStageEnd
        - slackStageStart
        - slackUpdate

## Usage
3. Import the library into groovy files
    - Add this to the top of files that require the library
    ```
        // Note that @master can be replaced with a different branch in the jenkins-library repository
        @Library("jenkins-library@stable")
    ```