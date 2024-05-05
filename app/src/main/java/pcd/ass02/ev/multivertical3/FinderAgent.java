package pcd.ass02.ev.multivertical3;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FinderAgent extends AbstractVerticle {
    private Set<String> visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Map<String, Integer> pageWordCounts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FinderAgent());
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log("FinderAgent started.");

        EventBus eb = this.getVertx().eventBus();
        MessageConsumer<Object> consumer = eb.consumer("crawl");
        consumer.handler(message -> {
            CrawlRequest request = (CrawlRequest) message.body();
            /*
            crawl(request.getUrl(), request.getWord(), request.getDepth()).onComplete(result -> {
                if (result.succeeded()) {
                    message.reply(result.result());
                } else {
                    message.fail(0, result.cause().getMessage());
                }
            });
            */
        });

        log("FinderAgent ready to consume crawl requests.");
        startPromise.complete();
    }

    private Future<CrawlResponse> crawl(String url, String word, int depth) {
        if (depth == 0 || visitedPages.contains(url)) {
            return Future.succeededFuture(new CrawlResponse(pageWordCounts));
        }
        log("depth: " + depth + " - start analyzing url: " + url);
        visitedPages.add(url);

        Promise<CrawlResponse> promise = Promise.promise();

        this.getVertx().executeBlocking(promise2 -> {
            try {
                log("depth: " + depth + " - start analyzing url: " + url);

                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");

                pageWordCounts.put(url, countOccurrences(doc.text(), word));

                List<Future> futures = new ArrayList<>();
                for (Element link : links) {
                    //futures.add(vertx.eventBus().request("crawl", new CrawlRequest(link.attr("abs:href"), word, depth - 1)));
                }

                CompositeFuture.join(futures).onComplete(result -> {
                    if (result.succeeded()) {
                        promise2.complete(new CrawlResponse(pageWordCounts));
                    } else {
                        promise2.fail(result.cause());
                    }
                });
            } catch (IOException e) {
                System.err.println("Error connecting to the URL: " + url);
                promise2.complete(new CrawlResponse(pageWordCounts));
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