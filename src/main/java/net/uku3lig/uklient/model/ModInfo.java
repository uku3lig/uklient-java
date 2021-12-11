package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private ConfigInfo config;

    public enum Provider {
        @SerializedName("modrinth")
        MODRINTH,
        @SerializedName("curseforge")
        CURSEFORGE
    }

    public enum ConfigType {
        @SerializedName("directory")
        DIRECTORY,
        @SerializedName("file")
        FILE
    }

    @Getter
    @AllArgsConstructor
    public static class ConfigInfo {
        private ConfigType type;
        private String name;

        public Path getFile(String preset) {
            URL configURL = getClass().getClassLoader().getResource("config");
            Objects.requireNonNull(configURL);

            Path root = Paths.get(configURL.getPath()).normalize();

            Path configFile = root.resolve(preset + File.separator + name);
            if (!Files.exists(configFile)) configFile = root.resolve("common" + File.separator + name);

            return configFile.toAbsolutePath().normalize();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class FallbackFile {
        private String minecraftVersion;
        private String versionId;
    }
}
