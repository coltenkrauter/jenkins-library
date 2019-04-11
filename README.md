# Jenkins Library

## Setup
These instructions are intended to assist in setting up a jenkins shared library.

1. Create a GitHub repository per [Jenkins Guidelines](https://jenkins.io/doc/book/pipeline/shared-libraries/) regarding shared libraries
	- Currently using: jenkins-library
	- Groovy files are located in /src/library and have `package library` at the top of each file

2. Configure Jenkins
	- In Jenkins instance, navigate to Manage Jenkins > Configure System and scroll down to the *Global Pipeline Libraries* section
	- Key Configuration settings:
		- Default version: master
		- Load implicitly: False
		- Allow default version to be overridden: True
		- Include @Library changes in job recent changes: True
		- Retrieval method: Modern SCM
		- Source Code Management: GitHub
		- Credentials: s-00078 (GitHub Enterprise Personal Access Token)
		- Owner: OCC
		- Repository: jenkins-library
		- Behaviors: Discover branches, all branches

## Usage
3. Import the library into groovy files
	- Add this to the top of files that require the library
	```
		// Note that @master can be replaced with a different branch in the jenkins-library repository
		@Library("jenkins-library@master")
		import library.*
	```
	- In order to use the notify.groovy file, you must ensure that there is a yaml config file at /jenkins/config.yaml that is like [config.yaml/sample](https://scm.starbucks.com/OCC/jenkins-library/blob/master/src/library/config.yaml.sample)

## Resources
[Slack Plugin](https://github.com/jenkinsci/slack-plugin)