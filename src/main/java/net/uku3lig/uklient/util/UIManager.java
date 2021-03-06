package net.uku3lig.uklient.util;

import com.diogonunes.jcolor.Attribute;

import java.util.*;
import java.util.stream.Collectors;

public class UIManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String PROMPT = "> ";

    private static final int EMPTY_INPUT = 0xB16B00BA; // big booba
    private static final int WRONG_INPUT = 0x0B00B1E5; // boobies

    private static final String WRONG_INPUT_MESSAGE = "Wrong input, please retry.";
    private static final String TOO_MANY_WRONG = "Too many wrong answers. Be gone.";

    private static final char OFF_COLOR = '1';
    private static final int COLOR_AMOUNT = getColors().size();
    private static final String LIST_FORMAT = "_[&%c%s_] &%c%s".replace("_", "&"+OFF_COLOR);
    private static final String MAP_FORMAT = "_[%s_] [&%c%s_] &%c%s".replace("_", "&"+OFF_COLOR);
    private static final String DEFAULT_FORMAT = LIST_FORMAT + String.format(" &%c(default)", OFF_COLOR);

    private static List<Character> getColors() {
        List<Character> colors = Arrays.asList('2', '6', '9', '5');
        Collections.shuffle(colors);
        return colors;
    }

    /**
     * Makes the user choose
     * @param options The options
     * @param defaultIndex The index of the default option, from 0 to <code>options.size() - 1</code>
     * @return The chosen string
     */
    public static int choice(List<String> options, int defaultIndex) {
        if (defaultIndex >= options.size())
            throw new IllegalArgumentException("defaultIndex cannot be bigger than list size!");

        printList(options, defaultIndex);
        for (int i = 0; i < 5; i++) {
            System.out.print(PROMPT);
            int answer = readInt();

            switch (answer) {
                case EMPTY_INPUT:
                    // no input, return default value
                    return defaultIndex;
                case WRONG_INPUT:
                    break;
                default:
                    if (answer > options.size()) break;
                    return answer-1;
            }
            System.out.println(WRONG_INPUT_MESSAGE);
        }
        throw new IllegalArgumentException(TOO_MANY_WRONG);
    }

    public static <T extends Enum<T>> T choiceEnum(List<T> options, int defaultIndex) {
        Map<Integer, T> map = options.stream().collect(Collectors.toMap(options::indexOf, e -> e));
        int choice = choice(map.values().stream().map(Enum::name).collect(Collectors.toList()), defaultIndex);
        return map.get(choice);
    }

    public static int choice(List<String> options) {
        printList(options);
        for (int i = 0; i < 5; i++) {
            System.out.print(PROMPT);
            int answer = readInt();

            switch (answer) {
                case EMPTY_INPUT:
                case WRONG_INPUT:
                    break;
                default:
                    if (answer > options.size()) break;
                    return answer-1;
            }

            System.out.println(WRONG_INPUT_MESSAGE);
        }
        throw new IllegalArgumentException(TOO_MANY_WRONG);
    }

    public static <T extends Enum<T>> T choiceEnum(List<T> options) {
        Map<Integer, T> map = options.stream().collect(Collectors.toMap(options::indexOf, e -> e));
        int choice = choice(map.values().stream().map(Enum::name).collect(Collectors.toList()));
        return map.get(choice);
    }

    public static boolean yesNo(String question, boolean defaultAnswer) {
        String choice = defaultAnswer ? "Y/n" : "y/N";
        System.out.printf("%s [%s] ", Color.parse("&f"+question, Attribute.BOLD()), choice);

        String answer = scanner.nextLine();
        if (answer.isEmpty()) return defaultAnswer;
        else return answer.charAt(0) == 'y';
    }

    public static Map<String, Boolean> updateSelection(Map<String, Boolean> initialElements) {
        List<Map.Entry<String, Boolean>> entries = initialElements.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList()); // make sure that entries are not immutable

        int i = 0;
        do {
            printMap(entries);
            String quit = String.format(LIST_FORMAT, 'f', "0", 'f', "Save changes & quit");
            System.out.println(Color.parse(quit, Attribute.BOLD()));
            System.out.print(PROMPT);

            int answer = readInt();
            switch (answer) {
                case EMPTY_INPUT:
                case WRONG_INPUT:
                    break;
                case 0:
                    return entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                default:
                    if (answer > entries.size()) break;
                    entries.get(answer-1).setValue(!entries.get(answer-1).getValue());
                    continue;
            }
            System.out.println(WRONG_INPUT_MESSAGE);
            i++;
        } while (i < 5);
        throw new IllegalArgumentException(TOO_MANY_WRONG);
    }

    public static String input(String query) {
        String q = String.format("%s %s", query, PROMPT);
        System.out.print(Color.parse(q, Attribute.BOLD()));
        return scanner.nextLine();
    }

    // = helper methods =

    private static int readInt() {
        String line = scanner.nextLine();
        if (line == null || line.isEmpty()) return EMPTY_INPUT;

        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return WRONG_INPUT;
        }
    }

    public static void printList(List<String> list, int defaultIndex) {
        ListIterator<String> it = list.listIterator();
        List<Character> colors = getColors();
        while (it.hasNext()) {
            char color = colors.get(it.nextIndex() % COLOR_AMOUNT);
            String formatted = String.format(it.nextIndex() == defaultIndex ? DEFAULT_FORMAT : LIST_FORMAT,
                    color, it.nextIndex()+1, color, it.next());
            System.out.println(Color.parse(formatted, Attribute.BOLD()));
        }
    }

    public static void printList(List<String> list) {
        printList(list, -1);
    }

    public static void printMap(Map<String, Boolean> elements) {
        printMap(new LinkedList<>(elements.entrySet()));
    }

    public static void printMap(List<Map.Entry<String, Boolean>> elements) {
        ListIterator<Map.Entry<String, Boolean>> it = elements.listIterator();
        List<Character> colors = getColors();
        while (it.hasNext()) {
            Map.Entry<String, Boolean> next = it.next();
            char color = colors.get(it.nextIndex() % COLOR_AMOUNT);
            String bool = Boolean.TRUE.equals(next.getValue()) ? "&a???" : "&c???";
            String formatted = String.format(MAP_FORMAT, bool, color, it.nextIndex(), color, next.getKey());
            System.out.println(Color.parse(formatted, Attribute.BOLD()));
        }
    }

    private UIManager() {}
}
