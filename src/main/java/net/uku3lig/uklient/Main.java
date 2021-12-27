package net.uku3lig.uklient;

import com.diogonunes.jcolor.Attribute;
import me.tongfei.progressbar.ProgressBar;
import net.uku3lig.uklient.download.CurseforgeDownloader;
import net.uku3lig.uklient.download.FabricInstaller;
import net.uku3lig.uklient.download.ModrinthDownloader;
import net.uku3lig.uklient.util.ResourceManager;
import net.uku3lig.uklient.model.ModInfo;
import net.uku3lig.uklient.model.NamedModList;
import net.uku3lig.uklient.util.Color;
import net.uku3lig.uklient.util.MinecraftHelper;
import net.uku3lig.uklient.util.UIManager;
import net.uku3lig.uklient.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
    public static final String COW =
            "&f ___________________________ \n" +
            "< uklient installer poggers >\n" +
            " --------------------------- \n" +
            "        \\   ^__^\n" +
            "         \\  (oo)\\_______\n" +
            "            (__)\\       )\\/\\\n" +
            "                ||----w |\n" +
            "                ||     ||";

    static final List<String> AVAILABLE_MC_VERSIONS = Arrays.asList("1.18.1", "1.17.1", "1.16.5");
    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        System.out.println(Color.parse(COW, Attribute.BOLD()));

        Path mcPath = MinecraftHelper.findMcDir();
        while (!Files.isDirectory(mcPath) || !Files.exists(mcPath.resolve("launcher_profiles.json"))) {
            System.out.println("Cannot find your .minecraft directory");
            mcPath = Paths.get(UIManager.input("Please input your .minecraft directory path")).normalize().toAbsolutePath();
        }

        System.out.println("\n\n");
        System.out.println(Color.parse("&3Please choose a Minecraft version:", Attribute.BOLD()));
        final String mcVer = AVAILABLE_MC_VERSIONS.get(UIManager.choice(AVAILABLE_MC_VERSIONS));

        System.out.println(Color.parse("\n\n&3Please choose an installation directory:", Attribute.BOLD()));
        List<String> choices = Arrays.asList("Install in .uklient (recommended)",
                "Install in .minecraft (will delete all your current mods)", "Install in another directory");
        Path installDir;
        switch (UIManager.choice(choices, 0)) {
            case 0:
                installDir = mcPath.getParent().resolve(".uklient");
                break;
            case 1:
                installDir = mcPath;
                break;
            default:
                do {
                    installDir = Paths.get(UIManager.input("Please input your installation directory")).normalize().toAbsolutePath();
                } while (Files.exists(installDir) && !Files.isDirectory(installDir));
        }

        System.out.println(Color.parse("\n\n&3Please choose a preset:", Attribute.BOLD()));
        LinkedHashMap<String, String> presetNames = new LinkedHashMap<>(ResourceManager.getPresets().stream()
                .collect(Collectors.toMap(NamedModList::getDisplayName, NamedModList::getName)));
        List<String> displayNames = new LinkedList<>(presetNames.keySet());

        NamedModList preset = ResourceManager.getPresetByName(presetNames.get(displayNames.get(UIManager.choice(displayNames, 0))));

        Collection<ModInfo> mods = ResourceManager.addDependencies(preset.getModInfos());

        if (!Files.isDirectory(installDir)) {
            try {
                Files.deleteIfExists(installDir);
                Files.createDirectories(installDir);
            } catch (IOException e) {
                System.err.println("Could not create installation directory");
                System.err.println("Please check if " + installDir + " is a directory");
                System.exit(1);
            }
        }

        final Path modPath = installDir.resolve("mods");
        final Path configPath = installDir.resolve("config");
        final Path texturePath = installDir.resolve("resourcepacks");

        try {
            Files.createDirectories(modPath);
            Files.createDirectories(configPath);
            Files.createDirectories(texturePath);
        } catch (IOException e) {
            System.err.println("Could not create installation directories");
            System.err.println("Please check if mods, config or resourcepacks are valid directories in " + installDir);
            System.exit(1);
        }

        final Path finalMcPath = mcPath;
        final Path finalInstallDir = installDir;


        FabricInstaller.installFabric(mcVer, mcPath, executor)
                .thenCompose(v -> {
                    final ProgressBar pb = Util.getProgressBar("Downloading Mods", mods.size());
                    return CompletableFuture.allOf(mods.stream().distinct()
                                    .map(m -> {
                                        if (m.getProvider().equals(ModInfo.Provider.MODRINTH))
                                            return ModrinthDownloader.download(m, mcVer, modPath, executor);
                                        else return CurseforgeDownloader.download(m, mcVer, modPath, executor);
                                    }).map(c -> c.thenRun(pb::step)).toArray(CompletableFuture[]::new))
                            .thenRun(pb::close).thenRun(System.out::println);
                })
                .thenRun(() -> mods.forEach(m -> m.copyConfig(preset.getName(), configPath)))
                .thenRun(() -> System.out.println("Copied all config files"))
                .thenCompose(v -> FabricInstaller.getLatestFabricLoader())
                .thenAccept(f -> MinecraftHelper.createLauncherProfile(finalMcPath, finalInstallDir, f, mcVer))
                .thenRun(() -> System.out.println("Created launcher profile"))
                .thenRun(() -> MinecraftHelper.copyUserFiles(finalMcPath, finalInstallDir))
                .thenRun(() -> System.out.println("Copied resource packs and options"))
                .thenRun(() -> System.out.println(Color.parse("\n&3uklient installed! you can now close this window", Attribute.BOLD())));
    }
}
