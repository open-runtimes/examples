import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.util.stream.Collectors;
import java.util.stream.*;

import com.google.gson.Gson;

/**
 * Enum for provider names
 * @param name is provider name
 *             TINY_PNG is TinyPNG provider
 *             KRAKENIO is Kraken.io provider
 *             getName() is getter for provider name
 */

private enum Provider {
    TINY_PNG("tinypng"), KRAKENIO("krakenio");

    private final String name;

    Provider(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    // check if provider is valid
    static boolean validateProvider(String name) {
        for (Provider providerName : Provider.values()) {
            if (providerName.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}

    final Gson gson = new Gson();

    public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {

        // payload string
        String payloadString = req.getPayload() == null || req.getPayload().isEmpty() ? "{}" : req.getPayload();

        // convert payload to map
        Map<String, Object> payload = gson.fromJson(payloadString, Map.class);

        // check if requested payload and variables are present
        RuntimeResponse errorResponse = checkPayloadAndVariables(req, res);
        if (errorResponse != null) {
            return errorResponse;
        }

        // check if payload contains provider and image
        errorResponse = validatePayload(payload, res);
        if (errorResponse != null) {
            return errorResponse;
        }

        // get provider from payload and image from payload
        String provider = payload.get("provider").toString();
        String image = payload.get("image").toString();

        // check API key is present in variables
        String apiKeyVariable = Provider.TINY_PNG.getName().equals(provider) ? "TINYPNG_API_KEY" : "KRAKENIO_API_KEY";

        errorResponse = checkEmptyApiKey(req, res, apiKeyVariable);
        if (errorResponse != null) {
            return errorResponse;
        }
        String apiKey = req.getVariables().get(apiKeyVariable);

        // compressed image in Base64 string
        String compressedImage = "compressed image is under implementation";

        // response data to return
        Map<String, Object> responseData = new HashMap<>();

        // TODO: compress image using provider API and store the result in compressedImage variable

        // TODO: check if compressedImage is valid

        // If input valid then return success true and compressed image
        responseData.put("success", true);
        responseData.put("image", compressedImage);

        return res.json(responseData);
    }

    /**
     * Check if requested payload and variables are present
     * @param req is request object from function call
     * @param res is response object from function call
     * @return null if payload and variables are present, otherwise return error response
     */
    private RuntimeResponse checkPayloadAndVariables(RuntimeRequest req, RuntimeResponse res) {
        Map<String, Object> responseData = new HashMap<>();

        // check if requested payload and variables are present
        if (req.getPayload() == null || req.getPayload().trim().isEmpty() || req.getPayload().trim().equals("{}")) {
            responseData.put("success", false);
            responseData.put("message", "Payload is empty, please provide provider and image");
            return res.json(responseData);
        }

        if (req.getVariables() == null) {
            responseData.put("success", false);
            responseData.put("message", "Variables are empty, please provide an API key for provider");
            return res.json(responseData);
        }
        return null;
    }

    /**
     * Validate payload whether it contains provider and image
     * @param payload is an object from request payload which contains provider and image
     * @param res is response object from function call
     * @return null if payload is valid, otherwise return error response
     */

    private RuntimeResponse validatePayload(Map<String, Object> payload, RuntimeResponse res) {
        Map<String, Object> responseData = new HashMap<>();

        // check if payload contains provider and image
        if (!payload.containsKey("provider") || !payload.containsKey("image")) {
            responseData.put("success", false);
            responseData.put("message", "Payload is invalid, please provide provider and image");
            return res.json(responseData);
        }

        // get provider from payload and image from payload
        String provider = payload.get("provider").toString();
        String image = payload.get("image").toString();

        // check if provider is valid
        if (!Provider.validateProvider(provider)) {
            responseData.put("success", false);
            String providerNames = Stream.of(Provider.values()).map(Provider::getName).collect(Collectors.joining(", ")); // get all provider names
            responseData.put("message", "Provider " + provider + "is invalid, please provide one of providers: " + providerNames);
            return res.json(responseData);
        }

        // check if image is valid in Base64 with regex pattern matching
        if (!image.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$") || image.trim().isEmpty()) {
            responseData.put("success", false);
            responseData.put("message", "Image is invalid, please provide a valid Base64 image");
            return res.json(responseData);
        }
        return null;
    }

    /**
     * Check if API key is present in variables for provider
     * @param req is request object from function call
     * @param res is response object from function call
     * @param apiKeyVariable is API key variable name for provider
     * @return null if API key is present, otherwise return error response
     */

    // check API key is present in variables
    private RuntimeResponse checkEmptyApiKey(RuntimeRequest req, RuntimeResponse res, String apiKeyVariable) {
        Map<String, String> variables = req.getVariables();

        if (!variables.containsKey(apiKeyVariable) || variables.get(apiKeyVariable) == null || variables.get(apiKeyVariable).trim().isEmpty()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", false);
            responseData.put("message", "API key is not present in variables, please provide " + apiKeyVariable + " for the provider");
            return res.json(responseData);
        }
        return null;
    }