def call(Closure main) {
    env.BUILD_START = new Date();
    env.SUCCESS = "true";
    
    node {
        try {
            /* Clears the Jenkins WORKSPACE directory */
            clean();
            initialize();
            main();
        } catch (err) {
            /* Notify slack with error message */
            slackError(err);
            throw(err);

        } catch(Throwable err) {
            /* Notify slack with error message */
            slackError(err);
            throw(err);
            
        } finally {
            /* Notify slack with error message */
            clean();
            //TODO: Provide atExit() cleanup function handler
            slackBuildEnd();
        }
    }
}
