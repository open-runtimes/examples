import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import io.appwrite.Client;
import io.appwrite.services.Account;
import io.appwrite.services.Avatars;
import io.appwrite.services.Databases;
import io.appwrite.services.Functions;
import io.appwrite.services.Health;
import io.appwrite.services.Locale;
import io.appwrite.services.Storage;
import io.appwrite.services.Teams;
import io.appwrite.services.Users;

/*
  'req' variable has:
    'getHeaders()' - function to get headers as a Map<String, String>
    'getPayload()' - function to get body data as a String
    'getVariables()' - function to get variables as a Map<String, String>

  'res' variable has:
    'send(text, status)' - function that accepts a String to return text response. Status code defaults to 200
    'json(obj, status)' - function that accepts a Map<String, Object> to return JSON response. Status code defaults to 200
  
  If an error is thrown, a response with code 500 will be returned.
*/

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
    var client = new Client();

    // You can remove services you don't use
    var account = new Account(client);
    var avatars = new Avatars(client);
    var database = new Databases(client);
    var functions = new Functions(client);
    var health = new Health(client);
    var locale = new Locale(client);
    var storage = new Storage(client);
    var teams = new Teams(client);
    var users = new Users(client);

    var variables = req.getVariables();

    if (variables == null
        || !variables.containsKey("APPWRITE_FUNCTION_ENDPOINT")
        || !variables.containsKey("APPWRITE_FUNCTION_API_KEY")
        || variables.get("APPWRITE_FUNCTION_ENDPOINT") == null
        || variables.get("APPWRITE_FUNCTION_API_KEY") == null) {
        System.out.println("Environment variables are not set. Function cannot use Appwrite SDK.");
    } else {
        client
          .setEndpoint(variables.get("APPWRITE_FUNCTION_ENDPOINT"))
          .setProject(variables.get("APPWRITE_FUNCTION_PROJECT_ID"))
          .setKey(variables.get("APPWRITE_FUNCTION_API_KEY"));
    }

    return res.json(Map.of(
        "areDevelopersAwesome", true
    ));
}
