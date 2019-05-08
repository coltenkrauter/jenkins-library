def call(MESSAGE) {
    postMessage(env.BUILD_LOG_SLACK_CHANNEL, MESSAGE);

    return new Date();
}
