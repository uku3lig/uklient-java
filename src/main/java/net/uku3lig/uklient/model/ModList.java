package net.uku3lig.uklient.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.uku3lig.uklient.util.ResourceManager;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ModList {
    private final String name;
    @SerializedName("display_name")
    private final String displayName;
    private final List<String> mods;

    public List<ModInfo> getModInfos() {
        if (!ResourceManager.getMods().stream().allMatch(m -> mods.contains(m.getName()))) {
            mods.stream()
                    .filter(s -> {
                        try {
                            ResourceManager.getModFromName(s);
                            return false;
                        } catch (NoSuchElementException e) {
                            return true;
                        }
                    }).forEach(s -> System.err.printf("\rmod %s does not exist in preset %s!%n", s, name));
        }

        return ResourceManager.getMods().stream()
                .filter(m -> mods.contains(m.getName()))
                .collect(Collectors.toList());
    }
}
