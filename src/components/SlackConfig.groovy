package components;

class SlackConfig {
	@NonCPS
    def getSlackChannel() {
        return "build-log";
    }
}
