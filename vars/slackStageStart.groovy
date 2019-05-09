def call(STAGENAME) {
    postSlackText("Stage *${STAGENAME}* started");

    return new Date();
}
