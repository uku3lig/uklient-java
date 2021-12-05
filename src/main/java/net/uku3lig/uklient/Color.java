package net.uku3lig.uklient;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diogonunes.jcolor.Attribute.*;

public enum Color {
    BLACK('0', BLACK_TEXT()),
    DARK_BLUE('1', BLUE_TEXT()),
    DARK_GREEN('2', GREEN_TEXT()),
    DARK_AQUA('3', CYAN_TEXT()),
    DARK_RED('4', RED_TEXT()),
    DARK_PURPLE('5', MAGENTA_TEXT()),
    GOLD('6', YELLOW_TEXT()),
    GRAY('7', TEXT_COLOR(7)),
    DARK_GRAY('8', TEXT_COLOR(8)),
    BLUE('9', BRIGHT_BLUE_TEXT()),
    GREEN('a', BRIGHT_GREEN_TEXT()),
    AQUA('b', BRIGHT_CYAN_TEXT()),
    RED('c', BRIGHT_RED_TEXT()),
    PINK('d', BRIGHT_MAGENTA_TEXT()),
    YELLOW('e', BRIGHT_YELLOW_TEXT()),
    WHITE('f', WHITE_TEXT()),

    BOLD('l', BOLD()),
    UNDERLINE('n', UNDERLINE()),
    ITALIC('o', ITALIC()),
    STRIKETHROUGH('m', STRIKETHROUGH()),

    RESET('r', CLEAR());


    public final char code;
    public final Attribute attr;

    Color(char code, Attribute attr) {
        this.code = code;
        this.attr = attr;
    }

    public String format(String source, Attribute... other) {
        if (other.length == 0) return Ansi.colorize(source, attr);
        return Ansi.colorize(source, Stream.concat(Stream.of(attr), Arrays.stream(other)).toArray(Attribute[]::new));
    }

    public static String fromFirstChar(String source, Attribute... global) {
        char first = source.charAt(0);
        return Arrays.stream(values())
                .filter(c -> c.code == first)
                .findFirst()
                .map(c -> c.format(source.substring(1), global))
                .orElse(source);
    }

    public static String parse(String source, Attribute... global) {
        return Arrays.stream(source.split("&(?=[\\da-fl-or])"))
                .filter(s -> !s.isEmpty())
                .map(s -> fromFirstChar(s, global))
                .collect(Collectors.joining(""));
    }
}
