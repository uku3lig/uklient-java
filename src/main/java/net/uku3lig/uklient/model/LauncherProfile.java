package net.uku3lig.uklient.model;

import lombok.Data;

import java.time.Instant;

@Data
public class LauncherProfile {
    private Instant created;
    private Instant lastPlayed;
    private String icon;
    private String gameDir;
    private String lastVersionId;
    private String name;
    private String type;
}
