def call(STAGENAME) {
    postMessage(env.BUILD_LOG_SLACK_THREAD, "Stage *${STAGENAME}* started");

    return new Date();
}
