package io.translate_text.java;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
    // Validate that values present in the request are not empty (payload, variables)
    RuntimeResponse errorResponse = checkEmptyPayloadAndVariables(req, res);
    if (errorResponse != null) {
        return errorResponse;
    }

    // Validate the requested payload
    String payloadString = req.getPayload().toString();

    errorResponse = validatePayload(payloadString, res);
    if (errorResponse != null) {
        return errorResponse;
    }

    Map<String, Object> payload = gson.fromJson(payloadString, Map.class);

    // Get provider from payload
    String provider = payload.get("provider").toString();
    String sourceLanguageCode = payload.get("from").toString();

    String API_KEY = "API_KEY";
    String REGION = "REGION";
    String SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY";

    errorResponse = validateVariables(req, res, API_KEY, SECRET_ACCESS_KEY, REGION, provider);
    if (errorResponse != null) {
        return errorResponse;
    }

    Map<String, String> variables = req.getVariables();
    String apiKey = variables.get(API_KEY);

    String translatedText = "";
    Map<String, Object> responseData = new HashMap<>();

    try {
        translatedText = translateText(payload, variables, apiKey);
    } catch (Exception e) {
        responseData.put("success", false);
        responseData.put("message", "Something went wrong while translating the text, please check with the developers. Error: " + e.getMessage());
        return res.json(responseData);
    }
    responseData.put("success", true);
    responseData.put("message", translatedText);
    responseData.put("from", sourceLanguageCode);

    return res.json(responseData);
}

public String translateText(Map<String, Object> payload, Map<String, String> variables, String apiKey) throws GeneralSecurityException, IOException {
    String provider = payload.get("provider").toString();
    String sourceLanguageCode = payload.get("from").toString();
    String targetLanguageCode = payload.get("to").toString();
    String text = payload.get("text").toString();

    if (Objects.equals("aws", provider)) {
        String secretAccessKey = variables.get("SECRET_ACCESS_KEY");
        return translateTexteUsingAWS(text, sourceLanguageCode, targetLanguageCode, apiKey, secretAccessKey);
    } else if (Objects.equals("google", provider)) {
        return translateTextUsingGCP(text, targetLanguageCode, apiKey);
    } else if (Objects.equals("azure", provider)) {
        String region = variables.get("REGION");
        return translateTexteUsingAzure(text, sourceLanguageCode, targetLanguageCode, apiKey, region);
    }

    return null;
}


public String translateTextUsingGCP(String text, String targetLanguage, String apiKey) throws GeneralSecurityException, IOException {
    Translate t = new Translate.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), null)
            .setApplicationName("Interacto")
            .build();

    Translate.Translations.List list = t.new Translations().list(
            Arrays.asList(text), targetLanguage);
    TranslationsListResponse translateResponse = list.setKey(apiKey).execute();
    List<TranslationsResource> translationsResourceList = translateResponse.getTranslations();
    return translationsResourceList.get(0).getTranslatedText();
}

public String translateTexteUsingAWS(String text, String sourceLanguageCode, String targetLanguageCode, String accessKeyId, String secretAccessKey) {

    try {
        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        TranslateTextRequest textRequest = TranslateTextRequest.builder()
                .sourceLanguageCode(sourceLanguageCode)
                .targetLanguageCode(targetLanguageCode)
                .text(text)
                .build();

        TranslateTextResponse textResponse = translateClient.translateText(textRequest);
        System.out.println(textResponse.translatedText());
        translateClient.close();
        return textResponse.translatedText();
    } catch (TranslateException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
    return text;
}

public String translateTexteUsingAzure(String text, String sourceLanguageCode, String targetLanguageCode, String apiKey, String region) throws IOException {
    String endpointUrl = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=" + sourceLanguageCode + "&to=" + targetLanguageCode;
    String requestBody = "[{\"text\":\"" + text + "\"}]";

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType,
            requestBody);
    Request request = new Request.Builder()
            .url(endpointUrl)
            .post(body)
            .addHeader("Ocp-Apim-Subscription-Key", apiKey)
            // location required if you're using a multi-service or regional (not global) resource.
            .addHeader("Ocp-Apim-Subscription-Region", region)
            .addHeader("Content-type", "application/json")
            .build();
    Response response = client.newCall(request).execute();
    String res = response.body().string();
    JsonParser parser = new JsonParser();
    JsonElement json = parser.parse(res);
    String answer = json.getAsJsonArray().getAsJsonArray().get(0).getAsJsonObject().get("translations").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();

    return answer;
}

