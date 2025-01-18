package org.rkzyomc.mcserverpackerpro.utils;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rkzyomc.mcserverpackerpro.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Tool {

    private static final @NotNull Logger logger = Main.getLogger();
    public static final @NotNull Class<Main> clazz = Main.class;

    /**
     * 读取resource内文件为String
     *
     * @param path 位置
     * @return 未找到返回null
     */
    public static @Nullable String readResourceFile(@NotNull Path path) {
        try (InputStream stream = clazz.getResourceAsStream(path.toString())) {
            if (stream == null) {
                logger.error("Resource file not found {}", path);
                return null;
            }

            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder out = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                out.append(output);
            }

            return out.toString();
        } catch (IOException e) {
            logger.error("Error extracting resource file: {}", path);
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放resource文件到指定文件夹
     */
    public static void extractResourceFile(@NotNull Path resourcePath, @NotNull Path extractPath) {
        String input = readResourceFile(resourcePath);
        if (input == null) return;
        Path targetPath = extractPath.resolve(resourcePath.toString().substring(1));

        try {
            Files.copy(Path.of(input), targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
