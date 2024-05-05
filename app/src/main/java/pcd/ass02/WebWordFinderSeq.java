package pcd.ass02;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class WebWordFinderSeq extends WebWordFinderBase {

    private static Set<String> visitedPages = new HashSet<>();

    public static void main(String[] args) {
        var t0 = System.currentTimeMillis();
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        find(url, word, depth);
        generateReport();
        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));
    }

    static void find(String url, String word, int depth) {
        if (depth == 0 || visitedPages.contains(url)) {
            return;
        }

        visitedPages.add(url);

        try {
            Document doc = Jsoup.connect(url).get();

            String text = doc.text();
            int occurrences = WebWordFinderBase.countOccurrences(text, word);
            if (occurrences > 0)
                pageWordCounts.put(url, occurrences);

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                find(nextUrl, word, depth - 1);
            }
        } catch (IOException e) {
            ignoredUrls.add(url);
        }
    }


}
