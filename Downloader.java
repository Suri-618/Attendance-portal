import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Downloader {
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Paths.get("d:/JAVA/JUNO/lib"));
        String[] urls = {
                "https://repo1.maven.org/maven2/com/google/zxing/core/3.5.1/core-3.5.1.jar",
                "https://repo1.maven.org/maven2/com/google/zxing/javase/3.5.1/javase-3.5.1.jar",
                "https://repo1.maven.org/maven2/com/beust/jcommander/1.82/jcommander-1.82.jar"
        };
        for (String url : urls) {
            String name = url.substring(url.lastIndexOf('/') + 1);
            System.out.println("Downloading " + name);
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get("d:/JAVA/JUNO/lib/" + name), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        System.out.println("Done downloading.");
    }
}
