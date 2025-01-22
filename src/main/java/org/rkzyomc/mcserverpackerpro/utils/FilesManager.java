package org.rkzyomc.mcserverpackerpro.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.rkzyomc.mcserverpackerpro.configs.Setting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FilesManager {
    private static final List<String> DEFAULT_PATHS = List.of(
            "./default",
            "./default/files",
            "./default/servers",
            "./built",
            "./built/files",
            "./built/servers",
            "./backup",
            "./backup/default",
            "./backup/built"
    );

    private static final List<String> RESOURCE_FILES = List.of(
            "/placeholder.json", "./placeholder.json"
    );

    private final @NotNull Set<Path> paths = new HashSet<>();
    private final @NotNull Map<String, Path> files = new HashMap<>();
    private final @NotNull Logger logger;
    private final @NotNull Path dataFolder;
    private static ConfigManager<Setting> settingManager;

    public FilesManager(@NotNull Logger logger, @NotNull Class<?> clazz) {
        this.logger = logger;
        try {
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            this.dataFolder = Paths.get(url.toURI()).getParent();
        } catch (Exception e) {
            logger.error("Failed to initialize data folder.", e);
            throw new IllegalStateException("Unable to determine data folder.", e);
        }
        initPaths();
        initFiles();
    }

    public void initAll() {
        paths.forEach(this::createDirectory);
        files.forEach((resourcePath, extractPath) ->
                Tool.extractResourceFile(resourcePath, extractPath, false)
        );
        settingManager = ConfigManager.create(getDataFolder(), "setting.yml", Setting.class);
        settingManager.reloadConfig();
    }

    public @NotNull Path getPath(@NotNull String key) {
        Path path = dataFolder.resolve(key);
        if (Files.exists(path)) {
            return path;
        }
        logger.error("Path [{}] does not exist.", path);
        throw new NoSuchElementException("Path not found: " + key);
    }

    public void backupFile(@NotNull String relativePath) {
        Path targetPath = getPath(relativePath);
        File file = targetPath.toFile();

        if (!isEmptyFolder(file)) {
            String backupName = targetPath.getFileName().toString() + "-" + Tool.getTime() + ".zip";
            Path backupPath = getPath("backup").resolve(backupName);
            Tool.compressFolder(file, backupPath.toFile());
        }
    }

    public JsonObject getPlaceholderJson() {
        return JsonParser.parseString(
                Objects.requireNonNull(Tool.readFileToString(getPath("./placeholder.json")))
        ).getAsJsonObject();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEmptyFolder(@NotNull File file) {
        File[] files = file.listFiles();
        if (files == null) return true;
        for (File f : files) {
            if (f.isDirectory()) {
                if (!isEmptyFolder(f)) return false;
            } else {
                return false;
            }
        }
        return true;
    }

    private void initPaths() {
        DEFAULT_PATHS.forEach(path -> paths.add(toAbsolutePath(path)));
    }

    private void initFiles() {
        for (int i = 0; i < RESOURCE_FILES.size(); i += 2) {
            files.put(RESOURCE_FILES.get(i), toAbsolutePath(RESOURCE_FILES.get(i + 1)));
        }
    }

    private void createDirectory(@NotNull Path path) {
        if (Files.exists(path)) return;
        try {
            Files.createDirectories(path);
            logger.info("Created directory: [{}]", path);
        } catch (IOException e) {
            logger.error("Failed to create directory: [{}]", path, e);
            throw new IllegalStateException("Unable to create directory: " + path, e);
        }
    }

    private @NotNull Path toAbsolutePath(@NotNull String relativePath) {
        if (!relativePath.startsWith("./")) {
            logger.error("Unsupported path format: [{}]", relativePath);
            throw new IllegalArgumentException("Path must start with './': " + relativePath);
        }
        return dataFolder.resolve(relativePath.substring(2).replace("/", File.separator));
    }

    private @NotNull Path getDataFolder() {
        return dataFolder;
    }

    public ConfigManager<Setting> getSettingManager() {
        return settingManager;
    }
}
