def call(MESSAGE) {
	postMessage(env.BUILD_LOG_SLACK_THREAD, MESSAGE);

    return new Date();
}
