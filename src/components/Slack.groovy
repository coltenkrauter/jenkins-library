package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
	def pipeline;

	// Constructor
    Slack(pipeline) {
    	this.pipeline = pipeline;
    	plugins.echo("CONSTRUCTOR");
    }

	def test() {
		plugins.echo("This is the SLACK CLASS");
	}
}