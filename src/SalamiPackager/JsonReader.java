package SalamiPackager;
import java.io.*;
import java.util.*;

public class JsonReader {
    public static Map<String, String> parseJson(File file) throws IOException {
        Map<String, String> result = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("\"")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].replaceAll("[\"{}]", "").trim();
                String value = parts[1].replaceAll("[\",]", "").trim();
                result.put(key, value);
            }
        }
        br.close();
        return result;
    }
    public static Map<String, String> parseJsonStream(InputStream stream) throws IOException {

        Map<String, String> result = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("\"")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].replaceAll("[\"{}]", "").trim();
                String value = parts[1].replaceAll("[\",]", "").trim();
                result.put(key, value);
            }
        }
        br.close();
        return result;
    }
}