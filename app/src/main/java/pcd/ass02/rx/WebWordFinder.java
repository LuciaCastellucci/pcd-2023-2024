package pcd.ass02;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebWordFinder {
    private static Map<String, Integer> wordOccurrences = new HashMap<>();
    private static List<String> ignoredUrls =  new ArrayList<>();

    public static void main(String[] args) {
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023"; // Replace with the starting web address
        String word = "system"; // Replace with the word you want to search for
        int depth = 2; // Depth level

        find(url, word, depth);
        generateReport();
    }

    private static void find(String url, String word, int depth) {
        if (depth == 0) {
            return;
        }

        try {
            Document doc = Jsoup.connect(url).get();

            String text = doc.text();
            int occurrences = countOccurrences(text, word);
            if (occurrences > 0)
                wordOccurrences.put(url, occurrences);

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                find(nextUrl, word, depth - 1);
            }
        } catch (IOException e) {
            ignoredUrls.add(url);
        }
    }

    private static int countOccurrences(String text, String word) {
        String[] words = text.split("\\s+");
        int count = 0;
        for (String w : words) {
            if (w.equalsIgnoreCase(word))
                count++;
        }
        return count;
    }

    private static void generateReport() {
        System.out.println("Report:");
        for (Map.Entry<String, Integer> entry : wordOccurrences.entrySet()) {
            System.out.println("URL: " + entry.getKey() + ", Occurrences: " + entry.getValue());
        }
        System.out.println("Ignored URLs because of IOException:");
        for (String url : ignoredUrls) {
            System.out.println(url);
        }
    }
}
