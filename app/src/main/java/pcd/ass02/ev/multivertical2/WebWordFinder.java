package pcd.ass02.ev.multivertical2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebWordFinder {

    public static void main(String[] args) {
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new ReportVerticle(), res -> {
            vertx.deployVerticle(new ScrapingVerticle(url, word, depth));
        });
    }
}

class ReportVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        log("ReportVerticle started.");

        EventBus eb = vertx.eventBus();
        eb.consumer("report.wordCount", message -> {
            JsonObject wordCount = (JsonObject) message.body();
            System.out.println("Report: " + wordCount.encodePrettily());
        });
        startPromise.complete();

        log("ReportVerticle ready.");
    }

    private void log(String msg) {
        System.out.println("[VERTICAL] ["+Thread.currentThread()+"] " + msg);
    }
}

class ScrapingVerticle extends AbstractVerticle {

    private String url;
    private String word;

    private int MAX_DEPT;
    private int depth;

    public ScrapingVerticle(String url, String word, int depth) {
        this.url = url;
        this.word = word;
        this.MAX_DEPT = depth;
        this.depth = depth;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log("ScrapingVerticle started.");
        var t0 = System.currentTimeMillis();

        scrapePage(url, word, 0).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println("Crawling completed. Here is the report:");
                /*
                for (Map.Entry<String, Integer> entry : pageWordCounts.entrySet()) {

                    System.out.println("URL: " + entry.getKey() + ", Keyword occurrences: " + entry.getValue());
                }
                */

                var t1 = System.currentTimeMillis();
                System.out.println("Time elapsed: " + (t1 - t0));
            } else {
                System.err.println("Crawling failed: " + result.cause());
            }
        });

        startPromise.complete();
        log("ScrapingVerticle ready.");
    }

    /*
    private Future<Integer> scrapePage(String url, String word, int depth) {
        if (depth == MAX_DEPT) {
            return null;
        }
        vertx.executeBlocking(promise -> {
            try {
                Document doc = Jsoup.connect(url).get();
                String text = doc.text();

                int wordCount = countOccurrences(text, word);


                // recursively scrape linked pages
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    scrapePage(link.attr("abs:href"), word,depth + 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            promise.complete();
        }, false,
                result -> {
            vertx.eventBus().publish("report.wordCount", new JsonObject().put(url, result.result()));
        });
    }

     */

    private Future<Void> scrapePage(String url, String word, int depth) {
        Promise<Void> promise = Promise.promise();
        if (depth == MAX_DEPT) {
            promise.complete();
            return promise.future();
        }
        vertx.executeBlocking(promise2 -> {
            try {
                Document doc = Jsoup.connect(url).get();
                String text = doc.text();

                int wordCount = countOccurrences(text, word);
                vertx.eventBus().publish("report.wordCount", new JsonObject().put(url, wordCount));

                // recursively scrape linked pages
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    scrapePage(link.attr("abs:href"), word,depth + 1);
                }
            } catch (IOException e) {
                promise2.fail(e);
            }
            promise2.complete();
        }, false, res -> {
            if (res.succeeded()) {
                promise.complete();
            } else {
                promise.fail(res.cause());
            }
        });
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

