import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThingSpeakIntegration {

    private static final String WRITE_API_KEY = "D80N38JNLTU6S7VK";
    private static final String READ_API_KEY = "OS9VH93GGW1NANFI";
    private static final String THINGSPEAK_URL = "https://api.thingspeak.com/update.json";
    private static final int CHANNEL_ID = 2917497;

    // Sends sensor data to ThingSpeak
    public void sendDataToThingSpeak(double temperature, double humidity, double moisture) {
        try {
            String urlString = THINGSPEAK_URL + "?api_key=" + WRITE_API_KEY +
                               "&field1=" + temperature +
                               "&field2=" + humidity +
                               "&field3=" + moisture;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaApp");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("✅ Data updated to ThingSpeak successfully!");
            } else {
                System.out.println("⚠️ Failed to update data. HTTP Code: " + responseCode);
            }

        } catch (IOException e) {
            System.err.println("❌ Exception sending to ThingSpeak:");
            e.printStackTrace();
        }
    }

    // Checks internet connectivity
    public boolean isInternetAvailable() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://clients3.google.com/generate_204").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000); // 1 second timeout
            conn.connect();
            return conn.getResponseCode() == 204;
        } catch (IOException e) {
            return false;
        }
    }

    // Fetches latest data from ThingSpeak (optional for read access or GUI display)
    public static String fetchDataFromThingSpeak() {
        try {
            String urlString = "https://api.thingspeak.com/channels/" + CHANNEL_ID + "/feeds.json?api_key=" + READ_API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaApp");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}




/*import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThingSpeakIntegration {

    // ThingSpeak Write & Read API Keys
    private static final String WRITE_API_KEY = "D80N38JNLTU6S7VK";
    private static final String READ_API_KEY = "OS9VH93GGW1NANFI";
    private static final String THINGSPEAK_URL = "https://api.thingspeak.com/update.json";
    private static final int CHANNEL_ID = 2917497;

    // Method to update data to ThingSpeak
    public void sendDataToThingSpeak(double temperature, double humidity, double moisture) {
        try {
            String urlString = THINGSPEAK_URL + "?api_key=" + WRITE_API_KEY +
                               "&field1=" + temperature +
                               "&field2=" + humidity +
                               "&field3=" + moisture;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data updated successfully to ThingSpeak!");
            } else {
                System.out.println("Failed to update data to ThingSpeak. HTTP Code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to check internet connection
    public boolean isInternetAvailable() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000); // 1 second timeout
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Method to fetch the latest data from ThingSpeak
    public static String fetchDataFromThingSpeak() {
        try {
            String urlString = "https://api.thingspeak.com/channels/" + CHANNEL_ID + "/feeds.json?api_key=" + READ_API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Main method to run from command prompt
    public static void main(String[] args) {
        ThingSpeakIntegration ts = new ThingSpeakIntegration();

        if (ts.isInternetAvailable()) {
            // Sample data: temp, humidity, moisture
            ts.sendDataToThingSpeak(27.5, 60.3, 45.0);

            String data = fetchDataFromThingSpeak();
            System.out.println("\nFetched Data from ThingSpeak:");
            System.out.println(data);
        } else {
            System.out.println("No internet connection.");
        }
    }
}
*/

