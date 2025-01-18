package org.rkzyomc.mcserverpackerpro.utils;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilesManager {
    private final @NotNull Set<Path> paths = new HashSet<>();
    private final @NotNull Logger logger;
    private final @NotNull Class<?> clazz;
    private final @NotNull Path dataFolder;

    public FilesManager(@NotNull Logger logger, @NotNull Class<?> clazz) {
        this.logger = logger;
        this.clazz = clazz;
        try {
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            Path jarPath = Paths.get(url.toURI()).getParent();
            this.dataFolder = jarPath != null ? jarPath : Paths.get("");
        } catch (Exception e) {
            logger.error("Error retrieving data folder.", e);
            throw new RuntimeException(e);
        }
        initPathSet();
    }

    private void initPathSet() {
        List.of(
                "./default",
                "./default/files",
                "./default/servers",
                "./built",
                "./built/files",
                "./built/servers",
                "./backup",
                "./backup/default",
                "./backup/built"
        ).forEach(s -> paths.add(toPath(s)));
    }

    public void initFiles() {
        paths.forEach(this::createDirectory);
    }

    private void createDirectory(Path path) {
        if (path.toFile().exists()) return;
        try {
            Files.createDirectories(path);
            logger.info("Directory created: [{}]", path);
        } catch (IOException e) {
            logger.error("Failed to create directory: [{}]", path, e);
            throw new RuntimeException(e);
        }
    }

    private Path toPath(String s) {
        if (s.startsWith("./")) {

            String[] split = s.substring(2).split("/");
            StringBuilder builder = new StringBuilder();
            for (String string : split) {
                builder.append(string).append("/");
            }

            return dataFolder.resolve(builder.toString());
        } else {
            logger.error("not support the string [{}]", s);
            throw new RuntimeException();
        }
    }

    private @NotNull Path getDataPath() {
        return dataFolder;
    }

    public @NotNull Set<Path> getPaths() {
        return paths;
    }
}