private RuntimeResponse validateVariables(RuntimeRequest req, RuntimeResponse res, String apiKey, String secretAccessKey, String region, String provider) {
    Map<String, String> variables = req.getVariables();

    if (!variables.containsKey(apiKey) || variables.get(apiKey) == null || variables.get(apiKey).trim().isEmpty()) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "Please pass a non-empty API Key(s) " + apiKey + " for translateText");
        return res.json(responseData);
    }

    if (Objects.equals("aws", provider) && (!variables.containsKey(secretAccessKey) || variables.get(secretAccessKey) == null || variables.get(secretAccessKey).trim().isEmpty())) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "Please pass a non-empty " + secretAccessKey + " for translateText");
        return res.json(responseData);
    }

    if (Objects.equals("azure", provider) && (!variables.containsKey(region) || variables.get(region) == null || variables.get(region).trim().isEmpty())) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "Please pass a non-empty " + region + " for translateText");
        return res.json(responseData);
    }

    return null;
}

private RuntimeResponse validatePayload(String payloadString, RuntimeResponse res) {
    Map<String, Object> responseData = new HashMap<>();
    Map<String, Object> payload;
    ;
    try {
        payload = gson.fromJson(payloadString, Map.class);
    } catch (Exception e) {
        responseData.put("success", false);
        responseData.put("message", "The payload is invalid. Example of valid payload:{\"provider\":\"aws\",\"from\":\"cs\",\"to\":\"en\",\"text\":\"Ahoj svet.\"}");
        return res.json(responseData);
    }

    if (!payload.containsKey("provider")) {
        responseData.put("success", false);
        responseData.put("message", "Please provide a valid provider");
        return res.json(responseData);
    }


    if (!payload.containsKey("from")) {
        responseData.put("success", false);
        responseData.put("message", "Please provide a valid from");
        return res.json(responseData);
    }

    if (!payload.containsKey("to")) {
        responseData.put("success", false);
        responseData.put("message", "Please provide a valid to");
        return res.json(responseData);
    }

    if (!payload.containsKey("text")) {
        responseData.put("success", false);
        responseData.put("message", "Please provide a valid text");
        return res.json(responseData);
    }

    String provider = payload.get("provider").toString();
    String from = payload.get("from").toString();
    String to = payload.get("to").toString();
    String text = payload.get("text").toString();

    if (provider == null || provider.isEmpty()) {
        responseData.put("success", false);
        responseData.put("message", "Provided provider: " + provider + " is not valid, please provide a valid provider");
        return res.json(responseData);
    }

    if (from == null || from.isEmpty()) {
        responseData.put("success", false);
        responseData.put("message", "Provided from: " + from + " is not valid, please provide a valid from value");
        return res.json(responseData);
    }

    if (to == null || to.isEmpty()) {
        responseData.put("success", false);
        responseData.put("message", "Provided to: " + to + " is not valid, please provide a valid to value");
        return res.json(responseData);
    }

    if (text == null || text.isEmpty()) {
        responseData.put("success", false);
        responseData.put("message", "Provided text: " + text + " is not valid, please provide a valid text value");
        return res.json(responseData);
    }

    return null;
}

private RuntimeResponse checkEmptyPayloadAndVariables(RuntimeRequest req, RuntimeResponse res) {

    Map<String, Object> responseData = new HashMap<>();

    if (req.getPayload() == null || req.getPayload().trim().isEmpty() || req.getPayload().trim().equals("{}")) {
        responseData.put("success", false);
        responseData.put("message", "Payload is empty, expected a payload with {\"provider\":\"aws\",\"from\":\"cs\",\"to\":\"en\",\"text\":\"Ahoj svet.\"}");
        return res.json(responseData);

    }
    if (req.getVariables() == null) {
        responseData.put("success", false);
        responseData.put("message", "Empty function variables found. You need to pass an API_KEY");
        return res.json(responseData);
    }

    return null;
}