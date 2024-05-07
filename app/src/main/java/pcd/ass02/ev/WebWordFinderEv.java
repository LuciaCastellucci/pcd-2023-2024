package pcd.ass02.ev;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebWordFinderEv {

    public static void main(String[] args) {
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new ReportVerticle(), res -> {
            vertx.deployVerticle(new FinderVerticle(url, word, depth));
        });
    }
}

class ReportVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        log("ReportVerticle:: Started.");

        EventBus eb = vertx.eventBus();
        eb.consumer("report.wordCount.success", message -> {
            ScrapeResult scrapeResult = (ScrapeResult) message.body();
            log("Occurency: " + scrapeResult.occurrences() + " for url: " + scrapeResult.url());
        });

        eb.consumer("report.wordCount.failure", message -> {
            String ignoredUrl = (String) message.body();
            log("Ignored due to IOException, url: " + ignoredUrl);
        });
        startPromise.complete();

        log("ReportVerticle:: Ready.");
    }

    private void log(String msg) {
        System.out.println("[VERTICAL] ["+Thread.currentThread()+"] " + msg);
    }
}

class FinderVerticle extends AbstractVerticle {

    private Set<String> visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private String url;
    private String word;
    private int depth;

    public FinderVerticle(String url, String word, int depth) {
        this.url = url;
        this.word = word;
        this.depth = depth;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log("ScrapingVerticle:: Started.");
        var t0 = System.currentTimeMillis();

        find(url, depth).onComplete(res -> {
            log("ScrapingVerticle:: Scraping over");
            var t1 = System.currentTimeMillis();
            log("Time elapsed: " + (t1 - t0));
        });

        startPromise.complete();
        log("ScrapingVerticle:: Ready.");
    }

    private Future<Void> find(String url, int depth) {
        if (depth == 0 || visitedPages.contains(url)) {
            return Future.succeededFuture();
        }

        visitedPages.add(url);

        Promise<Void> promise = Promise.promise();
        EventBus eventBus = vertx.eventBus();

        this.getVertx().executeBlocking(promise2 -> {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");

                eventBus.publish("report.wordCount.success", new ScrapeResult(url, countOccurrences(doc.text())));

                List<Future> futures = new ArrayList<>();
                for (Element link : links) {
                    futures.add(find(link.attr("abs:href"), depth - 1));
                }

                CompositeFuture.join(futures).onComplete(result -> {
                    if (result.succeeded()) {
                        promise2.complete();
                    } else {
                        promise2.fail(result.cause());
                    }
                });
            } catch (IOException e) {
                eventBus.publish("report.wordCount.failure", url);
                promise2.complete();
            }
        }, promise);

        return promise.future();
    }
    private int countOccurrences(String text) {
        String[] words = text.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(word::equalsIgnoreCase)
                .count();
    }

    private void log(String msg) {
        System.out.println("[VERTICAL] ["+Thread.currentThread()+"] " + msg);
    }
}

