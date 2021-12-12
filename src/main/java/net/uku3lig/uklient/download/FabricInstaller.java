package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.FabricInstallerMetadata;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class FabricInstaller {
    private static final String BASE_URL = "https://maven.fabricmc.net/net/fabricmc/fabric-installer/";
    private static final FabricRequester requester = RequestManager.supplyRetrofit(BASE_URL).create(FabricRequester.class);

    private static final String URL_FORMAT = BASE_URL + "%s/fabric-installer-%s.jar";

    public static CompletableFuture<URL> getLatestFabricInstaller() {
        return requester.getMetadata().thenApply(m -> {
            String ver = m.getVersioning().getRelease();
            String url = String.format(URL_FORMAT, ver, ver);
            return Util.url(url);
        });
    }

    public static CompletableFuture<Void> installFabric(String mcVer, Path installDir) {
        return getLatestFabricInstaller().thenCompose(u -> Downloader.download(u, Util.getTmpDir().resolve("fabric.jar")))
                .thenAccept(path -> {
                    String[] command = getInstallCommand(path, mcVer, installDir);
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        Process proc = builder.inheritIO().start();
                        proc.waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    private static String[] getInstallCommand(Path toInstaller, String mcVer, Path installDir) {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        LinkedList<String> command = new LinkedList<>();
        command.add(java);
        command.add("-jar");
        command.add(toInstaller.toAbsolutePath().normalize().toString());
        command.add("client"); // install fabric for client

        command.add("-noprofile"); // do not create a profile in mc launcher

        command.add("-dir");
        command.add(installDir.toAbsolutePath().normalize().toString());

        command.add("-mcversion");
        command.add(mcVer);

        return command.toArray(new String[0]);
    }

    public static Path findMcDir() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        Path dir;

        if (os.contains("win") && System.getenv("APPDATA") != null) {
            dir = Paths.get(System.getenv("APPDATA")).resolve(".minecraft");
        } else {
            String home = System.getProperty("user.home", ".");
            Path homeDir = Paths.get(home);

            if (os.contains("mac")) {
                dir = homeDir.resolve("Library").resolve("Application Support").resolve("minecraft");
            } else {
                dir = homeDir.resolve(".minecraft"); // linux B)
            }
        }

        return dir.toAbsolutePath().normalize();
    }

    private interface FabricRequester {
        @GET("maven-metadata.xml")
        CompletableFuture<FabricInstallerMetadata> getMetadata();
    }
}
