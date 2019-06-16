package components;

class Config {
    def CONFIGS = [
        SLACK_CHANNEL: "build-log",
        GITHUB_API_URL: "https://api.github.com",
        GITHUB_OWNER: "coltenkrauter",
        GITHUB_TOKEN_CRED_ID: "github_pat_secret_text",
        GITHUB_URL: "github.com",
        ANSIBLE_REPO: "ansible"
    ];

    @NonCPS
    def get(property) {
        return CONFIGS[property];
    }
}