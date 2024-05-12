package pcd.ass02;

import pcd.ass02.ev.WebWordFinderEv;
import pcd.ass02.rx.WebWordFinderRx;
import pcd.ass02.vt.WebWordFinderVt;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche";
        String word = "sistema";
        int depth = 2;

        var finder = new WebWordFinderRx();
        // var finder = new WebWordFinderVt();
        // var finder = new WebWordFinderEv();

        finder.find(url, word, depth);
    }
}
