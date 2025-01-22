package org.rkzyomc.mcserverpackerpro.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.rkzyomc.mcserverpackerpro.interfaces.Placeholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager implements Placeholder {
    private final @NotNull Logger logger;
    private final @NotNull JsonObject json;

    private PlaceholderManager(@NotNull Logger logger, @NotNull JsonObject json) {
        this.logger = logger;
        this.json = updateConfig(json);
    }

    public static Placeholder getInstance(@NotNull Logger logger, @NotNull JsonObject json) {
        return new PlaceholderManager(logger, json);
    }

    @Override
    public @NotNull String parse(@NotNull String text) {
        Pattern pattern = Pattern.compile("\\$\\(mcp\\.([^)]+)\\)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(1);
            text = text.replaceAll(
                    "\\$\\(mcp\\."+group+"\\)",
                    Tool.getValueByPath(
                            json,
                            group
                    ).getAsString()
            );
        }
        return text;
    }

    private @NotNull JsonObject updateConfig(@NotNull JsonObject json) {
        Pattern pattern = Pattern.compile("\\$\\(mcp\\.([^)]+)\\)");
        Matcher matcher = pattern.matcher(json.toString());
        if (matcher.find()) {
            String group = matcher.group(1);
            json = JsonParser.parseString(
                    json.toString().replaceAll(
                            "\\$\\(mcp\\."+group+"\\)",
                            Tool.getValueByPath(
                                    json,
                                    group
                            ).getAsString()
                    )
            ).getAsJsonObject();
            return updateConfig(json);
        } else {
            return json;
        }
    }
}
