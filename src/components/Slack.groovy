package components;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
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

    def postMessage(message) {
        return pipeline.slackSend(message: message).threadId;
    }

    def postMessage(channel, message) {
        return pipeline.slackSend(channel: channel, message: message).threadId;
    }

    def postAttachment(attachment) {
        return pipeline.slackSend(attachments: JsonOutput.toJson(attachment)).threadId;
    }

    def postAttachment(channel, attachment) {
        return pipeline.slackSend(channel: channel, attachments: JsonOutput.toJson(attachment)).threadId;
    }

    def postAttachmentAndBroadcast(channel, attachment) {
        return pipeline.slackSend(channel: channel, attachments: JsonOutput.toJson(attachment), replyBroadcast: true).threadId;
    }

    def get_duration(start, end) {
        def duration = groovy.time.TimeCategory.minus(
          end,
          start
        )

        def values = [
            "seconds" : duration.seconds,
            "minutes" : duration.minutes,
            "hours" : duration.hours,
            "days" : duration.days,
            "ago" : duration.ago,
        ]

        return values;
    }

    def get_duration_string(start, end) {
        def values = get_duration(start, end)

        def seconds = values["seconds"]
        def message = "$seconds second" + plural(seconds)

        def minutes = values["minutes"]
        if (minutes)
            message = "$minutes minute" + plural(minutes) + ", $message"

        def hours = values["hours"]
        if (hours)
            message = "$hours hour" + plural(hours) + ", $message"

        def days = values["days"]
        if (days)
            message = "$days day" + plural(days) + ", $message"

        message = message.replace(", 0 seconds", "")

        if (message == "0 seconds") {
            message = "<1 second"
        }

        return message;
    }

    def plural(value) {
        return (value.toInteger() == 0 || value.toInteger() > 1) ? "s" : ""
    }

    def post(url, body) {
        try {
            def http = new URL(url).openConnection() as HttpURLConnection;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", 'application/json');
            http.setRequestProperty("Content-Type", 'application/json');

            http.outputStream.write(body.getBytes("UTF-8"));
            http.connect();

            def response = [:];

            if (http.responseCode == 200) {
                response = new JsonSlurper().parseText(http.inputStream.getText('UTF-8'));
            } else {
                response = new JsonSlurper().parseText(http.errorStream.getText('UTF-8'));
            }

            pipeline.echo("response: ${response}");

        } catch (Exception e) {
            // handle exception, e.g. Host unreachable, timeout etc.
            pipeline.echo("error: ${e}");
        }
    }

    def postMessage() {
        def body = [
            token: "xoxb-2184481876-576704771890-Tx9XpMFsCVgBljG1r2jJpTbm",
            channel: "#test-api-yet-again",
            text: "Text here.",
            username: "otherusername",
        ];

        post("https://slack.com/api/chat.postMessage", JsonOutput.toJson(body));
    }
}