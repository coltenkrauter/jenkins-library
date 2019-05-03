package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
    def pipeline;
    def debug = true;

    // Constructor
    Slack(pipeline) {
        this.pipeline = pipeline;
        debug("In the Slack constructor");

        if (!pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED) {
            pipeline.debug("This will only be executed the first time Slack class is instantiated");
            pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED = "true";
        }
    }
    
    def echo(String message) {
        pipeline.echo(message);
    }
    
    @NonCPS
    def debug(String message) {
        if (debug) {
            echo(message);
        }
    }


    
}