# Jenkins Library

## Setup
These instructions are intended to assist in setting up a jenkins shared library.

1. Create a GitHub repository per [Jenkins Guidelines](https://jenkins.io/doc/book/pipeline/shared-libraries/) regarding shared libraries
	- Currently using: jenkins-library
	- Groovy function files are located in `/vars`

2. Configure Jenkins
	- In Jenkins instance, navigate to Manage Jenkins > Configure System and scroll down to the *Global Pipeline Libraries* section
	- Key Configuration settings:
		- Default version: master
		- Load implicitly: False
		- Allow default version to be overridden: True
		- Include @Library changes in job recent changes: True
		- Retrieval method: Modern SCM
		- Source Code Management: GitHub
		- Credentials: Use a GitHub Personal Access Token credential
		- Owner: This is often your GitHub username
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
