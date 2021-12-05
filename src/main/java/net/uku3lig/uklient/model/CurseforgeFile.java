package net.uku3lig.uklient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class CurseforgeFile {
    private String fileName;
    private URL downloadUrl;
    private Instant fileDate;
    private List<String> gameVersion;
}
