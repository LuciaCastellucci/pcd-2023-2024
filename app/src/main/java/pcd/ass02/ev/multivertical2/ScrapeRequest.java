package pcd.ass02.ev.multivertical2;

import java.io.Serializable;

public class ScrapeRequest implements Serializable {

    private final String url;
    private final String word;
    private final int depth;

    public ScrapeRequest(String url, String word, int depth) {
        this.url = url;
        this.word = word;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public String getWord() {
        return word;
    }

    public int getDepth() {
        return depth;
    }
}
