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

    def post(url, body, token) {
        try {
            def http = new URL(url).openConnection() as HttpURLConnection;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", 'application/json');
            http.setRequestProperty("Content-Type", 'application/json');
            http.setRequestProperty("Authorization", "Bearer ${token}");
            

            http.outputStream.write(body.getBytes("UTF-8"));
            http.connect();

            def response = [:];

            if (http.responseCode == 200) {
                response = new JsonSlurper().parseText(http.inputStream.getText('UTF-8'));
            } else {
                response = new JsonSlurper().parseText(http.errorStream.getText('UTF-8'));
            }

            pipeline.echo("response: ${response}");

            if (!pipeline.env.BUILD_LOG_SLACK_CHANNEL_ID) {
                pipeline.env.BUILD_LOG_SLACK_CHANNEL_ID = response.channel;
            }

            if (!pipeline.env.BUILD_LOG_SLACK_THREAD) {
                pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS = response.ts;
                pipeline.env.BUILD_LOG_SLACK_THREAD = response.ts;
            }
        } catch (Exception e) {
            // handle exception, e.g. Host unreachable, timeout etc.
            pipeline.echo("error: ${e}");
        }
    }

    def postAttachment(token, channel, attachments) {
        def body = [
            channel: "#build-log",
            attachments: attachments,
        ];

        // def body = [
        //     channel: "#build-log",
        //     text: "text",
        // ];

        post("https://slack.com/api/chat.postMessage", JsonOutput.toJson(body), token);
    }
}