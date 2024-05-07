package pcd.ass02;

import java.util.*;

public abstract class WebWordFinderBase {

    public static List<String> ignoredUrls =  new ArrayList<>();

    public static Map<String, Integer> pageWordCounts = new HashMap<>();

    public static int countOccurrences(String text, String word) {
        String[] words = text.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(word::equalsIgnoreCase)
                .count();
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
