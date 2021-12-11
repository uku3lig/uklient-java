package net.uku3lig.uklient;

import net.uku3lig.uklient.model.ModCategory;
import net.uku3lig.uklient.model.ModInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {
    private static final List<ModInfo> mods = new ArrayList<>();
    private static final List<ModCategory> categories = new ArrayList<>();

    public static List<ModInfo> getMods() {
        if (!mods.isEmpty()) mods.addAll(loadMods());
        return Collections.unmodifiableList(mods);
    }

    public static List<ModCategory> getCategories() {
        if (categories.isEmpty()) categories.addAll(loadCategories());
        return Collections.unmodifiableList(categories);
    }

    public static List<ModInfo> getModFromProvider(ModInfo.Provider provider) {
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

    private static Collection<ModInfo> loadMods() {
        // try with resources to ensure that everything closes correctly
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("mods.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = RequestManager.getParametrized(List.class, ModInfo.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static Collection<ModCategory> loadCategories() {
        try (InputStream is = Objects.requireNonNull(ResourceManager.class.getClassLoader().getResourceAsStream("categories.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = RequestManager.getParametrized(List.class, ModCategory.class);
            return RequestManager.getGson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private ResourceManager() {
    }
}
