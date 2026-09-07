package net.smileycorp.mounts.common;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.smileycorp.mounts.config.data.ParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class MountsLogger {

    private static final Logger logger = LogManager.getLogger(Constants.MODID);
    private static final Path log_file = Paths.get("logs/mounts.log");

    private static final List<String> persistent_data = Lists.newArrayList();
    private static final List<String> errors = Lists.newArrayList();

    private static boolean is_volatile = false;
    private static boolean has_errors = false;

    public static void clearLog(boolean clear_persistent) {
        try {
            Files.write(log_file, clear_persistent ? Lists.newArrayList() : persistent_data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to write to log file", e);
            e.printStackTrace();
        }
    }

    public static void logSilently(Object message) {
        writeToFile(message);
    }

    public static void logInfo(Object message) {
        writeToFile(message);
        logger.info(message);
    }

    public static void logError(Object message, Exception e) {
        boolean stackTrace = true;
        if (e instanceof ParsingException) {
            String file = ((ParsingException) e).getFile();
            stackTrace = false;
            if (file != null) {
                errors.add(file);
                writeToFile("Errors in file " + file);
            }
        }
        logger.error(message, e);
        writeToFile(message +  " " + (stackTrace ? e : e.getMessage()));
        if (stackTrace) {
            for (StackTraceElement traceElement : e.getStackTrace()) writeToFile(traceElement);
            e.printStackTrace();
        }
        has_errors = true;
    }

    private static boolean writeToFile(Object message) {
        return writeToFile(Lists.newArrayList(String.valueOf(message)));
    }

    public static boolean writeToFile(List<String> out) {
        try {
            if (!is_volatile) persistent_data.addAll(out);
            Files.write(log_file, out, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (Exception e) {
            logger.error("Failed to write to log file", e);
            e.printStackTrace();
            return false;
        }
    }

    public static void markVolatile() {
        is_volatile = true;
    }

    public static boolean hasErrors() {
        return has_errors;
    }

    public static List<String> getErrors() {
        return errors;
    }

    public static ITextComponent getFiletext() {
        String file = log_file.toAbsolutePath().toString();
        TextComponentString text = new TextComponentString(file);
        text.setStyle(new Style().setUnderlined(true).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(file))));
        return text;
    }

    public static void blankLine() {
        writeToFile("");
    }

    public static void heading(String message) {
        writeToFile("############################## " + message + " ##############################");
    }

    public static void clearErrors() {
        has_errors = false;
        errors.clear();
    }

}
