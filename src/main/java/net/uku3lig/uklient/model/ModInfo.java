package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.uku3lig.uklient.util.ResourceManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ModInfo {
    private String id;
    private String name;
    private Provider provider;
    private List<FallbackFile> fallback;
    private List<String> dependencies;
    private List<String> config;
    @SerializedName("any-version")
    private boolean anyVersion;

    @SneakyThrows(URISyntaxException.class)
    public Map<String, Path> getConfigResourcePaths(String preset) {
        if (config == null || config.isEmpty()) return Collections.emptyMap();
        URL configURL = getClass().getClassLoader().getResource("config");
        Objects.requireNonNull(configURL);

        Path root = Paths.get(configURL.toURI()).normalize();
        Path common = root.resolve("common");

        return config.stream()
                .map(c -> {
                    Path configFile = root.resolve(preset).resolve(c);
                    if (!Files.exists(configFile)) configFile = common.resolve(c);
                    if (!Files.exists(configFile)) return null;
                    configFile = configFile.toAbsolutePath().normalize();
                    return new AbstractMap.SimpleEntry<>(c, configFile);
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void copyConfig(String preset, Path destination) {
        if (config == null || config.isEmpty()) return;
        Path mcConfigPath = destination.toAbsolutePath().normalize();
        Map<String, Path> paths = getConfigResourcePaths(preset);
        if (paths.isEmpty()) return;

        try {
            if (!Files.isDirectory(mcConfigPath)) Files.deleteIfExists(mcConfigPath);
            Files.createDirectories(mcConfigPath);

            for (Map.Entry<String, Path> resource : paths.entrySet()) {
                Path copyPath = mcConfigPath.resolve(resource.getKey());
                if (Files.isDirectory(resource.getValue())) Files.walkFileTree(resource.getValue(), ResourceManager.getVisitor(resource.getValue(), copyPath));
                else Files.copy(resource.getValue(), copyPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Could not copy config file " + config);
        }
    }

    public ModInfo(ModrinthSearchResult.Mod mod) {
        this.name = mod.getSlug();
        this.id = mod.getModId().replace("local-", "");
        this.provider = Provider.MODRINTH;
    }

    public ModInfo(CurseforgeSearchResult result) {
        this.name = result.getSlug();
        this.id = String.valueOf(result.getId());
        this.provider = Provider.CURSEFORGE;
    }

    public enum Provider {
        @SerializedName("modrinth") MODRINTH,
        @SerializedName("curseforge") CURSEFORGE
    }

    @Getter
    @AllArgsConstructor
    public static class FallbackFile {
        private String minecraftVersion;
        private String versionId;
    }
}
