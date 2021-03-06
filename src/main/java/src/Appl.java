package src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@SpringBootApplication
@RestController
public class Appl {
    ArrayList<History> histories = new ArrayList();
    private static List<Letter> letterList = new ArrayList<>();
    int historyCursor = -1;
    String tmpHistoryLetter = "";

    private synchronized void addFirstHistory(@RequestBody TextDto textDto, int size) {
        histories.add(new History("add", size, size + textDto.text.length(), textDto.text)); //"add" in CONST
        historyCursor++;
    }

    private void updateHistory() {
        if (historyCursor < histories.size() - 1) {
            histories.subList(historyCursor, histories.size()).clear();
        }
    }

    private void operationChangesStyle() { //in switch
        if (operationGet().equals("bold")) {
            for (int i = getHistoryCursor(historyCursor).from; i <= getHistoryCursor(historyCursor).to; i++) {
                boolean currentStatus = letterList.get(i).isBold();
                letterList.get(i).setBold(!currentStatus);
            }
        }

        if (operationGet().equals("italic")) {
            for (int ind = getHistoryCursor(historyCursor).from; ind <= getHistoryCursor(historyCursor).to; ind++) {
                boolean currentStatus = letterList.get(ind).isItalic();
                letterList.get(ind).setItalic(!currentStatus);
            }
        }

        if (operationGet().equals("underline")) {
            for (int ind = getHistoryCursor(historyCursor).from; ind <= getHistoryCursor(historyCursor).to; ind++) {
                boolean currentStatus = letterList.get(ind).isUnderline();
                letterList.get(ind).setUnderline(!currentStatus);
            }
        }
    }


    private void operationRemove() {
        if (operationGet().equals("remove")) {
            int j = 0;
            for (int ind = getHistoryCursor(historyCursor).from; ind < getHistoryCursor(historyCursor).to; ind++) {
                letterList.add(ind, new Letter(getHistoryCursor(historyCursor).text.charAt(j)));
                j++;
            }
        }
    }

    private History getHistoryCursor(int historyCursor) {
        return histories.get(historyCursor);
    }

    private String operationGet() {
        return getHistoryCursor(historyCursor).operation;
    }

    @PostMapping(path = "/addByInd", consumes = "application/json", produces = "application/json")
    String insertByIndex(@RequestParam(name = "fromPosition") Integer fromPosition,
                         @RequestBody TextDto textDto) {
        addFirstHistory(textDto, fromPosition);
        for (int i = 0; i < textDto.text.length(); i++) {
            Letter letter = new Letter(textDto.text.charAt(i));
            letterList.add(fromPosition, letter);
            fromPosition++;
        }
        updateHistory();

        return getString();
    }


    @PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
    //this for return json. String will converted to json when return?
    @ResponseBody
    String add(@RequestBody TextDto textDto) {
        addFirstHistory(textDto, letterList.size());
        for (int i = 0; i < textDto.text.length(); i++) {
            Letter letter = new Letter(textDto.text.charAt(i));
            letterList.add(letter);
        }
        updateHistory();

        return getString();
    }


    @GetMapping(path = "/undo")
    String undo() {
        try {
            getHistoryCursor(historyCursor);
        } catch (Exception e) {
            return "No undo action";
        }
        if (operationGet().equals("add")) {
            for (int ind = getHistoryCursor(historyCursor).from; ind < getHistoryCursor(historyCursor).to; ind++) {
                letterList.remove((int) getHistoryCursor(historyCursor).from);
            }
        }
        operationRemove();
        operationChangesStyle();
        historyCursor--;
        return getString();
    }


    @GetMapping(path = "/redo")
    String redo() {
        try {
            getHistoryCursor(historyCursor + 1);
        } catch (Exception e) {
            return "No redo action";
        }
        historyCursor++;
        if (operationGet().equals("add")) {
            for (int ind = getHistoryCursor(historyCursor).from; ind < getHistoryCursor(historyCursor).to; ind++) {
                letterList.add(ind, new Letter(getHistoryCursor(historyCursor).text.charAt(historyCursor)));
            }
        }
        if (operationGet().equals("remove")) {
            for (int ind = getHistoryCursor(historyCursor).from; ind < getHistoryCursor(historyCursor).to; ind++) {
                letterList.remove(ind);
            }
        }
        operationChangesStyle();
        return getString();
    }


