package net.uku3lig.uklient.download;

import net.uku3lig.uklient.model.FabricMetadata;
import net.uku3lig.uklient.util.Util;
import retrofit2.http.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FabricInstaller {
    private static final String BASE_URL = "https://maven.fabricmc.net/net/fabricmc/";
    private static final FabricRequester requester = RequestManager.supplyRetrofit(BASE_URL).create(FabricRequester.class);

    private static final String URL_FORMAT = BASE_URL + "fabric-installer/%s/fabric-installer-%s.jar";

    public static CompletableFuture<URL> getLatestFabricInstaller() {
        return requester.getInstallerMetadata().thenApply(m -> {
            String ver = m.getVersioning().getRelease();
            String url = String.format(URL_FORMAT, ver, ver);
            return Util.url(url);
        });
    }

    public static CompletableFuture<Void> installFabric(String mcVer, Path installDir, Executor exec) {
        Path fabric = Util.getTmpDir().resolve("fabric.jar");
        return getLatestFabricInstaller().thenCompose(u -> Downloader.download(u, fabric, exec))
                .thenAccept(v -> {
                    String[] command = getInstallCommand(fabric, mcVer, installDir);
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        Process proc = builder.inheritIO().start();
                        proc.waitFor();
                        if (proc.exitValue() != 0) throw new IOException("fabric installation went wrong");
                    } catch (Exception e) {
                        System.err.println("Could not install fabric");
                        Thread.currentThread().interrupt();
                    }
                });
    }

    public static CompletableFuture<String> getLatestFabricLoader() {
        return requester.getLoaderMetadata()
                .thenApply(FabricMetadata::getVersioning)
                .thenApply(FabricMetadata.Versioning::getRelease);
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

    private interface FabricRequester {
        @GET("fabric-installer/maven-metadata.xml")
        CompletableFuture<FabricMetadata> getInstallerMetadata();

        @GET("fabric-loader/maven-metadata.xml")
        CompletableFuture<FabricMetadata> getLoaderMetadata();
    }
}
