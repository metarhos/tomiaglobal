package src;

public class TextDto {
    public TextDto(String text) {
        this.text = text;
    }

    public TextDto() {
    }

    @Override
    public String toString() {
        return "TextDto{" +
                "text='" + text + '\'' +
                '}';
    }

    public String text;

}
