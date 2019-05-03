package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
	def plugins;

	// Constructor
    Slack(jenkins) {
    	this.plugins = plugins;
    }

	def test() {
		plugins.echo("This is the SLACK CLASS");
	}
}