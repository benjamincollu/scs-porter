import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong amount of arguments, displaying help dialog...");
            helpDialog();
            System.exit(1);
        }
        String fileName = args[0];
        String to = args[1];

        try {
            System.out.println("Extracting manifest.sii from " + fileName + " to " + to);
            extractFile(Path.of(fileName), Path.of("manifest.sii"));
        } catch (IOException e) {
            System.err.println("Error extracting manifest.sii from " + fileName + ": ");
            e.printStackTrace();
            System.exit(2);
        }


        try {
            System.out.println("Reading manifest.sii and setting compatible_versions to " + to + ".*");
            changeVersion(to);
        } catch (IOException e) {
            System.err.println("Error writing to file manifest.sii");
            e.printStackTrace();
            System.exit(2);
        }

        System.out.println("Sucessfully extracted manifest.sii");
    }

    private static void changeVersion(String to) throws IOException {
        Scanner scanner = new Scanner(new File("manifest.sii"));
        boolean found = false;
        ArrayList<String> lines = new ArrayList<>();
        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            lines.add(line);
            if (line.startsWith("compatible_versions")) {
                System.out.println("Found compatible_versions line: " + lineNumber);
                found = true;
                // duplicate that line and set the version to port to
                // it looks like this compatible_versions[]: "1.53.*"
                String newLine = "compatible_versions[]: \"" + to + ".*\" # ported by scs-porter";
                lines.add(newLine);
                break;
            }
            lineNumber++;
        }
        if (found) {
            // write the lines back to manifest.sii
            Files.write(Path.of("manifest.sii"), lines);
            System.out.println("Successfully set compatible_versions to " + to + ".*");
        } else {
            System.err.println("No compatible_versions line found in manifest.sii");
            System.exit(2);
        }
    }

    public static void helpDialog() {
        System.out.println("scs-porter - A command-line tool to port .scs mod files to another version of Euro Truck Simulator 2");
        System.out.println("valid syntax:");
        System.out.println("    scs-porter.jar <file> <version to port to>");
        System.out.println("example:");
        System.out.println("    scs-porter.jar mod.scs 1.54");
    }

    private static void extractFile(Path source, Path output) throws IOException {
        URI uri = URI.create("jar:" + source.toUri());
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of("create", "false"))) {
            Path fileToExtract = fileSystem.getPath("/" + "manifest.sii");
            Files.copy(fileToExtract, output);
        }
    }
}
