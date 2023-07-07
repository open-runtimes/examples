package java.collection_wiper;
import okhttp3.*;
import java.io.IOException;

public AppwriteCollectionWiper {
    private static final String API_ENDPOINT = "http://localhost/v1";
    private static final String PROJECT_ID = "649f0fde61b9f6d9d83c";
    private static final String COLLECTION_ID = "";
    private static final String API_KEY = "9761bd7ab2d242e1e1343cf2757e51a2f28befda464a168947f308fca1cf4d98f4d749b90291ff3d35155025018c856aeaf3d597cb1ec6ad7ce067b0d10e87eb586b2f9ca3e0da9c81a8509acdefae6c2632f95ea4f6986bdaffe8a8dc15eaa40332ff7fd059b00f6aad1ef92618a903aa7b6b273903e3c750296b75d227b182";

    public static void main(String[] args) {
        wipeAppwriteCollection();
    }

    public static void wipeAppwriteCollection() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_ENDPOINT + "/database/collections/" + COLLECTION_ID + "/documents")
                .addHeader("X-Appwrite-Project", PROJECT_ID)
                .addHeader("X-Appwrite-Key", API_KEY)
                .delete()
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                System.out.println("Collection wiped successfully.");
            } else {
                System.out.println("Failed to wipe collection. Error: " + response.body().string());
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