    @GetMapping(path = "/remove")
    String remove(@RequestParam(name = "fromPosition") Integer fromPosition,
                  @RequestParam(name = "toPosition") Integer toPosition) {
        tmpHistoryLetter = "";
        for (int i = fromPosition; i < toPosition; i++) {
            tmpHistoryLetter = tmpHistoryLetter.concat(String.valueOf(letterList.get(i).getCharacter()));
            letterList.remove((int) fromPosition);
        }
        histories.add(new History("remove", fromPosition, toPosition, tmpHistoryLetter));
        historyCursor++;
        updateHistory();
        return getString();
    }


    @GetMapping(path = "/italic")
    String Italic(@RequestParam(name = "fromPosition") Integer fromPosition,
                  @RequestParam(name = "toPosition") Integer toPosition) {
        tmpHistoryLetter = "";
        for (int i = fromPosition; i <= toPosition; i++) {
            boolean currentStatus = letterList.get(i).isItalic();
            letterList.get(i).setItalic(!currentStatus);
            tmpHistoryLetter = tmpHistoryLetter.concat(String.valueOf(letterList.get(i).getCharacter()));
        }
        histories.add(new History("italic", fromPosition, toPosition, tmpHistoryLetter));
        historyCursor++;
        updateHistory();
        return getString();
    }

    @GetMapping(path = "/underline")
    String Underline(@RequestParam(name = "fromPosition") Integer fromPosition,
                     @RequestParam(name = "toPosition") Integer toPosition) {
        tmpHistoryLetter = "";
        for (int i = fromPosition; i <= toPosition; i++) {
            boolean currentStatus = letterList.get(i).isUnderline();
            letterList.get(i).setUnderline(!currentStatus);
            tmpHistoryLetter = tmpHistoryLetter.concat(String.valueOf(letterList.get(i).getCharacter()));
        }
        histories.add(new History("underline", fromPosition, toPosition, tmpHistoryLetter));
        historyCursor++;
        updateHistory();
        return getString();
    }


    @GetMapping(path = "/bold")
    String boldStyle(@RequestParam(name = "fromPosition") Integer fromPosition,
                     @RequestParam(name = "toPosition") Integer toPosition) {
        tmpHistoryLetter = "";
        for (int i = fromPosition; i <= toPosition; i++) {
            boolean currentStatus = letterList.get(i).isBold();
            letterList.get(i).setBold(!currentStatus);
            tmpHistoryLetter = tmpHistoryLetter.concat(String.valueOf(letterList.get(i).getCharacter()));
        }
        histories.add(new History("bold", fromPosition, toPosition, tmpHistoryLetter));
        historyCursor++;
        updateHistory();
        return getString();
    }


    public static String getString() {
        StringBuilder sb = new StringBuilder();
        boolean isBold = letterList.get(0).isBold();
        boolean isUnderline = letterList.get(0).isUnderline();
        boolean isItalic = letterList.get(0).isItalic();
        char character = letterList.get(0).getCharacter();

        if (letterList.size() == 0) {
            return "";
        }
        if (isBold) {
            sb.append("<strong>");
        }
        if (isUnderline) {
            sb.append("<underline>");
        }
        if (isItalic) {
            sb.append("<italic>");
        }
        sb.append(character);

        for (int i = 1; i < letterList.size(); i++) {
            Letter letter = letterList.get(i);
            boolean currentBold = letter.isBold();
            boolean currentUnderline = letter.isUnderline();
            boolean currentItalic = letter.isItalic();
            char currentChar = letter.getCharacter();

            if (currentItalic != isItalic) {
                sb.append(isItalic ? "</italic>" : "<italic>");
                isItalic = !isItalic;
            }

            if (currentUnderline != isUnderline) {
                sb.append(isUnderline ? "</underline>" : "<underline>");
                isUnderline = !isUnderline;
            }

            if (currentBold != isBold) {
                sb.append(isBold ? "</strong>" : "<strong>");
                isBold = !isBold;
            }
            sb.append(currentChar);
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        SpringApplication.run(Appl.class, args);


    }
}
