package betterquesting.backport;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static void moveFile(File source, File dest, boolean replace) throws IOException {
        if (replace && dest.exists()) {
            if (!dest.delete()) {
                throw new IOException("Failed to delete existing file: " + dest.getAbsolutePath());
            }
        }
        if (!source.renameTo(dest)) {
            throw new IOException("Failed to move file from " + source.getPath() + " to " + dest.getPath());
        }
    }
}
