package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.ModCategory;
import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ResourceManager {
    private static final List<ModInfo> mods = new ArrayList<>();
    private static final List<ModCategory> categories = new ArrayList<>();

    public static List<ModInfo> getMods() {
        if (mods.isEmpty()) mods.addAll(loadMods());
        return Collections.unmodifiableList(mods);
    }

    public static List<ModCategory> getCategories() {
        if (categories.isEmpty()) categories.addAll(loadCategories());
        return Collections.unmodifiableList(categories);
    }

    public static List<ModInfo> getModsFromProvider(ModInfo.Provider provider) {
        return getMods().stream()
                .filter(m -> m.getProvider().equals(provider))
                .collect(Collectors.toList());
    }

    public static ModInfo getModFromName(String name) {
        return getMods().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static ModInfo getModFromId(String id) {
        return getMods().stream()
                .filter(m -> m.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static ModCategory getCategoryByName(String name) {
        return getCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static  CompletableFuture<List<URL>> getDependencies(Collection<ModInfo> mods, String mcVer) {
        if (mods.isEmpty()) return CompletableFuture.completedFuture(Collections.emptyList());

        Set<CompletableFuture<URL>> futures = mods.stream()
                .collect(Collectors.groupingBy(ModInfo::getProvider))
                .entrySet().stream()
                .map(e -> {
                    List<String> ids = e.getValue().stream()
                            .map(ModInfo::getDependencies)
                            .flatMap(Collection::stream)
                            .distinct()
                            .collect(Collectors.toList());
                    return new AbstractMap.SimpleEntry<>(e.getKey(), ids);
                }).map(e -> {
                    if (e.getKey().equals(ModInfo.Provider.MODRINTH)) {
                        return e.getValue().stream()
                                .map(id -> ModrinthDownloader.getMostRecentFile(id, mcVer))
                                .collect(Collectors.toList());
                    } else {
                            return e.getValue().stream()
                                    .map(id -> CurseforgeDownloader.getMostRecentFile(id, mcVer))
                                    .collect(Collectors.toList());
                    }
                }).flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return Util.allOf(futures);
    }

    private static Collection<ModInfo> loadMods() {
        // try with resources to ensure that everything closes correctly
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("mods.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = Util.getParametrized(List.class, ModInfo.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static Collection<ModCategory> loadCategories() {
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("categories.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = Util.getParametrized(List.class, ModCategory.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static FileVisitor<Path> getVisitor(Path source, Path target) {
        return new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path p = target.resolve(source.relativize(dir));
                Files.createDirectories(p);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path p = target.resolve(source.relativize(file));
                Files.copy(file, p);
                return FileVisitResult.CONTINUE;
            }
        };
    }

    private ResourceManager() {
    }
}
