package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class ModrinthFile {
    @SerializedName("date_published")
    private final Instant datePublished;
    private final List<ModFile> files;
    @SerializedName("game_versions")
    private final List<String> gameVersions;
    private final List<String> loaders;

    @AllArgsConstructor @Getter
    public static class ModFile {
        private final URL url;
        private final String filename;
        private final boolean primary;
    }
}
