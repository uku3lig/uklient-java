package net.uku3lig.uklient.download;

import lombok.SneakyThrows;

import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.nio.file.StandardOpenOption.*;

public class Downloader {
    @SneakyThrows
    public static CompletableFuture<Void> download(URL url, Path path, Executor executor) {
        Path dir = path.getParent();
        if (!Files.isDirectory(dir)) {
            Files.createDirectories(dir);
        }
        return CompletableFuture.supplyAsync(() -> {
            try (ReadableByteChannel in = Channels.newChannel(url.openStream());
                 FileChannel out = FileChannel.open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {

                URLConnection conn = url.openConnection();
                long fileSize = conn.getContentLengthLong();
                long downloaded = 0;

                while (downloaded < fileSize) downloaded += out.transferFrom(in, downloaded, fileSize);
            } catch (Exception e) {
                System.err.println("An error occurred while downloading file " + url);
                System.err.println("Please retry later");
                System.exit(1);
            }
            return null;
        }, executor);
    }

    private Downloader() {
    }
}
