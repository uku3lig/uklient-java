package net.uku3lig.uklient.download;

import lombok.SneakyThrows;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static java.nio.file.StandardOpenOption.*;

public class Downloader {
    public static CompletableFuture<Path> download(URL url, Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try (ReadableByteChannel in = Channels.newChannel(url.openStream());
                 FileChannel out = FileChannel.open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {

                URLConnection conn = url.openConnection();
                long fileSize = conn.getContentLengthLong();
                long downloaded = 0;

                while (downloaded < fileSize) downloaded += out.transferFrom(in, downloaded, fileSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        });
    }

    @SneakyThrows(MalformedURLException.class)
    public static URL url(String url) {
        return new URL(url);
    }

    public static Path path(URL url, File folder) {
        String[] strings = url.getPath().split("/");
        String filename = strings[strings.length - 1];
        return folder.toPath().resolve(filename);
    }

    public static Path getTmpDir() {
        String tmp = System.getProperty("java.io.tmpdir");
        return Paths.get(tmp);
    }

    private Downloader() {
    }
}
