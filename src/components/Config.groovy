package components;

class Config {
    def CONFIGS = [
        SLACK_CHANNEL: "build-log",
        GITHUB_API_URL: "https://api.github.com",
        GITHUB_OWNER: "coltenkrauter",
        GITHUB_TOKEN_CRED_ID: "github-pat-secret-text",
        GITHUB_URL: "github.com",
        ANSIBLE_REPO: "ansible",
        ANSIBLE_SSH_PRIVATE_KEY_ID: "ssh-key-ubuntu",
        ANSIBLE_VAULT_PASSWORD_ID: "ansible-vault-password",
        PIPELINE_TIMEOUT_MINUTES: 30,
    ];

    @NonCPS
    def get(property) {
        return CONFIGS[property];
    }
}
