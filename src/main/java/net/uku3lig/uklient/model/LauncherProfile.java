package net.uku3lig.uklient.model;

import lombok.Data;

import java.time.Instant;

@Data
public class LauncherProfile {
    private Instant created;
    private String icon;
    private String javaArgs;
    private Instant lastUsed;
    private String lastVersionId;
    private String name;
    private String type;
}
