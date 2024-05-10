package pcd.ass02.ev;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pcd.ass02.FindResult;
import pcd.ass02.GUI;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebWordFinderEv {

    private GUI gui;

    public WebWordFinderEv() {
        this.gui = null;
    }
    public WebWordFinderEv(GUI gui) {
        this.gui = gui;
    }

    public void find(String url, String word, int depth) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new ReportVerticle(gui), res -> {
            vertx.deployVerticle(new FinderVerticle(url, word, depth));
        });
    }
}

class ReportVerticle extends AbstractVerticle {

    private GUI gui;

    public ReportVerticle(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log("ReportVerticle:: Started.");

        EventBus eb = vertx.eventBus();
        eb.consumer("report.wordCount.success", message -> {
            FindResult findResult = (FindResult) message.body();
            if (gui != null) {
                gui.print(findResult);
            }
            log("Occurency: " + findResult.occurrences() + " for url: " + findResult.url());
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

        computeFinding(url, depth).onComplete(res -> {
            log("ScrapingVerticle:: Scraping over");
            var t1 = System.currentTimeMillis();
            log("Time elapsed: " + (t1 - t0));
        });

        startPromise.complete();
        log("ScrapingVerticle:: Ready.");
    }

    private Future<Void> computeFinding(String url, int depth) {
        if (depth == 0 || !visitedPages.add(url)) {
            return Future.succeededFuture();
        }

        Promise<Void> promise = Promise.promise();
        EventBus eventBus = vertx.eventBus();

        this.getVertx().executeBlocking(promise2 -> {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");

                int occurrences = countOccurrences(doc.text());
                if (occurrences > 0) {
                    eventBus.publish("report.wordCount.success", new FindResult(url, word, depth, occurrences));
                }

                List<Future> futures = new ArrayList<>();
                for (Element link : links) {
                    futures.add(computeFinding(link.attr("abs:href"), depth - 1));
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

