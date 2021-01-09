package src;

public class History {

    String operation;
    Integer from;
    Integer to;
    String text;

    public History(String operation, Integer from, Integer to, String text) {
        this.operation = operation;
        this.from = from;
        this.to = to;
        this.text = text;
    }
}
