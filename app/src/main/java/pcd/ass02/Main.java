package pcd.ass02;

import pcd.ass02.ev.WebWordFinderEv;
import pcd.ass02.rx.WebWordFinderRx;
import pcd.ass02.vt.WebWordFinderVt;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        var t0 = System.currentTimeMillis();
        String url = "https://corsi.unibo.it/magistrale/IngegneriaScienzeInformatiche/insegnamenti/piano/2023/8614/000/000/2023";
        String word = "system";
        int depth = 2;

        //var finder = new WebWordFinderVt();
        // var finder = new WebWordFinderEv();
        var finder = new WebWordFinderRx();

        finder.find(url, word, depth);

        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));
    }
}
