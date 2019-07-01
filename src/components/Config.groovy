package components;

class Config {
    def CONFIGS = [
        SLACK_CHANNEL: "build-log",
        GITHUB_API_URL: "https://api.github.com",
        GITHUB_OWNER: "coltenkrauter",
        GITHUB_TOKEN_CRED_ID: "github-pat-secret-text",
        GITHUB_URL: "github.com",
        ANSIBLE_REPO: "ansible",
        PIPELINE_TIMEOUT_MINUTES: 30
    ];

    @NonCPS
    def get(property) {
        return CONFIGS[property];
    }
}