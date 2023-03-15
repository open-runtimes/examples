import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

private static class MessageSendingFailedException extends RuntimeException {
    public MessageSendingFailedException(String message) {
        super(message);
    }

    public MessageSendingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) {
    Map<String, BiConsumer<Map<String, Object>, Map<String, String>>> messageDispatcher = Map.of(
            "Discord", this::sendDiscordMessage,
            "Email", this::sendEmailMessage,
            "SMS", this::sendSMSMessage,
            "Twitter", this::sendTwitterMessage
    );

    String payloadString = req.getPayload();
    Gson gson = new Gson();
    Map payload = gson.fromJson(payloadString, Map.class);

    try {
        Optional.ofNullable(messageDispatcher.get((String) payload.get("type")))
                .orElseThrow(() -> new IllegalArgumentException(String.format("No such type: %s", payload.get("type"))))
                .accept(payload, req.getVariables());
        return res.json(Map.of("success", true));
    } catch (IllegalArgumentException exception) {
        return res.json(Map.of(
                "success", false,
                "message", exception.getMessage()
        ));
    }
}

private void sendDiscordMessage(Map<String, Object> messageBody, Map<String, String> variables) {
    String discordWebhookURL = variables.get("DISCORD_WEBHOOK_URL");

    Gson gson = new Gson();
    String message = getMessageField("message");
    String body = gson.toJson(Map.of("content", message));

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(discordWebhookURL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

    try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 299) {
            throw new MessageSendingFailedException("Request failed: " + response.body());
        }
    } catch (IOException | InterruptedException e) {
        throw new MessageSendingFailedException(e.getMessage(), e);
    }
}

private void sendEmailMessage(Map<String, Object> messageBody, Map<String, String> variables) {
    String mailgunDomain = variables.get("MAILGUN_DOMAIN");
    String mailgunApiKey = variables.get("MAILGUN_API_KEY");

    Gson gson = new Gson();

    String receiver = getMessageField(messageBody, "receiver"); // "hello@example.com",
    String message = getMessageField(messageBody, "message"); // "Programming is fun!",
    String subject = getMessageField(messageBody, "subject"); // "Programming is funny!"

    String body = gson.toJson(Map.of(
            "from", "<welcome@my-awesome-app.io>",
            "to", receiver,
            "subject", subject,
            "text",message)
    );

    HttpClient client = HttpClient.newHttpClient();
    String uri = String.format("https://api.mailgun.net/v3/%s/messages", mailgunDomain);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("APIKEY %s", mailgunApiKey))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

    try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 299) {
            throw new MessageSendingFailedException("Request failed: " + response.body());
        }
    } catch (IOException | InterruptedException e) {
        throw new MessageSendingFailedException(e.getMessage(), e);
    }
}

private void sendSMSMessage(Map<String, Object> messageBody, Map<String, String> variables) {
    String twilioAccountSid = variables.get("TWILIO_ACCOUNT_SID");
    String twilioAuthToken = variables.get("TWILIO_AUTH_TOKEN");
    String twilioSender = variables.get("TWILIO_SENDER");

    Gson gson = new Gson();

    String receiver = getMessageField(messageBody, "receiver"); // "hello@example.com",
    String message = getMessageField(messageBody, "message"); // "Programming is fun!",

    String body = gson.toJson(Map.of(
            "From", twilioSender,
            "To", receiver,
            "Body", message)
    );

    HttpClient client = HttpClient.newHttpClient();
    String uri = String.format("https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json", twilioAccountSid);
    String authorizationHeader = "BASIC " + Base64.getEncoder().encodeToString(String.format("%s:%s", twilioAccountSid, twilioAuthToken).getBytes());

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .header("Authorization", authorizationHeader)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

    try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() > 299) {
            throw new MessageSendingFailedException("Request failed: " + response.body());
        }
    } catch (IOException | InterruptedException e) {
        throw new MessageSendingFailedException(e.getMessage(), e);
    }
}

private void sendTwitterMessage(Map<String, Object> messageBody, Map<String, String> variables) {
    String twitterApiKey = variables.get("TWITTER_API_KEY");
    String twitterApiKeySecret = variables.get("TWITTER_API_KEY_SECRET");
    String twitterAccessToken = variables.get("TWITTER_ACCESS_TOKEN");
    String twitterAccessTokenSecret = variables.get("TWITTER_ACCESS_TOKEN_SECRET");

    Gson gson = new Gson();

    String receiver = getMessageField(messageBody, "receiver"); // "hello@example.com",
    String message = getMessageField(messageBody, "message"); // "Programming is fun!",

    // TODO: add use of twitter API to send a tweet
}

private String getMessageField(Map<String, Object> messageBody, String field) {
    return Optional.ofNullable(messageBody.get(field)).orElseThrow(() -> new IllegalArgumentException(String.format("%s field not specified", field));
}