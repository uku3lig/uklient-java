package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.util.Util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class ModDownloader<R> {
    private R requester;

    protected R getRequester(Class<R> klass) {
        if (requester == null) requester = RequestManager.supplyRetrofit(getBaseUrl()).create(klass);
        return requester;
    }

    public abstract String getBaseUrl();

    public abstract CompletableFuture<URL> getMostRecentFile(ModInfo mod, String ver);

    public abstract CompletableFuture<ModInfo> search(String query);

    public CompletableFuture<Path> download(ModInfo mod, String mcVer, Path destFolder, Executor e) {
        if (!Files.isDirectory(destFolder))
            throw new IllegalArgumentException(destFolder + " is not a folder!!!");
        return getMostRecentFile(mod, mcVer).thenCompose(u -> {
            if (Util.NOT_FOUND.toString().equalsIgnoreCase(u.toString())) {
                System.err.printf("\r%s does not have a file for %s%n", mod.getName(), mcVer);
                return CompletableFuture.completedFuture(null);
            }
            else return Downloader.download(u, Util.path(u, destFolder), e);
        });
    }
}
