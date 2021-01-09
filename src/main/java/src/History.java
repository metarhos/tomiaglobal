package src;

public class History {
    public History(String operation, Integer from, Integer to, String text) {
        this.operation = operation;
        this.from = from;
        this.to = to;
        this.text = text;
    }

    String operation;
    Integer from;
    Integer to;
    String text;
}
