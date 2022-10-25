import java.util.Collections;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.UrlValidator;

// List of supported providers
private enum Provider {

    BITLY("bitly"),
    TINY_URL("tinyurl");

    private final String name;

    Provider(String name) {
        this.name = name;
    }

    static boolean validateProvider(String name) {
        for(Provider providerName: Provider.values()) {
            if(providerName.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    String getName() {
        return name;
    }
}

final Gson gson = new Gson();

final Map<String, String> endpointsMap = Map.of(
    "bitly", "https://api-ssl.bitly.com/v4/shorten",
    "tinyurl", "https://api.tinyurl.com/create"
);

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
    
    // Validate that values present in the request are not empty (payload, variables)
    RuntimeResponse errorResponse = checkEmptyPayloadAndVariables(req, res);
    if(errorResponse != null) {
        return errorResponse;
    }

    // Validate the requested payload (provider and URL)
    String payloadString = req.getPayload();
    Map<String, Object> payload = gson.fromJson(payloadString, Map.class);

    errorResponse = validatePayload(payload, res);
    if(errorResponse != null) {
        return errorResponse;
    }

    // Generate short url
    String provider = payload.get("provider").toString();
    String url = payload.get("url").toString();

    // Validate that the API key is not empty
    String apiKeyVariable = Provider.BITLY.getName().equals(provider) ? "BITLY_API_KEY" : "TINYURL_API_KEY";

    errorResponse = checkEmptyAPIKey(req, res, apiKeyVariable);
    if(errorResponse != null) {
        return errorResponse;
    }

    String apiKey = req.getVariables().get(apiKeyVariable);

    String shortUrl = "";

    Map<String, Object> responseData = new HashMap<>();

    try {
        shortUrl = generateShortUrl(apiKey, provider, url);
    } catch(Exception e) {
        responseData.put("success", false);
        responseData.put("message", "Something went wrong while generating the shortUrl, please check with the developers. Error: " + e.getMessage());
        return res.json(responseData);
    }

    // Validate that the generated short URL is not empty
    errorResponse = checkEmptyShortUrl(shortUrl, res);
    if(errorResponse != null) {
        return errorResponse;
    }

    // Send response
    responseData.put("success", true);
    responseData.put("url", shortUrl);

    return res.json(responseData);
}

/**
 * This method validates that the generated short URL is not empty
 * 
 * @param shortUrl is the URL to be validated
 * @return null if shortURL is non-empty, otherwise an error response
 */
private RuntimeResponse checkEmptyShortUrl(String shortUrl, RuntimeResponse res) {
    Map<String, Object> responseData = new HashMap<>();
    
    if(shortUrl == null || shortUrl.trim().isEmpty()) {
        responseData.put("success", false);
        responseData.put("message", "Blank or null shortUrl value is returned, please try again or check with the developers");
        return res.json(responseData);
    }

    return null;
}

/**
 * This method validates that non-empty provider and URL are present in the payload
 * It also validates that the requested provider is one of the supported providers
 * 
 * @param payload is the object that contains the provider and the URL
 * @return null if payload is valid, otherwise an error response
 */
private RuntimeResponse validatePayload(Map<String, Object> payload, RuntimeResponse res) {
    Map<String, Object> responseData = new HashMap<>();

    // Validate that payload has both provider and url
    if(!payload.containsKey("provider") || !payload.containsKey("url")) {
        responseData.put("success", false);
        responseData.put("message", "Payload must contain both provider and url data");
        return res.json(responseData);
    }

    String provider = payload.get("provider").toString();
    String url = payload.get("url").toString();

    // Validate the provider
    if(!Provider.validateProvider(provider)) {
        responseData.put("success", false);
        String providerNames = Stream.of(Provider.values())
                                    .map(Provider::getName)
                                    .collect(Collectors.joining(", "));
        responseData.put("message", "Provider " + provider + " is not supported currently. " +
                                        "Only " + providerNames + " are supported");
        return res.json(responseData);
    }

    // Validate the URL
    UrlValidator urlValidator = new UrlValidator();
    if (!urlValidator.isValid(url)) {
        responseData.put("success", false);
        responseData.put("message", "Provided URL: " + url + " is not valid, please provide a valid, correctly formed URL");
        return res.json(responseData);
    }
    
    return null;
}

/**
 * This function will validate that the payload and variables are non-empty in the request
 * 
 * @param req is the received POST request
 * @return null is nothing is empty, otherwise an error response
 */
private RuntimeResponse checkEmptyPayloadAndVariables(RuntimeRequest req, RuntimeResponse res) {
    Map<String, Object> responseData = new HashMap<>();
    
    if(req.getPayload() == null || req.getPayload().trim().isEmpty() || req.getPayload().trim().equals("{}")) {
        responseData.put("success", false);
        responseData.put("message", "Payload is empty, expected a payload with provider and URL");
        return res.json(responseData);
    }

    if(req.getVariables() == null) {
        responseData.put("success", false);
        responseData.put("message", "Empty function variables found. You need to pass an API key for the provider");
        return res.json(responseData);
    }
    return null;
}

/**
 * This method validates that a non-empty API key is present in variables
 * 
 * @param req is the received POST request
 * @return null if non-empty API key is present, otherwise an error response
 */
private RuntimeResponse checkEmptyAPIKey(RuntimeRequest req, RuntimeResponse res, String apiKeyVariable) {
    Map<String, String> variables = req.getVariables();

    if(!variables.containsKey(apiKeyVariable)
        || variables.get(apiKeyVariable) == null
        || variables.get(apiKeyVariable).trim().isEmpty()) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "Please pass a non-empty API Key " + apiKeyVariable + " for the provider");
        return res.json(responseData);
    }

