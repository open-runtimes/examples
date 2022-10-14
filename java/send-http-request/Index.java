import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final Gson gson = new Gson();
final Map<String, Object> out = new HashMap<>();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
    String payloadString = req.getPayload() == null || req.getPayload().isEmpty() 
        ? "{}" 
        : req.getPayload();

    Map<String, Object> payload = gson.fromJson(payloadString, Map.class);

    String url = null;
    String method = null;
    String body = null;
    String content_type = null;
    LinkedTreeMap<String,String> headers = null;
    url = payload.containsKey("url") ? (String)payload.get("url") : null;
    method = payload.containsKey("method") ? (String)payload.get("method") : null;
    body = payload.containsKey("body") ? (String)payload.get("body") : null;
    content_type = payload.containsKey("content_type") ? (String)payload.get("content_type") : null;
    headers = payload.containsKey("headers") ? (LinkedTreeMap<String, String>) payload.get("headers") : null;

    if(url == null || method == null){
        out.put("success", false);
        out.put("message", "Custom data is in incorrect format or don't exist");
        //return res.json(out);
        System.out.println("Custom data is in incorrect format or don't exist");
    }

    if(!method.equals("GET") && !method.equals("POST") && !method.equals("PUT") &&
            !method.equals("PATCH") && !method.equals("DELETE") && !method.equals("HEAD")){
        out.put("success", false);
        out.put("message", "Method is in incorrect type");
        //return res.json(out);
        System.out.println("Method is in incorrect type");
    }

    try{

        URL urll = new URL(url);
        HttpURLConnection http = (HttpURLConnection)urll.openConnection();
        http.setRequestMethod(method);

        for(String key : headers.keySet()){
            http.setRequestProperty(key, headers.get(key));
        }

        if(body != null){
            http.setRequestProperty("Content-Type", content_type);
            http.setDoOutput(true);
            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
        }

        String response = formatResponse(http);

        out.put("success", true);
        out.put("response", response);
        http.disconnect();
        return res.json(out);
    }
    catch (Exception exception){
        out.put("success", false);
        out.put("message", exception.getMessage());
        return res.json(out);
    }
}

private static String formatResponse(HttpURLConnection http) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader((http.getInputStream())));
    StringBuilder sb = new StringBuilder();
    String output;
    while ((output = br.readLine()) != null) {
        sb.append(output);
    }
    return sb.toString();
}