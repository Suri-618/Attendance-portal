import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class Refactor {
    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("d:/JAVA/JUNO");
        String replacement = "        mainPanel.setPreferredSize(new Dimension(f.getWidth(), f.getHeight()));\n" +
                             "        mainPanel.setMinimumSize(new Dimension(f.getWidth(), f.getHeight()));\n" +
                             "        mainPanel.setMaximumSize(new Dimension(f.getWidth(), f.getHeight()));\n" +
                             "        \n" +
                             "        JPanel wrapperPanel = new JPanel(new java.awt.GridBagLayout()) {\n" +
                             "            @Override\n" +
                             "            protected void paintComponent(Graphics g) {\n" +
                             "                super.paintComponent(g);\n" +
                             "                Graphics2D g2d = (Graphics2D) g;\n" +
                             "                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);\n" +
                             "                int tw = getWidth(), th = getHeight();\n" +
                             "                Color color1 = new Color(15, 20, 25);\n" +
                             "                Color color2 = new Color(30, 40, 50);\n" +
                             "                GradientPaint gp = new GradientPaint(0, 0, color1, tw, th, color2);\n" +
                             "                g2d.setPaint(gp);\n" +
                             "                g2d.fillRect(0, 0, tw, th);\n" +
                             "            }\n" +
                             "        };\n" +
                             "        wrapperPanel.add(mainPanel, new java.awt.GridBagConstraints());\n" +
                             "        f.setExtendedState(JFrame.MAXIMIZED_BOTH);\n" +
                             "        f.add(wrapperPanel);\n" +
                             "        f.setVisible(true);";

        Pattern pattern = Pattern.compile("f\\.add\\(mainPanel\\);\\s*f\\.setVisible\\(true\\);");
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.java")) {
            for (Path file : stream) {
                if (file.getFileName().toString().equals("Refactor.java")) continue;
                String content = new String(Files.readAllBytes(file), "UTF-8");
                if (content.contains("f.add(mainPanel);") && content.contains("f.setVisible(true);")) {
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        String newContent = matcher.replaceAll(Matcher.quoteReplacement(replacement));
                        Files.write(file, newContent.getBytes("UTF-8"));
                        System.out.println("Updated " + file.getFileName());
                    }
                }
            }
        }
    }
}
