package net.smileycorp.mounts.config.data;

import net.smileycorp.mounts.common.MountsLogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.*;
import java.util.Collections;

public class ConfigDataManager {

    public static Path CONFIG_FOLDER;

    public static void init() {
        MountsLogger.blankLine();
        MountsLogger.heading("CHECKING CONFIG DATA");
        MountsLogger.blankLine();
        CONFIG_FOLDER = Paths.get(new File("config/mounts").getAbsolutePath());
        if (shouldGenerateFiles(CONFIG_FOLDER.getParent())) generateData();
        else MountsLogger.logInfo("Config data files are up to date, skipping data/asset generation");
    }

    public static boolean shouldGenerateFiles(Path folder) {
        if (isUpToDate()) return false;
        try {
            if (CONFIG_FOLDER.toFile().exists()) {
                File backup = folder.resolve("mounts-backup").toFile();
                backup.mkdir();
                FileUtils.deleteDirectory(backup);
                FileUtils.copyDirectory(CONFIG_FOLDER.toFile(), backup);
                MountsLogger.logInfo("Backed up old data to " + backup);
            }
            FileUtils.deleteDirectory(CONFIG_FOLDER.toFile());
        } catch (Exception e) {
        }
        return true;
    }

    public static void generateData() {
        try (FileSystem mod = FileSystems.newFileSystem(ConfigDataManager.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                Collections.emptyMap())) {
            Files.find(mod.getPath("config_defaults"), Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile())
                    .forEach(ConfigDataManager::copyFileFromMod);
            MountsLogger.logInfo("Generated config files");
        } catch (Exception e) {
            MountsLogger.logInfo("Failed to generate config files");
        }
    }

    private static boolean isUpToDate() {
        return CONFIG_FOLDER.toFile().exists();
    }

    private static void copyFileFromMod(Path path) {
        try {
            FileUtils.copyInputStreamToFile(Files.newInputStream(path),
                    new File(CONFIG_FOLDER.toFile(), path.toString().replace( "config_defaults/", "")));
            MountsLogger.logInfo("Copied file " + path);
        } catch (Exception e) {
            MountsLogger.logError("Failed to copy file " + path, e);
        }
    }

}
