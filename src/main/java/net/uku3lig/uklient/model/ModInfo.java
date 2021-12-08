package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
    }

    @Getter
    @AllArgsConstructor
    public static class FallbackFile {
        private String minecraftVersion;
        private String versionId;
    }
}
