package pcd.ass02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WebWordFinderBase {

    public static List<String> ignoredUrls =  new ArrayList<>();

    public static Map<String, Integer> pageWordCounts = new HashMap<>();

    public static int countOccurrences(String text, String word) {
        String[] words = text.split("\\s+");
        int count = 0;
        for (String w : words) {
            if (w.equalsIgnoreCase(word))
                count++;
        }
        return count;
    }

    public static void generateReport() {
        System.out.println("Report:");
        for (Map.Entry<String, Integer> entry : pageWordCounts.entrySet()) {
            System.out.println("URL: " + entry.getKey() + ", Occurrences: " + entry.getValue());
        }
        System.out.println("Ignored URLs because of IOException:");
        for (String url : ignoredUrls) {
            System.out.println(url);
        }
    }
}
