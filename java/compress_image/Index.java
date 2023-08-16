import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.Base64;
import com.google.gson.Gson;
import com.tinify.Source;
import com.tinify.Tinify;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

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
        String apiKeyVariable;
        String apiSecretVariable;

        if (Provider.TINY_PNG.getName().equals(provider)) {
            apiKeyVariable = "TINYPNG_API_KEY";
            apiSecretVariable = null;
        } else {
            apiKeyVariable = "KRAKENIO_API_KEY";
            apiSecretVariable = "KRAKENIO_API_SECRET";
        }

        errorResponse = checkEmptyApiKeyAndSecret(req, res, apiKeyVariable, apiSecretVariable);
        if (errorResponse != null) {
            return errorResponse;
        }
        String apiKey = req.getVariables().get(apiKeyVariable);
        String secretKey = req.getVariables().get(apiSecretVariable);

        // compressed image in Base64 string
        String compressedImage = "";

        // response data to return
        Map<String, Object> responseData = new HashMap<>();

        // compress image using provider API and store the result in compressedImage variable
        if (Provider.TINY_PNG.getName().equals(provider)) {
            // Decode image from Base64 string
            byte[] imageByte = convertToByte(image);

            // Compress image
            byte[] compressedImageByte = tinifyCompress(imageByte, apiKey);

            // Encode image to Base64 string
            compressedImage = convertToBase64(compressedImageByte);
        } else {
            // Decode input string
            byte[] imageBytes = convertToByte(image);

            // Compress image
            String urlResponse = krakenCompress(imageBytes, apiKey, secretKey);

            // Decode compressed image from URL
            URL url = new URL(urlResponse);
            InputStream inputStream = url.openStream();
            byte[] compressedImageBytes = inputStream.readAllBytes();
            compressedImage = convertToBase64(compressedImageBytes);
            inputStream.close();
        }

        // Check if compressedImage is valid
        errorResponse = checkCompressedImage(compressedImage, res);
        if (errorResponse != null) {
            return errorResponse;
        }

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

    private RuntimeResponse checkEmptyApiKeyAndSecret(RuntimeRequest req, RuntimeResponse res, String apiKeyVariable, String apiSecretVariable) {
        Map<String, String> variables = req.getVariables();

        if (!variables.containsKey(apiKeyVariable) || variables.get(apiKeyVariable) == null || variables.get(apiKeyVariable).trim().isEmpty()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", false);
            responseData.put("message", "API key is not present in variables, please provide " + apiKeyVariable + " for the provider");
            return res.json(responseData);
        }

        if (apiSecretVariable != null && (!variables.containsKey(apiSecretVariable) || variables.get(apiSecretVariable) == null || variables.get(apiSecretVariable).trim().isEmpty())) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", false);
            responseData.put("message", "API secret is not present in variables, please provide " + apiSecretVariable + " for the provider");
            return res.json(responseData);
        }
        return null;
    }

    /**
     * Check if compressed image is valid
     * @param compressedImage is compressed image in Base64 string
     * @param res is response object from function call
     * @return null if compressed image is valid, otherwise return error response
     */

    private RuntimeResponse checkCompressedImage(String compressedImage, RuntimeResponse res) {
        Map<String, Object> responseData = new HashMap<>();

        // check if compressed image is valid
        if (!compressedImage.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$") || compressedImage.trim().isEmpty()) {
            responseData.put("success", false);
            responseData.put("message", "Compressed image is invalid, please provide a valid Base64 image");
            return res.json(responseData);
        }
        return null;
    }


    /**
     * Converts Base64 input of payload to byte array
     * @param String baseInput is base64 string variable from payload
     * @return byte [] baseInput decoded as byte array
     */

    private byte [] convertToByte(String baseInput) {
        return Base64.getDecoder().decode(baseInput);
    }

    /**
     * Converts byte array input returned by compression method to Base64
     * @param byte [] byteInput is byte array variable returned from compression method
     * @return String byteInput encoded as Base64 String
     */

    private String convertToBase64(byte [] byteInput) {
        return Base64.getEncoder().encodeToString(byteInput);
    }


    /**
     * Compresses image in byte array format using TinyPNG provider
     * @param byte [] image is image to compress in byte array format
     * @return byte [] compressed image
     */

    private byte [] tinifyCompress(byte [] image, String apiKey) throws Exception {
        Tinify.setKey(apiKey);
        Source source = Tinify.fromBuffer(image);
        return source.toBuffer();
    }

    /**
     * Compresses image in byte array format using Kraken.io provider
     * @param byte [] image is image to compress in byte array format
     * @return String url of compressed image in String format
     */

    public String krakenCompress(byte[] image, String apiKey, String apiSecret) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String krakedUrl = "";

        String data = "{\"auth\":{\"api_key\":\"" + apiKey + "\",\"api_secret\":\"" + apiSecret + "\"}, \"wait\": true}";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", data)
                .addFormDataPart("upload", "image.png", RequestBody.create(MediaType.parse("image/png"), image))
                .build();

        Request request = new Request.Builder()
                .url("https://api.kraken.io/v1/upload")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson responseGson = new Gson();
            Map<String, Object> responseData = responseGson.fromJson(response.body().string(), Map.class);
            krakedUrl = responseData.get("kraked_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return krakedUrl;
    };