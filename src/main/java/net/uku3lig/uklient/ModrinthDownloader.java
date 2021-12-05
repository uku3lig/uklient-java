package net.uku3lig.uklient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.uku3lig.uklient.model.ModrinthFile;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModrinthDownloader {
    private static final Gson gson = new Gson();
    private static final Map<String, String> mods = new HashMap<>();

    private static final String BASE_URL = "https://api.modrinth.com/api/v1/";
    private static final ModrinthRequester requester = RequestManager.supplyRetrofit(BASE_URL).create(ModrinthRequester.class);

    public static String getModID(String name) {
        if (mods.isEmpty()) loadMods();
        return mods.get(name);
    }

    public static CompletableFuture<URL> getMostRecentFile(String modId, String mcVer) {
        return requester.getFiles(modId).thenApply(l -> l.stream()
                .filter(f -> f.getGameVersions().contains(mcVer))
                .filter(f -> f.getLoaders().contains("fabric"))
                .max(Comparator.comparing(ModrinthFile::getDatePublished))
                .map(f -> f.getFiles().stream()
                        .filter(ModrinthFile.ModFile::isPrimary)
                        .map(ModrinthFile.ModFile::getUrl).
                        findFirst().orElseThrow(NoSuchElementException::new))
                .orElseThrow(NoSuchElementException::new));
    }

    public static CompletableFuture<java.nio.file.Path> download(String modId, String mcVer, File destFolder) {
        if (!destFolder.isDirectory()) throw new IllegalArgumentException(destFolder.getAbsolutePath() + " is not a folder!!!");
        return getMostRecentFile(modId, mcVer).thenCompose(url -> Downloader.download(url, Downloader.path(url, destFolder)));
    }

    private static void loadMods() {
        InputStream in = ModrinthDownloader.class.getClassLoader().getResourceAsStream("modrinth.json");
        if (in == null) throw new NullPointerException("Error: could not find file modrinth.json");

        try (InputStreamReader reader = new InputStreamReader(in)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            Map<String, String> map = root.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getAsString()));

            mods.putAll(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ModrinthDownloader() {}

    private interface ModrinthRequester {
        @GET("mod/{id}/version")
        CompletableFuture<List<ModrinthFile>> getFiles(@Path("id") String modId);
    }
}
