package net.uku3lig.uklient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.uku3lig.uklient.ResourceManager;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ModCategory {
    private String name;
    private List<String> mods;

    public List<ModInfo> getModInfos() {
        return ResourceManager.getMods().stream()
                .filter(m -> mods.contains(m.getName()))
                .collect(Collectors.toList());
    }
}