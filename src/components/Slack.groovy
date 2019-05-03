package components;

import groovy.json.JsonOutput;
import java.time.Instant;

class Slack {
    def pipeline;
    def debug = true;

    // Constructor
    Slack(pipeline) {
        this.pipeline = pipeline;

        if (!pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED) {
            // This will only be executed the first time Slack class is instantiated thanks to setting this env variable
            pipeline.env.SLACK_CONSTRUCTOR_WAS_INITIALIZED = "true";
            pipeline.env.BUILD_LOG_SLACK_CHANNEL = "build-log";
            pipeline.env.BUILD_LOG_SLACK_THREAD = "";
            pipeline.env.SUCCESS = "true";

            String build_user = pipeline.env.GIT_COMMITTER_NAME;
            wrap([$class: 'BuildUser']) {
                if (env.BUILD_USER) {
                    build_user = BUILD_USER;
                }
            }
            pipeline.env.BUILD_TRIGGER_USER = build_user;
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

    def postMessage(channel, message) {
        return slackSend(channel: channel, message: message).threadId;
    }

    def postAttachment(channel, attachment) {
        return slackSend(channel: channel, attachments: JsonOutput.toJson(attachment)).threadId;
    }

    def get_duration(start, end) {
        def duration = groovy.time.TimeCategory.minus(
          end,
          start
        )

        values = [
            "seconds" : duration.seconds,
            "minutes" : duration.minutes,
            "hours" : duration.hours,
            "days" : duration.days,
            "ago" : duration.ago,
        ]

        return values
    }

    def get_duration_string(start, end) {
        values = get_duration(start, end)

        seconds = values["seconds"]
        message = "$seconds second" + plural(seconds)

        minutes = values["minutes"]
        if (minutes)
            message = "$minutes minute" + plural(minutes) + ", $message"

        hours = values["hours"]
        if (hours)
            message = "$hours hour" + plural(hours) + ", $message"

        days = values["days"]
        if (days)
            message = "$days day" + plural(days) + ", $message"

        message = message.replace(", 0 seconds", "")

        if (message == "0 seconds") {
            message = "<1 second"
        }

        return message
    }

    def plural(value) {
        return (value.toInteger() == 0 || value.toInteger() > 1) ? "s" : ""
    }   
}