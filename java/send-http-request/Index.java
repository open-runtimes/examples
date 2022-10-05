import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import okhttp3.*;
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
    LinkedTreeMap<String,String> body = null;
    LinkedTreeMap<String,String> headers = null;
    url = payload.containsKey("url") ? (String)payload.get("url") : null;
    method = payload.containsKey("method") ? (String)payload.get("method") : null;
    body = payload.containsKey("body") ? (LinkedTreeMap<String, String>) payload.get("body") : null;
    headers = payload.containsKey("headers") ? (LinkedTreeMap<String, String>) payload.get("headers") : null;

    if(url == null || method == null){
        out.put("success", false);
        out.put("message", "Custom data is in incorrect format or don't exist");
        return res.json(out);
    }

    try{

        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        if(body != null){
            for(String key : body.keySet()){
                builder = builder.add(key, body.get(key));
            }
        }
        RequestBody rbody = builder.build();

        Request.Builder rbuilder = new Request.Builder()
                .url(url);
        if(headers != null){
            for(String key : headers.keySet()){
                rbuilder = rbuilder.addHeader(key, headers.get(key));
            }
        }
        switch (method){
            case "GET":
                rbuilder = rbuilder.get();
                break;
            case "POST":
                rbuilder = rbuilder.post(rbody);
                break;
            case "PUT":
                rbuilder = rbuilder.put(rbody);
                break;
            case "PATCH":
                rbuilder = rbuilder.patch(rbody);
                break;
            case "DELETE":
                rbuilder = rbuilder.delete(rbody);
                break;
            case "HEAD":
                rbuilder = rbuilder.head();
                break;
            default:
                out.put("success", false);
                out.put("message", "HTTP method is in inccorect format");
                return res.json(out);
        }
        Request request = rbuilder.build();

        Response response = client.newCall(request).execute();

        out.put("success", true);
        out.put("response", response.body().string());
        response.close();
        return res.json(out);
    }
    catch (Exception exception){
        out.put("success", false);
        out.put("message", exception.getMessage());
        return res.json(out);
    }
}