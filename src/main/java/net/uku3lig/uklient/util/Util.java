package net.uku3lig.uklient.util;

import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Util {
    public static final URL NOT_FOUND = url("http://not.found");
    public static final URI NOT_FOUND_URI = uri(NOT_FOUND);
    public static final String SHORT_VER_PATTERN = "^1\\.\\d{1,2}$";

    public static <T> CompletableFuture<List<T>> allOf(Collection<CompletableFuture<T>> futures) {
        return accumulate(futures, Collectors.toList());
    }

    // I would NEVER steal code from jda
    // never
    // https://github.com/DV8FromTheWorld/JDA/blob/master/src/main/java/net/dv8tion/jda/api/requests/RestAction.java#L356
    private static <E, A, O> CompletableFuture<O> accumulate(Collection<CompletableFuture<E>> futures, Collector<E, A, O> collector) {
        Supplier<A> accumulator = collector.supplier();
        BiConsumer<A, ? super E> add = collector.accumulator();
        Function<A, ? extends O> output = collector.finisher();

        futures = new LinkedHashSet<>(futures);
        Iterator<? extends CompletableFuture<? extends E>> iterator = futures.iterator();
        CompletableFuture<A> result = iterator.next().thenApply(it -> {
            A list = accumulator.get();
            add.accept(list, it);
            return list;
        });

        while (iterator.hasNext())
        {
            CompletableFuture<? extends E> next = iterator.next();
            result = result.thenCombine(next, (list, b) -> {
                add.accept(list, b);
                return list;
            });
        }

        return result.thenApply(output);
    }

    public static boolean containsMcVer(String userMcVer, Collection<String> modMcVer) {
        String shortVer = getShortVer(userMcVer);
        return modMcVer.stream().anyMatch(s -> s.equalsIgnoreCase(userMcVer) || s.equalsIgnoreCase(shortVer));
    }

    public static String getShortVer(String mcVer) {
        if (Pattern.matches(SHORT_VER_PATTERN, mcVer)) return mcVer;
        return mcVer.substring(0, mcVer.lastIndexOf('.'));
    }

    @SneakyThrows(MalformedURLException.class)
    public static URL url(String url) {
        return new URL(url);
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI uri(URL url) {
        return url.toURI();
    }

    public static Path path(URL url, Path folder) {
        String[] strings = url.getPath().split("/");
        String filename = strings[strings.length - 1];
        return folder.resolve(filename);
    }

    public static Path getTmpDir() {
        String tmp = System.getProperty("java.io.tmpdir");
        return Paths.get(tmp);
    }

    public static Type getParametrized(Class<?> main, Class<?> parameter) {
        Type mainType = TypeToken.get(main).getType();
        Type parameterType = TypeToken.get(parameter).getType();
        return TypeToken.getParameterized(mainType, parameterType).getType();
    }

    private Util() {}
}
