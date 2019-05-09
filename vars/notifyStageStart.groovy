def call(STAGENAME) {
    postMessage("Stage *${STAGENAME}* started");

    return new Date();
}
