package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
	def pipeline;

	// Constructor
    Slack(pipeline) {
    	this.pipeline = pipeline;
    	pipeline.echo("CONSTRUCTOR");
    }

	def test() {
		pipeline.echo("This is the SLACK CLASS");
	}
}