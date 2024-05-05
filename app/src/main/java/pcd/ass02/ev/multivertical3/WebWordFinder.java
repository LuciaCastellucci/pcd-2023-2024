package pcd.ass02.ev.multivertical3;

import io.vertx.core.Vertx;

public class WebWordFinder {

    public static void main(String[] args) {
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FinderAgent(), res -> {
            /* deploy the second verticle only when the first has completed */
            vertx.deployVerticle(new CoordinatorAgent(url, word, depth));
        });
    }
}
