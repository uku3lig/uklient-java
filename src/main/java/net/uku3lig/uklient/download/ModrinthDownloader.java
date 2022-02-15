package net.uku3lig.uklient.download;

import lombok.Getter;
import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.model.ModrinthFile;
import net.uku3lig.uklient.model.ModrinthSearchResult;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModrinthDownloader extends ModDownloader<ModrinthDownloader.ModrinthRequester> {
    @Getter
    private static final ModrinthDownloader instance = new ModrinthDownloader();

    @Override
    public String getBaseUrl() {
        return "https://api.modrinth.com/api/v1/";
    }

    public CompletableFuture<URL> getMostRecentFile(ModInfo mod, String mcVer) {
        return getRequester(ModrinthRequester.class).getFiles(mod.getId()).thenApply(l -> l.stream()
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

    public CompletableFuture<ModInfo> search(String query) {
        return getRequester(ModrinthRequester.class).search(query)
                .thenApply(r -> r.getHits().isEmpty() ? null : r.getHits().get(0))
                .thenApply(m -> m == null ? null : new ModInfo(m));
    }

    private ModrinthDownloader() {
    }

    public interface ModrinthRequester {
        @GET("mod/{id}/version")
        CompletableFuture<List<ModrinthFile>> getFiles(@Path("id") String modId);

        @GET("/mod?limit=1")
        CompletableFuture<ModrinthSearchResult> search(@Query("query") String query);
    }
}
