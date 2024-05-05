package pcd.ass02.ev;

import java.io.Serializable;

public record ScrapeResult(String url, int occurrences) implements Serializable {

}
