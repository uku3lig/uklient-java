package net.uku3lig.uklient;

import com.diogonunes.jcolor.Attribute;

import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class UIManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String PROMPT = "> ";

    private static final int EMPTY_INPUT = 0xB16B00BA; // big booba
    private static final int WRONG_INPUT = 0x0B00B135; // boobies

    private static final List<Character> colors = Arrays.asList('6', '2', '3', '4', '5');
    private static final char OFF_COLOR = '1';
    private static final String FORMAT = "&%c[&%c%d&%c] &%c%s";
    private static final String DEFAULT_FORMAT = FORMAT + String.format(" &%c(default)", OFF_COLOR);

    /**
     * Makes the user choose
     * @param options The options
     * @param defaultIndex The index of the default option, from 0 to <code>options.size() - 1</code>
     * @return The chosen string
     */
    public static String choice(List<String> options, int defaultIndex) {
        printList(options, defaultIndex);
        for (int i = 0; i < 5; i++) {
            System.out.print(PROMPT);
            int answer = readInt();

            switch (answer) {
                case EMPTY_INPUT:
                    // no input, return default value
                    return options.get(defaultIndex);
                case WRONG_INPUT:
                    System.out.println("Wrong input, please retry.");
                    continue;
                default:
                    return options.get(answer-1);
            }
        }
        throw new IllegalArgumentException("Too many wrong answers. Be gone.");
    }

    public static <T extends Enum<T>> T choiceEnum(List<T> options, int defaultIndex) {
        Map<String, T> map = options.stream().collect(Collectors.toMap(Enum::name, e -> e));
        String choice = choice(new ArrayList<>(map.keySet()), defaultIndex);
        return map.get(choice);
    }

    public static String choice(List<String> options) {
        printList(options);
        for (int i = 0; i < 5; i++) {
            System.out.print(PROMPT);
            int answer = readInt();

            switch (answer) {
                case EMPTY_INPUT:
                case WRONG_INPUT:
                    System.out.println("Wrong input, please retry.");
                    continue;
                default:
                    return options.get(answer-1);
            }
        }
        throw new CompletionException(new IllegalArgumentException("Too many wrong answers. Be gone."));
    }

    public static <T extends Enum<T>> T choiceEnum(List<T> options) {
        Map<String, T> map = options.stream().collect(Collectors.toMap(Enum::name, e -> e));
        String choice = choice(new ArrayList<>(map.keySet()));
        return map.get(choice);
    }

    public static boolean yesNo(String question, boolean defaultAnswer) {
        String choice = defaultAnswer ? "Y/n" : "y/N";
        System.out.printf("%s [%s] ", Color.parse("&f"+question, Attribute.BOLD()), choice);

        String answer = scanner.nextLine();
        if (answer.isEmpty()) return defaultAnswer;
        else return answer.charAt(0) == 'y';
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
        while (it.hasNext()) {
            char color = colors.get(it.nextIndex() % colors.size());
            String formatted = String.format(it.nextIndex() == defaultIndex ? DEFAULT_FORMAT : FORMAT,
            OFF_COLOR, color, it.nextIndex()+1, OFF_COLOR, color, it.next());
            System.out.println(Color.parse(formatted, Attribute.BOLD()));
        }
    }

    public static void printList(List<String> list) {
        printList(list, -1);
    }

    private UIManager() {}
}
