def call(stageName, Closure stageFunction) {
    stage(stageName) {
        startTime = slackStageStart(stageName);
        stageFunction();
        slackStageEnd(stageName, startTime, new Date());
    }
}
