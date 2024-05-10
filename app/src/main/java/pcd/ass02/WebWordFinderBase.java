package pcd.ass02;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class WebWordFinderBase {

    public static Set<String> ignoredUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
