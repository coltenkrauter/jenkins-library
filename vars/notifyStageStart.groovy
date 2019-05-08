def call(STAGENAME) {
    postMessageInThread("Stage *${STAGENAME}* started");

    return new Date();
}
