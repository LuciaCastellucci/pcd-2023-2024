package pcd.ass02.ev.monovertical;

import io.vertx.core.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebWordFinderEv extends AbstractVerticle {
    private Set<String> visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Map<String, Integer> pageWordCounts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WebWordFinderEv());
    }

    @Override
    public void start() {
        log("starting event loop... ");
        var t0 = System.currentTimeMillis();

        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        crawl(url, word, depth).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println("Crawling completed. Here is the report:");
                for (Map.Entry<String, Integer> entry : pageWordCounts.entrySet()) {
                    System.out.println("URL: " + entry.getKey() + ", Keyword occurrences: " + entry.getValue());
                }
                var t1 = System.currentTimeMillis();
                System.out.println("Time elapsed: " + (t1 - t0));
            } else {
                System.err.println("Crawling failed: " + result.cause());
            }
        });
    }

    private Future<Void> crawl(String url, String word, int depth) {
        if (depth == 0 || visitedPages.contains(url)) {
            return Future.succeededFuture();
        }

        visitedPages.add(url);

        Promise<Void> promise = Promise.promise();

        this.getVertx().executeBlocking(promise2 -> {
            try {
                //log("depth: " + depth + " - start analyzing url: " + url);

                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");

                pageWordCounts.put(url, countOccurrences(doc.text(), word));

                List<Future> futures = new ArrayList<>();
                for (Element link : links) {
                    futures.add(crawl(link.attr("abs:href"), word, depth - 1));
                }

                CompositeFuture.join(futures).onComplete(result -> {
                    if (result.succeeded()) {
                        promise2.complete();
                    } else {
                        promise2.fail(result.cause());
                    }
                });
            } catch (IOException e) {
                //System.err.println("Error connecting to the URL: " + url);
                promise2.complete();
            }
        }, promise);

        return promise.future();
    }

    public int countOccurrences(String text, String word) {
        String[] words = text.split("\\s+");
        int count = 0;
        for (String w : words) {
            if (w.equalsIgnoreCase(word))
                count++;
        }
        return count;
    }

    private void log(String msg) {
        System.out.println("[VERTICAL] ["+Thread.currentThread()+"] " + msg);
    }
}
