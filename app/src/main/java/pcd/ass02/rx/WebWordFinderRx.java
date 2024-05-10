package pcd.ass02.rx;

import io.reactivex.rxjava3.core.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebWordFinderRx {
    private Set<String> visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private WebWordFinderGUIRx gui = null;

    public record FindResult(String url, String word, int depth, int occurrences) {
    };

    public static void main(String[] args) {
        var t0 = System.currentTimeMillis();
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        WebWordFinderRx webAnalyzer = new WebWordFinderRx();
        webAnalyzer.find(url, word, depth);

        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));
    }

    public WebWordFinderRx setGUI(WebWordFinderGUIRx gui) {
        this.gui = gui;
        return this;
    }

    public void find(String url, String word, int depth) {
        Observable.just(url)
                .flatMap(page -> computeFinding(url, word, depth))
                .subscribe(res -> log((FindResult) res), err -> log("Request ignored due to IOException, url: " + url + "\n" + err));
    }

    private Observable<FindResult> computeFinding(String url, String word, int depth) {
        return Observable.create(emitter -> {
            if (depth > 0 && !visitedPages.contains(url)) {
                visitedPages.add(url);
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    int occurrences = countOccurrences(doc.text(), word);
                    emitter.onNext(new FindResult(url, word, depth, occurrences));
                    for (Element link : links) {
                        new WebWordFinderRx().setGUI(gui).find(link.attr("abs:href"), word, depth - 1);
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());
                    emitter.onError(e);

                }
            }
            emitter.onComplete();
        });
    }

    private int countOccurrences(String text, String word) {
        String[] words = text.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(word::equalsIgnoreCase)
                .count();
    }

    private void log(String msg) {
        System.out.println("[" + Thread.currentThread() + "] " + msg);
    }

    private void log(FindResult result) {
        System.out.println(result.occurrences + " occurrences of word '" + result.word + "' for url: " + result.url);
        if (gui != null) {
            gui.print(result);
        }
    }
}