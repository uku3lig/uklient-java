package net.uku3lig.uklient.util;

import net.uku3lig.uklient.download.RequestManager;
import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.model.NamedModList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {
    private static final List<ModInfo> mods = new ArrayList<>();
    private static final List<NamedModList> categories = new ArrayList<>();
    private static final List<NamedModList> presets = new ArrayList<>();

    // MOD RELATED METHODS

    public static List<ModInfo> getMods() {
        if (mods.isEmpty()) mods.addAll(loadMods());
        return Collections.unmodifiableList(mods);
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

    public static Collection<ModInfo> addDependencies(Collection<ModInfo> mods) {
        if (mods == null || mods.isEmpty()) return new ArrayList<>();
        if (mods.stream().map(ModInfo::getDependencies).filter(Objects::nonNull).mapToLong(Collection::size).sum() == 0) return mods;

        return mods.stream()
                .map(ResourceManager::getDependencies)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Collection<ModInfo> getDependencies(ModInfo m) {
        if (m.getDependencies() == null) return new ArrayList<>(Collections.singleton(m));

        List<ModInfo> deps = m.getDependencies().stream()
                .map(ResourceManager::getModFromName)
                .collect(Collectors.toList());

        Collection<ModInfo> c = addDependencies(deps);
        c.add(m);
        return c;
    }

    // CATEGORY RELATED METHODS

    public static List<NamedModList> getCategories() {
        if (categories.isEmpty()) categories.addAll(loadNamedModList("categories"));
        return Collections.unmodifiableList(categories);
    }

    public static NamedModList getCategoryByName(String name) {
        return getCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    // PRESET METHODS

    public static List<NamedModList> getPresets() {
        if (presets.isEmpty()) presets.addAll(loadNamedModList("presets"));
        return Collections.unmodifiableList(presets);
    }

    public static NamedModList getPresetByName(String name) {
        return getPresets().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    // UTIL METHODS

    private static Collection<ModInfo> loadMods() {
        // try with resources to ensure that everything closes correctly
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("mods.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = Util.getParametrized(List.class, ModInfo.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Could not load mods. please retry later");
            System.exit(1);
        }
        return Collections.emptyList();
    }

    private static Collection<NamedModList> loadNamedModList(String filename) {
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream(filename + ".json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = Util.getParametrized(List.class, NamedModList.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Could not load categories. please retry later");
            System.exit(1);
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
                Files.copy(file, p, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        };
    }

    private ResourceManager() {
    }
}