package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.model.ModrinthFile;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.net.URL;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModrinthDownloader {
    private static final String BASE_URL = "https://api.modrinth.com/api/v1/";
    private static final ModrinthRequester requester = RequestManager.supplyRetrofit(BASE_URL).create(ModrinthRequester.class);

    public static CompletableFuture<URL> getMostRecentFile(ModInfo mod, String mcVer) {
        return requester.getFiles(mod.getId()).thenApply(l -> l.stream()
                        .filter(f -> Util.containsMcVer(mcVer, f.getGameVersions()) || mod.isAnyVersion())
                        .filter(f -> f.getLoaders().contains("fabric"))
                        .max(Comparator.comparing(ModrinthFile::getDatePublished))
                        .map(f -> f.getFiles().stream()
                                .filter(mf -> mf.getFilename().endsWith(".jar") || mf.isPrimary())
                                .map(ModrinthFile.ModFile::getUrl)
                                .findFirst().orElse(Util.NOT_FOUND))
                        .orElse(Util.NOT_FOUND))
                .exceptionally(t -> Util.NOT_FOUND);
    }

    public static CompletableFuture<Void> download(ModInfo mod, String mcVer, java.nio.file.Path destFolder, Executor e) {
        if (!Files.isDirectory(destFolder))
            throw new IllegalArgumentException(destFolder + " is not a folder!!!");
        return getMostRecentFile(mod, mcVer).thenCompose(url -> {
            if (Util.NOT_FOUND_URI.equals(Util.uri(url))) {
                System.err.printf("%s does not have a file for %s%n", mod.getName(), mcVer);
                return CompletableFuture.completedFuture(null);
            }
            else return Downloader.download(url, Util.path(url, destFolder), e);
        });
    }

    private ModrinthDownloader() {
    }

    private interface ModrinthRequester {
        @GET("mod/{id}/version")
        CompletableFuture<List<ModrinthFile>> getFiles(@Path("id") String modId);
    }
}
