def call(ANSIBLE_PLAYBOOK, INVENTORY, EXTRA_VARS) {
    ansiblePlaybook(
        playbook: "${ANSIBLE_REPO}/${ANSIBLE_PLAYBOOK}.yaml",
        inventory: "${ANSIBLE_REPO}/inventories/${INVENTORY}/hosts",
        extras: "--extra-vars \"${EXTRA_VARS}\"",
        credentialsId: "automation-ssh-private-key",
        vaultCredentialsId:  "vault-id",
    );
}
