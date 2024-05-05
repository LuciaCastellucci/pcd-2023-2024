package pcd.ass02.ev.multivertical3;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

import java.util.List;

public class CoordinatorAgent extends AbstractVerticle {

    String url;
    String word;
    int depth;

    public CoordinatorAgent(String url, String word, int depth) {
        this.url = url;
        this.word = word;
        this.depth = depth;
    }
    @Override
    public void start() {
        log("CoordinatorAgent started.");

        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        sendRequest(new CrawlRequest(List.of(url), word, depth));

        vertx.eventBus().consumer("new-agent", message -> {
            CrawlRequest request = (CrawlRequest) message.body();
            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(new FinderAgent());
            sendRequest(new CrawlRequest(request.getUrl(), request.getWord(), request.getDepth()));
        });
    }

    private void log(String msg) {
        System.out.println("[VERTICAL] ["+Thread.currentThread()+"] " + msg);
    }

    private void sendRequest(CrawlRequest request) {
        vertx.eventBus().request("crawl", request, (Handler<AsyncResult<Message<CrawlResponse>>>) reply -> {
            if (reply.succeeded()) {
                System.out.println("Crawling completed. Here is the report:");
                reply.result().body().getPageWordCount().entrySet().stream().map(
                        entry -> "URL: " + entry.getKey() + ", Keyword occurrences: " + entry.getValue()).forEach(System.out::println);
            } else {
                System.err.println("Crawling failed: " + reply.cause());
            }
        });
    }

}


