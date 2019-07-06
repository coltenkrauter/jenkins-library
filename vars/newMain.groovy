import components.Config;
import components.Slack;

def call(Closure main) {
    def config = new Config();

    env.BUILD_START = new Date();
    env.SUCCESS = "true";
    env.PIPELINE_TIMEOUT_MINUTES = config.get("PIPELINE_TIMEOUT_MINUTES")
    
    timeout(time: env.PIPELINE_TIMEOUT_MINUTES, unit: 'MINUTES') {
        node {
            try {
                clean();
                initialize();
                main();
            } catch (err) {
                /* Check if failure was due to timeout */
                def slack = new Slack(this);
                duration = slack.getDuration(new Date(BUILD_START), new Date());
                timeout = duration.minutes.toInteger() >= env.PIPELINE_TIMEOUT_MINUTES.toInteger();

                if(err.getMessage() == "no-build") {
                    slackError("Stopping build due to \"no-build\" flag in commit message.");
                } else{
                    if(timeout) {
                        slackError("Build failed due to timeout. ");
                    } else {
                        slackError(err);
                    }

                    echo err.toString()
                    throw(err);
                }

            } catch(Throwable err) {
                slackError(err);
                echo err.toString()
                throw(err);
                
            } finally {
                clean();
                slackBuildEnd();
            }
        }
    }
}
