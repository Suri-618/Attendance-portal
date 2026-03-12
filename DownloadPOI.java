import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.File;

public class DownloadPOI {
    public static void main(String[] args) {
        String[][] files = {
                { "https://repo1.maven.org/maven2/org/apache/poi/poi/4.1.2/poi-4.1.2.jar", "lib/poi-4.1.2.jar" },
                { "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/4.1.2/poi-ooxml-4.1.2.jar",
                        "lib/poi-ooxml-4.1.2.jar" },
                { "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-schemas/4.1.2/poi-ooxml-schemas-4.1.2.jar",
                        "lib/poi-ooxml-schemas-4.1.2.jar" },
                { "https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/3.1.0/xmlbeans-3.1.0.jar",
                        "lib/xmlbeans-3.1.0.jar" },
                { "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.19/commons-compress-1.19.jar",
                        "lib/commons-compress-1.19.jar" },
                { "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar",
                        "lib/commons-collections4-4.4.jar" }
        };

        File libDir = new File("lib");
        if (!libDir.exists())
            libDir.mkdirs();

        for (String[] fileInfo : files) {
            String url = fileInfo[0];
            String dest = fileInfo[1];
            try {
                System.out.println("Downloading " + dest + "...");
                URL downloadUrl = new URL(url);
                try (InputStream in = downloadUrl.openStream();
                        FileOutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Downloaded.");
            } catch (Exception e) {
                System.out.println("Error downloading " + dest + ": " + e.getMessage());
            }
        }
        System.out.println("All downloads complete.");
    }
}
