package net.smileycorp.mounts.config.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.smileycorp.mounts.common.MountsLogger;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.Collections;

public class ConfigDataManager {

    public static Path CONFIG_FOLDER;

    public static void init() {
        MountsLogger.clearLog(true);
        MountsLogger.blankLine();
        MountsLogger.heading("CHECKING CONFIG DATA");
        MountsLogger.blankLine();
        CONFIG_FOLDER = Paths.get(new File("config/mounts").getAbsolutePath());
        if (shouldGenerateFiles()) generateData();
        else MountsLogger.logInfo("Config data files are up to date, skipping data/asset generation");
    }

    public static boolean shouldGenerateFiles() {
        if (isUpToDate()) return false;
        try {
            if (CONFIG_FOLDER.toFile().exists()) {
                File backup = CONFIG_FOLDER.getParent().resolve("mounts-backup").toFile();
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
        File config_file = CONFIG_FOLDER.resolve("mounts-info.json").toFile();
        if (!config_file.isFile()) {
            MountsLogger.logInfo("Hordes data does not exist, generating new files.");
            return false;
        }
        try (FileSystem mod = FileSystems.newFileSystem(ConfigDataManager.class.getProtectionDomain().getCodeSource().getLocation().toURI(),
                Collections.emptyMap())) {
            JsonParser parser = new JsonParser();
            JsonObject configJson = parser.parse(new FileReader(config_file)).getAsJsonObject();
            if (configJson.get("data_version").getAsInt() < 0) return true;
            JsonObject modJson = parser.parse(new BufferedReader(
                    new InputStreamReader(Files.newInputStream(mod.getPath("config_defaults/mounts-info.json"))))).getAsJsonObject();
            if (configJson.get("data_version").getAsInt() >= modJson.get("data_version").getAsInt() && (!modJson.has("dev_version")
                    |! configJson.has("dev_version") || configJson.get("dev_version").getAsInt() >= modJson.get("dev_version").getAsInt()))
                return true;
            MountsLogger.logInfo("Mounts data is not up to date or set to pack author mode, generating new files.");;
            return false;
        } catch (Exception e) {
            MountsLogger.logError("Failed data version check", e);
            return true;
        }
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
