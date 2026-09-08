package net.smileycorp.mounts.config.data.mobs;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.ParsingException;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

public abstract class JsonLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final File directory;

    public JsonLoader(File directory) {
        this.directory = directory;
    }
    
    public final void loadData() {
        dataInit();
        Map<String, JsonElement> data = Maps.newHashMap();
        File[] files = directory.isFile() ? new File[]{directory} : directory.listFiles((f, s) -> s != null && s.endsWith(".json"));
        if (files == null) {
            MountsLogger.logError("Failed reading files in " + directory, new NullPointerException());
            return;
        }
        for (File file : files) {
            String id = file.getName().replace(".json", "");
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                JsonElement json = GSON.getAdapter(JsonElement.class).read(reader);
                if (data.put(id, json) != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                }
            } catch (Exception e) {
                MountsLogger.blankLine();
                MountsLogger.logError("Couldn't parse data " + file, error(e, id));
            }
        }
        readData(data);
    }

    protected abstract void dataInit();

    protected abstract void readData(Map<String, JsonElement> data);

    protected Exception error(Exception e, String file) {
        if (e instanceof JsonParseException) {
            e = new ParsingException(e.getMessage());
            ((ParsingException) e).setFile(file);
        }
        return e;
    }

}
