package pcd.ass02.vt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WebWordFinderVt {

    public static Set<String> ignoredUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static Map<String, Integer> pageWordCounts = new HashMap<>();
    private static Set<String> visitedPages = new HashSet<>();
    private static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public void find(String url, String word, int depth) {
        computeFinding(url, word, depth);
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        generateReport();
    }

    private void computeFinding(String url, String word, int depth) {
        if (depth == 0 || visitedPages.contains(url)) {
            return;
        }
        visitedPages.add(url);
        try {
            Document doc = Jsoup.connect(url).get();

            String text = doc.text();
            int occurrences = countOccurrences(text, word);
            if (occurrences > 0) {
                pageWordCounts.put(url, occurrences);
            }

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                executor.submit(() -> computeFinding(nextUrl, word, depth - 1));
            }
        } catch (IOException e) {
            ignoredUrls.add(url);
        }
    }

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
