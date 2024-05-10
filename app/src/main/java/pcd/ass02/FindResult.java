package pcd.ass02;

import java.io.Serializable;

public record FindResult(String url, String word, int depth, int occurrences) implements Serializable {
};