    return null;
}

/**
 * This method will generate a short URL for the given long URL and provider using the provider's API key
 * It will generate the request body and parse the response according to the provider
 * 
 * @param apiKey is the access token used by the provider to generate a short URL
 * @param provider is the service that will generate the short URL. E.g. tinyurl, bitly
 * @param url is the URL to be shortened
 * @return the shortened URL
 * @throws Exception in case of malformed URL, I/O exception, etc.
 */
private String generateShortUrl(String apiKey, String provider, String url) throws Exception {
    if(apiKey == null || apiKey.trim().isEmpty()) {
        return null;
    }

    String requestBody = "";

    if(Provider.BITLY.getName().equals(provider)) {
        requestBody = "{\"long_url\": \"" + url + "\"}";
    } else if(Provider.TINY_URL.getName().equals(provider)) {
        requestBody = "{\"url\": \"" + url + "\"}";
    }

    if(requestBody.isEmpty()) {
        return null;
    }

    String response = getShortUrlFromProvider(endpointsMap.get(provider), requestBody, apiKey);
    Map<String, Object> parsedResponse = gson.fromJson(response, Map.class);
    
    if(Provider.BITLY.getName().equals(provider)) {
        return parsedResponse.get("link").toString();
    } else if(Provider.TINY_URL.getName().equals(provider)) {
        Map<String, Object> responseData = (Map) parsedResponse.get("data");
        return responseData.get("tiny_url").toString();
    }

    return null;
}

/**
 * This method will send a POST request to the specified endpoint and return the provider's response
 * 
 * @param endpointUrl is the provider's POST endpoint to which the URL generation request is to be sent
 * @param requestBody is the Request Body for the POST request containing the URL to be shortened
 * @param apiKey is the access token used by the provider to generate a short URL
 * @return the provider's response to the POST request
 * @throws Exception in case of malformed URL, I/O exception, etc.
 */
private String getShortUrlFromProvider(String endpointUrl, String requestBody, String apiKey) throws Exception {
    URL url = new URL(endpointUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();

    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Authorization", "Bearer " + apiKey);
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