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
final Gson gson=new Gson();

public RuntimeResponse main(RuntimeRequest req,RuntimeResponse res)throws Exception{
        // Validate that values present in the request are not empty (payload, variables)
        RuntimeResponse errorResponse=checkEmptyPayloadAndVariables(req,res);
        if(errorResponse!=null){
            return errorResponse;
        }

        // Validate the requested payload (URL)
        String payloadString=req.getPayload().toString();
        Map<String, Object> payload=gson.fromJson(payloadString,Map.class);

        errorResponse=validatePayload(payload,res);
        if(errorResponse!=null){
            return errorResponse;
        }

        // Get file url from payload
        String fileurl=payload.get("fileUrl").toString();

        // Name API key is not empty
        String apiKeyVariable="DEEPGRAM_SECRET_KEY";

        errorResponse=checkEmptyAPIKey(req,res,apiKeyVariable);
        if(errorResponse!=null){
            return errorResponse;
        }

        String apiKey=req.getVariables().get(apiKeyVariable).toString();

        String deepgramData="";
        Map<String, Object> responseData=new HashMap<>();

        try{
            deepgramData=detectLanguage(fileurl,apiKey);
        }catch(Exception e){
            responseData.put("success",false);
            responseData.put("message","Something went wrong while generating the summary, please check with the developers. Error: "+e.getMessage());//TODO
        return res.json(responseData);
        }
        responseData.put("success",true);
        responseData.put("deepgramData",deepgramData);

        return res.json(responseData);
}


/**
 * This method will send a POST request to the specified endpoint and return the Deepgram's response
 *
 * @param requestBody is the Request Body for the POST request containing the URL to the wav file to Summarize
 * @param apiKey is the access token used by Deepgram to summarize
 * @return the Deepgram's response to the POST request
 * @throws Exception in case of malformed URL, I/O exception, etc.
 */
private String detectLanguage(String requestBody,String apiKey)throws Exception{
  
        String endpointUrl="https://api.deepgram.com/v1/listen?detect_language=true&punctuate=true";
        URL url=new URL(endpointUrl);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setRequestProperty("Authorization","Token "+apiKey);

        con.setDoOutput(true);

        //prepare request body
        requestBody="{"url":"requestBody"}";

        OutputStream os=con.getOutputStream();
        byte[] input=requestBody.getBytes("utf-8");
        os.write(input,0,input.length);

        StringBuilder response=new StringBuilder();

        BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
        String responseLine=null;
        responseData.put("success",false);
        while((responseLine=br.readLine())!=null){
            response.append(responseLine.trim());
        }

        br.close();
        con.disconnect();

        return response.toString();
}

/**
 * This method validates that a non-empty API key is present in variables
 *
 * @param req is the received POST request
 * @return null if non-empty API key is present, otherwise an error response
 */
private RuntimeResponse checkEmptyAPIKey(RuntimeRequest req,RuntimeResponse res,String apiKeyVariable){
        Map<String, String> variables=req.getVariables();

        if(!variables.containsKey(apiKeyVariable) ||variables.get(apiKeyVariable)==null||variables.get(apiKeyVariable).trim().isEmpty()){
            Map<String, Object> responseData=new HashMap<>();
            responseData.put("success",false);
            responseData.put("message","Please pass a non-empty API Key "+apiKeyVariable+" for Deepgram");

            return res.json(responseData);
        }

        return null;
}
/**
 * This method validates that non-empty URL is present in the payload
 *
 * @param payload is the object that contains the URL
 * @return null if payload is valid, otherwise an error response
 */
private RuntimeResponse validatePayload(Map<String, Object> payload,RuntimeResponse res){
        Map<String, Object> responseData=new HashMap<>();

        // Validate that payload has fileUrl
        if(!payload.containsKey("fileUrl")){
            responseData.put("success",false);
            responseData.put("message","Please provide a valid file URL");
            return res.json(responseData);
        }

        String fileUrl=payload.get("fileUrl").toString();

        // Validate the URL
        UrlValidator urlValidator=new UrlValidator();
        if(!urlValidator.isValid(fileUrl)){
            responseData.put("success",false);
            responseData.put("message","Provided URL: "+fileUrl+" is not valid, please provide a valid, correctly formed URL");
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
private RuntimeResponse checkEmptyPayloadAndVariables(RuntimeRequest req,RuntimeResponse res){
        Map<String, Object> responseData=new HashMap<>();

        if(req.getPayload()==null||req.getPayload().trim().isEmpty()||req.getPayload().trim().equals("{}")){
        responseData.put("success",false);
        responseData.put("message","Payload is empty, expected a payload with provider and URL");
            return res.json(responseData);
        }

        if(req.getVariables()==null){
        responseData.put("success",false);
        responseData.put("message","Empty function variables found. You need to pass an API key for the provider");
            return res.json(responseData);
        }
        return null;
}