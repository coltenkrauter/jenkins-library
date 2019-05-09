def call(STAGE_NAME) {
    postSlackText("Stage *${STAGE_NAME}* started");

    return new Date();
}
