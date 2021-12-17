package net.uku3lig.uklient.util;

import com.google.common.annotations.Beta;
import net.uku3lig.uklient.model.LauncherProfile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;

public class MinecraftHelper {
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

    @Beta
    public static void createLauncherProfile(String name, String fabricLoaderVersion, String mcVersion) {
        LauncherProfile profile = new LauncherProfile();

        profile.setCreated(Instant.now());
        //profile.setIcon(ICON);
        profile.setLastVersionId(String.format("fabric-loader-%s-%s", fabricLoaderVersion, mcVersion));
        profile.setName(name);
        profile.setType("custom");
    }

    private MinecraftHelper() {}
}
