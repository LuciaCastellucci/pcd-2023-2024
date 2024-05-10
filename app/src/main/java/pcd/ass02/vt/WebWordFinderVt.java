package pcd.ass02.vt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pcd.ass02.WebWordFinderBase;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WebWordFinderVt extends WebWordFinderBase {

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
}
