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
        }

        if (!pipeline.env.BUILD_LOG_SLACK_CHANNEL) {
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

            if (!pipeline.env.BUILD_LOG_SLACK_CHANNEL_ID) {
                pipeline.env.BUILD_LOG_SLACK_CHANNEL_ID = response.channel;
            }

            if (!pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS) {
                pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS = response.ts;
            }
        } catch (Exception e) {
            // handle exception, e.g. Host unreachable, timeout etc.
            pipeline.echo("error: ${e}");
        }
    }

    def postToSlackAPI(body, token) {
        post("https://slack.com/api/chat.postMessage", JsonOutput.toJson(body), token);
    }

    def postAttachment(token, attachments) {
        def body = [
            channel: pipeline.env.BUILD_LOG_SLACK_CHANNEL,
            attachments: attachments
        ];

        body = addThreadTS(body);
        postToSlackAPI(body, token);
    }

    def postMessage(token, message) {
        def body = [
            channel: pipeline.env.BUILD_LOG_SLACK_CHANNEL,
            text: message
        ];

        body = addThreadTS(body);
        postToSlackAPI(body, token);
    }

    def addThreadTS(body) {
        /* If this environment variable exists then post the message to in the thread */
        if (pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS) {
            body.thread_ts = pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS;
        }

        return body;
    }
    
    def modifyFirstPost(token, attachments) {
        def body = [
            channel: pipeline.env.BUILD_LOG_SLACK_CHANNEL_ID,
            attachments: attachments,
            ts: pipeline.env.BUILD_LOG_SLACK_MESSAGE_TS
        ];

        post("https://slack.com/api/chat.update", JsonOutput.toJson(body), token);
    }

    /* Helper functions */

    def getDuration(start, end) {
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

    def getDurationString(start, end) {
        def values = getDuration(start, end)

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
}
