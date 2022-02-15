package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModrinthSearchResult {
    private final List<Mod> hits;

    @Getter
    @AllArgsConstructor
    public static class Mod {
        @SerializedName("mod_id")
        private final String modId;
        private final String slug;
    }
}
