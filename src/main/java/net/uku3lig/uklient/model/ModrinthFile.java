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
    Instant datePublished;

    List<ModFile> files;

    @SerializedName("game_versions")
    List<String> gameVersions;

    List<String> loaders;

    @AllArgsConstructor @Getter
    public static class ModFile {
        URL url;
        String filename;
        boolean primary;
    }
}
