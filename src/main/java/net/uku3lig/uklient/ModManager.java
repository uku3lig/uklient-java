package net.uku3lig.uklient;

import net.uku3lig.uklient.model.ModInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ModManager {
    private static final List<ModInfo> mods = new ArrayList<>();

    public static List<ModInfo> getMods() {
        if (mods.isEmpty()) mods.addAll(loadMods());
        return mods;
    }

    public static List<ModInfo> getFromProvider(ModInfo.Provider provider) {
        return getMods().stream()
                .filter(m -> m.getProvider().equals(provider))
                .collect(Collectors.toList());
    }

    public static ModInfo getFromName(String name) {
        return getMods().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static ModInfo getFromId(String id) {
        return getMods().stream()
                .filter(m -> m.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private static Collection<ModInfo> loadMods() {
        // try with resources to ensure that everything closes correctly
        try (InputStream is = Objects.requireNonNull(ModManager.class.getClassLoader().getResourceAsStream("mods.json"));
             Reader reader = new InputStreamReader(is)) {
            Type listType = RequestManager.getParametrized(List.class, ModInfo.class);
            return RequestManager.getGson().<List<ModInfo>>fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private ModManager() {}
}
