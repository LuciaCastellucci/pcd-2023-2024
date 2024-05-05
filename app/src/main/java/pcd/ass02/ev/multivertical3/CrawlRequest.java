package pcd.ass02.ev.multivertical;

import java.io.Serializable;
import java.util.List;

public class CrawlRequest implements Serializable {
    private final List<String> url;
    private final String word;
    private final int depth;

    public CrawlRequest(List<String> url, String word, int depth) {
        this.url = url;
        this.word = word;
        this.depth = depth;
    }

    public List<String> getUrl() {
        return url;
    }

    public String getWord() {
        return word;
    }

    public int getDepth() {
        return depth;
    }
}
