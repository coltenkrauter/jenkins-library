package components;

class Config {
    def CONFIGS = [
        SLACK_CHANNEL: "build-log",
        GITHUB_API_URL: "https://api.github.com",
        GITHUB_OWNER: "coltenkrauter",
        GITHUB_TOKEN_CRED_ID: "build-server-github-integration",
        GITHUB_URL: "https://github.com"
    ];

    @NonCPS
    def get(property) {
        return CONFIGS[property];
    }
}