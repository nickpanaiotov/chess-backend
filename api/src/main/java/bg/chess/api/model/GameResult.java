package bg.chess.api.model;

import java.util.HashMap;
import java.util.Map;

public enum GameResult {
    WHITE_WON("1-0"),
    BLACK_WON("0-1"),
    DRAW("1/2-1/2"),
    ONGOING("*");

    static Map<String, GameResult> notation = new HashMap(4);
    String description;

    private GameResult(String description) {
        this.description = description;
    }

    public static GameResult fromValue(String v) {
        return valueOf(v);
    }

    public static GameResult fromNotation(String s) {
        return (GameResult)notation.get(s);
    }

    public String getDescription() {
        return this.description;
    }

    public String value() {
        return this.name();
    }

    static {
        notation.put("1-0", WHITE_WON);
        notation.put("0-1", BLACK_WON);
        notation.put("1/2-1/2", DRAW);
        notation.put("*", ONGOING);
    }
}
