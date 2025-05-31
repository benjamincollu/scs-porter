import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // argument check
        if (args.length != 2) {
            System.err.println("Wrong amount of arguments, displaying help dialog...");
            helpDialog();
            System.exit(1);
        }

        String fileName = args[0];
        String to = args[1];

        // file operations
        try {
            System.out.println("Reading manifest.sii and setting compatible_versions to " + to + ".*");
            changeVersion(Path.of(fileName), to);
        } catch (IOException e) {
            System.err.println("Error performing operations on file manifest.sii");
            e.printStackTrace();
            System.exit(2);
        }

        System.out.println("Sucessfully ported " + fileName + " to version " + to + ".*");
    }
    public static void helpDialog() {
        System.out.println("scs-porter - A command-line tool to port .scs mod files to another version of Euro Truck Simulator 2");
        System.out.println("valid syntax:");
        System.out.println("    scs-porter.jar <file> <version to port to>");
        System.out.println("example:");
        System.out.println("    scs-porter.jar mod.scs 1.54");
    }

    private static void changeVersion(Path source, String to) throws IOException {
        URI uri = URI.create("jar:" + source.toUri());
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of("create", "false"))) {
            Path manifest = fileSystem.getPath("/manifest.sii");

            boolean found = false;

            List<String> oldFile = Files.readAllLines(manifest);
            ArrayList<String> newFile = new ArrayList<>();

            int lineNumber = 0;

            for (String line : oldFile) {
                newFile.add(line);
                if (!found && line.trim().startsWith("compatible_versions")) {
                    System.out.println("Found compatible_versions line: " + lineNumber);
                    found = true;
                    newFile.add("compatible_versions[]: \"" + to + ".*\" # ported by scs-porter");
                }
                if (!found) {
                    lineNumber++;
                }
            }

            if (!found) {
                System.err.println("No compatible_versions line found in manifest.sii");
                System.exit(2);
            }

            Files.write(manifest, newFile);
            System.out.println("Successfully set compatible_versions to " + to + ".*");
        }
    }
}
