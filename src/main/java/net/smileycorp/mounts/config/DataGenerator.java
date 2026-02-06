package net.smileycorp.mounts.config;

import net.smileycorp.mounts.common.MountsLogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.*;
import java.util.Collections;

public class DataGenerator {

    public static File CONFIG_FOLDER;

    public static void generateData() {
        CONFIG_FOLDER = Paths.get(new File("config/mounts").getAbsolutePath()).toFile();
        if (!CONFIG_FOLDER.exists()) {
            CONFIG_FOLDER.mkdirs();
            try (FileSystem mod = FileSystems.newFileSystem(SpearRegistry.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                    Collections.emptyMap())) {
                Files.find(mod.getPath("config_defaults"), Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile())
                        .forEach(DataGenerator::copyFileFromMod);
                MountsLogger.logInfo("Generated config files");
            } catch (Exception e) {
                MountsLogger.logInfo("Failed to generate config files");
            }
        }
    }

    private static void copyFileFromMod(Path path) {
        try {
            FileUtils.copyInputStreamToFile(Files.newInputStream(path),
                    new File(CONFIG_FOLDER, path.toString().replace( "config_defaults/", "")));
            MountsLogger.logInfo("Copied file " + path);
        } catch (Exception e) {
            MountsLogger.logError("Failed to copy file " + path, e);
        }
    }
}
