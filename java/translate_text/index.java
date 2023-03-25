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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) {
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

    // Name API key is not empty
    String API_KEY = "API_KEY";

    errorResponse = validateVariables(req, res, API_KEY, provider);
    if (errorResponse != null) {
        return errorResponse;
    }
    Map<String, Object> variables = gson.fromJson(payloadString, Map.class);
    String apiKey = variables.get(API_KEY).toString();

    String translatedText = "";
    Map<String, Object> responseData = new HashMap<>();

    try {
        translatedText = translateText(payload, apiKey);
    } catch (Exception e) {
        responseData.put("success", false);
        responseData.put("message", "Something went wrong while translating the text, please check with the developers. Error: " + e.getMessage());
        return res.json(responseData);
    }
    responseData.put("success", true);
    responseData.put("message", translatedText);
    responseData.put("from",sourceLanguageCode);

    return res.json(responseData);
}

public String translateText(Map<String, Object> payload, String apiKey) throws GeneralSecurityException, IOException {
    String provider = payload.get("provider").toString();
    String sourceLanguageCode = payload.get("from").toString();
    String targetLanguageCode = payload.get("to").toString();
    String text = payload.get("text").toString();

    if (Objects.equals(Provider.AWS.toString(), provider)) {
        return translateTexteUsingAWS(text, sourceLanguageCode, targetLanguageCode, apiKey);
    } else if (Objects.equals(Provider.GOOGLE.toString(), provider)) {
        return translateTextUsingGCP(text, targetLanguageCode, apiKey);
    } else if (Objects.equals(Provider.AZURE.toString(), provider)) {
        return translateTexteUsingAzure(text, targetLanguageCode, apiKey);
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

public String translateTexteUsingAWS(String text, String sourceLanguageCode, String targetLanguageCode, String apiKey) {

    try {
        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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

public String translateTexteUsingAzure(String text, String targetLanguageCode, String apiKey) throws IOException {
    String endpointUrl = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=" + targetLanguageCode;
    String requestBody = "[{\n\t\"text\": " + text + "\n}]";
    URL url = new URL(endpointUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
    con.setRequestProperty("Accept", "application/json");
    con.setDoOutput(true);
    OutputStream os = con.getOutputStream();
    byte[] input = requestBody.getBytes("utf-8");
    os.write(input, 0, input.length);
    StringBuilder response = new StringBuilder();
    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
    String responseLine = null;
    while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
    }
    br.close();
    con.disconnect();
    return response.toString();
}

private RuntimeResponse validateVariables(RuntimeRequest req, RuntimeResponse res, String apiKey, String provider) {
    Map<String, String> variables = req.getVariables();

    if (!Objects.equals(Provider.AWS.toString(), provider) && !variables.containsKey(apiKey) || variables.get(apiKey) == null || variables.get(apiKey).trim().isEmpty()) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "Please pass a non-empty API Key(s) {}" + apiKey + " for translateText");

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
        responseData.put("message", "Payload is empty, expected a payload with bucketId");
        return res.json(responseData);

    }
    if (req.getVariables() == null) {
        responseData.put("success", false);
        responseData.put("message", "Empty function variables found. You need to pass an API key.");
        return res.json(responseData);
    }

    return null;
}