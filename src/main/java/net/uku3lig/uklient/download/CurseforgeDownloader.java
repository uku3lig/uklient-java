package net.uku3lig.uklient.download;

import lombok.Getter;
import net.uku3lig.uklient.model.CurseforgeFile;
import net.uku3lig.uklient.model.CurseforgeSearchResult;
import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CurseforgeDownloader extends ModDownloader<CurseforgeDownloader.CurseforgeRequester> {
    @Getter
    private static final CurseforgeDownloader instance = new CurseforgeDownloader();

    @Override
    public String getBaseUrl() {
        return "https://addons-ecs.forgesvc.net/api/v2/addon/";
    }

    public CompletableFuture<URL> getMostRecentFile(ModInfo mod, String mcVer) {
        return getRequester(CurseforgeRequester.class).getFiles(mod.getId()).thenApply(l -> l.stream()
                        .filter(f -> Util.containsMcVer(mcVer, f.getGameVersion()) || mod.isAnyVersion())
                        .filter(f -> f.getGameVersion().contains("Fabric"))
                        .max(Comparator.comparing(CurseforgeFile::getFileDate))
                        .map(CurseforgeFile::getDownloadUrl)
                        .orElse(Util.NOT_FOUND))
                .exceptionally(t -> Util.NOT_FOUND);
    }

    public CompletableFuture<ModInfo> search(String query) {
        return getRequester(CurseforgeRequester.class)
                .search(query).thenApply(l -> l.isEmpty() ? null : new ModInfo(l.get(0)));
    }

    private CurseforgeDownloader() {
    }

    public interface CurseforgeRequester {
        @GET("{id}/files")
        CompletableFuture<List<CurseforgeFile>> getFiles(@Path("id") String modId);

        @GET("search?gameId=432&sectionId=6&pageSize=1")
        CompletableFuture<List<CurseforgeSearchResult>> search(@Query("searchFilter") String query);
    }
}
