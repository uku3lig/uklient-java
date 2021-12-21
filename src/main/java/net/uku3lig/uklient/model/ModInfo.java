package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.uku3lig.uklient.download.ResourceManager;

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

    @SneakyThrows(URISyntaxException.class)
    public Map<String, Path> getConfigResourcePaths(String preset) {
        if (config == null || config.isEmpty()) return Collections.emptyMap();
        URL configURL = getClass().getClassLoader().getResource("config");
        Objects.requireNonNull(configURL);

        Path root = Paths.get(configURL.toURI()).normalize();
        Path common = root.resolve("common");

        return config.stream()
                .collect(Collectors.toMap(c -> c, c -> {
                    Path configFile = root.resolve(preset).resolve(c);
                    if (!Files.exists(configFile)) configFile = common.resolve(c);
                    return configFile.toAbsolutePath().normalize();
                }));
    }

    public void copyConfig(String preset, Path destination) {
        if (config == null || config.isEmpty()) return;
        Path mcConfigPath = destination.toAbsolutePath().normalize();

        try {
            if (!Files.isDirectory(mcConfigPath)) Files.deleteIfExists(mcConfigPath);
            Files.createDirectories(mcConfigPath);

            for (Map.Entry<String, Path> resource : getConfigResourcePaths(preset).entrySet()) {
                Path copyPath = mcConfigPath.resolve(resource.getKey());
                if (Files.isDirectory(resource.getValue())) Files.walkFileTree(resource.getValue(), ResourceManager.getVisitor(resource.getValue(), copyPath));
                else Files.copy(resource.getValue(), destination);
            }
        } catch (IOException e) {
            System.err.println("Could not copy config file " + config);
        }
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
