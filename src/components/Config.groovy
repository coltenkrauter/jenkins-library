package components;

class Config {
	def CONFIGS = [
		SLACK_CHANNEL: "build-log"
	];

	@NonCPS
    def get(property) {
        return CONFIGS[property];
    }
}
