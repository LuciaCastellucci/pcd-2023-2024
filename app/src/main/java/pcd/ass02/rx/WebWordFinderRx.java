package pcd.ass02.rx;

import io.reactivex.rxjava3.core.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pcd.ass02.FindResult;
import pcd.ass02.Flag;
import pcd.ass02.GUI;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebWordFinderRx {
    private Set<String> visitedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private GUI gui;

    private Flag stopFlag;

    public WebWordFinderRx() {
        this.gui = null;
    }
    public WebWordFinderRx(GUI gui, Flag stopFlag) {
        this.gui = gui;
        this.stopFlag = stopFlag;
    }

    public void find(String url, String word, int depth) {
        Observable.just(url)
                .flatMap(page -> computeFinding(url, word, depth))
                .subscribe(res -> log((FindResult) res), err -> log("Request ignored due to IOException, url: " + url + "\n" + err));
    }

    private Observable<FindResult> computeFinding(String url, String word, int depth) {
        return Observable.create(emitter -> {
            if (!stopFlag.isSet() && depth > 0 && visitedPages.add(url)) {
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    int occurrences = countOccurrences(doc.text(), word);
                    if (occurrences > 0) {
                        emitter.onNext(new FindResult(url, word, depth, occurrences));
                    }
                    for (Element link : links) {
                        find(link.attr("abs:href"), word, depth - 1);
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
        System.out.println(result.occurrences() + " occurrences of word '" + result.word() + "' for url: " + result.url());
        if (gui != null) {
            gui.print(result);
        }
    }
}