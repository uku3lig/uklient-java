package net.uku3lig.uklient;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.nio.file.StandardOpenOption.*;

public class Test {
    public static void main(String[] args) throws Exception {
        ResourceManager.getModFromName("sodium").copyConfig("", Paths.get("/home/leo/.uklient/config/mods"));
    }

    private static void map() {
        Map<String, Boolean> map = new ImmutableMap.Builder<String, Boolean>()
                .put("one", true)
                .put("two", false)
                .put("three", true)
                .build();

        Map<String, Boolean> result = UIManager.updateSelection(map);

        UIManager.printMap(result);
    }

    private static void modrinth2() {
        FabricInstaller.getLatestFabricInstaller().thenAccept(System.out::println);
        ModrinthDownloader.getMostRecentFile("AANobbMI", "1.17.1").thenAccept(System.out::println);
    }

    private static void curse() {
        CurseforgeDownloader.getMostRecentFile("297038", "1.17.1").thenAccept(System.out::println);
    }

    private static void jcolor() {
        AnsiFormat txt = new AnsiFormat(Attribute.BLUE_TEXT(), Attribute.BOLD());
        AnsiFormat main = new AnsiFormat(Attribute.YELLOW_TEXT(), Attribute.BOLD());

        String bracket1 = txt.format("[");
        String number = main.format("1");
        String bracket2 = txt.format("]");
        String modname = main.format("litematica");

        System.out.printf("%s%s%s %s%n", bracket1, number, bracket2, modname);
    }

    static void dlTest() throws Exception {
        URL url = new URL("https://gist.githubusercontent.com/uku3lig/fb1000f1f7fdfb069cebdb9c98088976/raw/e5472119dd69cc9bb9f1814992460af37277595e/CommandConverter.java");
        Path path = Paths.get("E:\\CommandConverter.java");
        try (ReadableByteChannel in = Channels.newChannel(url.openStream());
             FileChannel out = FileChannel.open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
            URLConnection connection = url.openConnection();

            long fileSize = connection.getContentLengthLong();
            System.out.println(fileSize);
            long downloaded = 0;
            while (downloaded < fileSize) {
                downloaded += out.transferFrom(in, downloaded, downloaded == 0 ? 1024 : fileSize);
                System.out.println(downloaded);
            }
        }
    }

    static void modrinth() {
        String id = ModrinthDownloader.getModID("sodium");
        ModrinthDownloader.download(id, "1.17.1", new File("/home/leo/"));
    }
}
