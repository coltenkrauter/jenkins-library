package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
    def pipeline;
    def debug = true;

    // Constructor
    Slack(pipeline) {
        this.pipeline = pipeline;

        echo("In the Constructor");
        
        if (!pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED) {
            echo("FOR THE FIRST TIME");
        } else {
            pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED = "true";            
            echo("ALL THE REST");
        }
    }
    
    def echo(String message) {
        pipeline.echo(message);
    }

    def debug(String message) {
        if (debug) {
            echo(message);
        }
    }


    
}