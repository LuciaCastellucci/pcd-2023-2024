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
    private String word;
    private int depth;
    private Set<String> visitedPages;

    private WebWordFinderGUIRx gui = null;

    public record AnalyzeResult (String url, int occurences, int depth) {
    };

    public static void main(String[] args) {
        var t0 = System.currentTimeMillis();
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        WebWordFinderRx webAnalyzer = new WebWordFinderRx(word, depth);
        webAnalyzer.analyze(url);

        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));
    }

    public WebWordFinderRx(String word, int depth) {
        this.word = word;
        this.depth = depth;
        this.visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public WebWordFinderRx(String word, int depth, WebWordFinderGUIRx gui) {
        this(word, depth);
        this.gui = gui;
    }

    public void analyze(String url) {
        Observable.just(url)
                .flatMap(this::processPage)
                .subscribe(res -> log((AnalyzeResult) res), err -> log("Request ignored due to IOException, url: " + url));
    }

    private Observable<AnalyzeResult> processPage(String url) {
        return Observable.create(emitter -> {
            if (depth > 0 && !visitedPages.contains(url)) {
                visitedPages.add(url);
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    int occurrences = countOccurrences(doc.text());
                    emitter.onNext(new AnalyzeResult(url, occurrences, depth));
                    for (Element link : links) {
                        new WebWordFinderRx(word, depth - 1, gui).analyze(link.attr("abs:href"));
                    }
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
            emitter.onComplete();
        });
    }

    private int countOccurrences(String text) {
        String[] words = text.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(word::equalsIgnoreCase)
                .count();
    }

    private void log(String msg) {
        System.out.println("[" + Thread.currentThread() + "] " + msg);
    }

    private void log(AnalyzeResult result) {
        System.out.println(result.occurences + " occurences of word '" + word + "' for url: " + result.url);
        if (gui != null) {
            gui.print(result);
        }
    }
}