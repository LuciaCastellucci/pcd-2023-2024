package pcd.ass02.ev.multivertical3;

import java.io.Serializable;
import java.util.Map;

public class CrawlResponse implements Serializable {
    private Map<String, Integer> pageWordCount;

    public CrawlResponse(Map<String, Integer> pageWordCount) {
        this.pageWordCount = pageWordCount;
    }

    // Getters
    public Map<String, Integer> getPageWordCount() {
        return pageWordCount;
    }
}