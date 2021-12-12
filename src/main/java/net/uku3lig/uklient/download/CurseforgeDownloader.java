package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.CurseforgeFile;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class CurseforgeDownloader {
    private static final String BASE_URL = "https://addons-ecs.forgesvc.net/api/v2/";
    private static final CurseforgeRequester requester = RequestManager.supplyRetrofit(BASE_URL).create(CurseforgeRequester.class);

    public static CompletableFuture<URL> getMostRecentFile(String modId, String mcVer) {
        return requester.getFiles(modId).thenApply(l -> l.stream()
                .filter(f -> f.getGameVersion().contains(mcVer))
                .max(Comparator.comparing(CurseforgeFile::getFileDate))
                .map(CurseforgeFile::getDownloadUrl)
                .orElseThrow(NoSuchElementException::new));
    }

    public static CompletableFuture<java.nio.file.Path> download(String modId, String mcVer, File destFolder) {
        if (!destFolder.isDirectory()) throw new IllegalArgumentException(destFolder.getAbsolutePath() + " is not a folder!!!");
        return getMostRecentFile(modId, mcVer).thenCompose(u -> Downloader.download(u, Util.path(u, destFolder)));
    }

    private CurseforgeDownloader() {}

    private interface CurseforgeRequester {
        @GET("addon/{id}/files")
        CompletableFuture<List<CurseforgeFile>> getFiles(@Path("id") String modId);
    }
}
