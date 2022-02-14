package net.uku3lig.uklient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class CurseforgeFile {
    private final String fileName;
    private final URL downloadUrl;
    private final Instant fileDate;
    private final List<String> gameVersion;
}
