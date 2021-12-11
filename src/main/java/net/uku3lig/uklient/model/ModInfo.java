package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.uku3lig.uklient.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class ModInfo {
    private String id;
    private String name;
    private Provider provider;
    private List<FallbackFile> fallback;
    private List<String> dependencies;
    private String config;

    public Path getConfigPath(String preset) {
        URL configURL = getClass().getClassLoader().getResource("config");
        Objects.requireNonNull(configURL);

        Path root = Paths.get(configURL.getPath()).normalize();

        Path configFile = root.resolve(preset + File.separator + config);
        if (!Files.exists(configFile)) configFile = root.resolve("common" + File.separator + config);

        return configFile.toAbsolutePath().normalize();
    }

    public void copyConfig(String preset, Path destination) {
        try {
            destination = destination.toAbsolutePath().normalize();
            Path configPath = getConfigPath(preset);

            if (!Files.isDirectory(destination)) Files.deleteIfExists(destination);
            Files.createDirectories(destination);

            destination = destination.resolve(config);

            if (Files.isDirectory(configPath)) Files.walkFileTree(configPath, ResourceManager.getVisitor(configPath, destination));
            else Files.copy(configPath, destination);
        } catch (IOException e) {
            e.printStackTrace();
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