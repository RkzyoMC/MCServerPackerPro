package org.rkzyomc.mcserverpackerpro.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.rkzyomc.mcserverpackerpro.interfaces.Placeholder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager implements Placeholder {
    private final @NotNull JsonObject json;
    private final @NotNull Map<String, Supplier<String>> placeholderMap = new HashMap<>(); // 特殊变量

    private PlaceholderManager(@NotNull Logger logger, @NotNull JsonObject json) {
        this.json = updateConfig(json);
        placeholderMap.put("\\$\\(random\\.uuid\\)", () -> UUID.randomUUID().toString());
        placeholderMap.forEach((s, stringSupplier) -> logger.info("placeholderMap [{}]", s));
    }

    public static Placeholder getInstance(@NotNull Logger logger, @NotNull JsonObject json) {
        return new PlaceholderManager(logger, json);
    }

    @Override
    public @NotNull String parse(@NotNull String text) {
        // 特殊变量替换
        for (String string : placeholderMap.keySet()) {
            Pattern pattern = Pattern.compile(string);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String get = placeholderMap.get(string).get();
                text = text.replaceFirst(
                        string,
                        get
                );
            }
        }

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
