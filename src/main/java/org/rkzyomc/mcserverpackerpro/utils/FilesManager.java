package org.rkzyomc.mcserverpackerpro.utils;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.rkzyomc.mcserverpackerpro.configs.Setting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.rkzyomc.mcserverpackerpro.utils.Tool.compressFolder;
import static org.rkzyomc.mcserverpackerpro.utils.Tool.getTime;

public class FilesManager {
    private final @NotNull Set<Path> paths = new HashSet<>();
    private final @NotNull Logger logger;
    private final @NotNull Class<?> clazz;
    private final @NotNull Path dataFolder;
    private static ConfigManager<Setting> settingManager;

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

    /**
     * 初始化 paths 的所有文件夹 <br>
     * 加载setting.yml
     */
    public void initFiles() {
        paths.forEach(this::createDirectory);
        settingManager = ConfigManager.create(getDataPath(), "setting.yml", Setting.class);
        settingManager.reloadConfig();
    }

    /**
     * 获取文件path
     * @param key 相对位置
     * @return 如果不存在抛出异常
     */
    public @NotNull Path getPath(String key) {
        Path path = dataFolder.resolve(key);
        if (path.toFile().exists()) {
            return path;
        }
        logger.error("代码错误 无法获取path[{}]", path);
        throw new RuntimeException();
    }

    /**
     * 压缩 default 和 built
     */
    public void zipFiles() {
        List.of(
                "./default",
                "./built"
        ).forEach(s -> {
            String substring = s.substring(2);
            File file = new File(getPath(s).toUri());
            if (!isEmptyFolder(file)) {
                compressFolder(
                        file,
                        getPath("backup").resolve(
                                substring
                        ).resolve(substring + "-" + getTime() + ".zip").toFile()
                );
            }
        });
    }

    /**
     * 压缩文件夹 存储到 ./backup/{文件夹名字}
     * @param path 相对路径 以./开头
     */
    public void backupFile(String path) {
        String[] split = path.split("/");
        String substring = split[split.length-1];
        File file = new File(getPath(path).toUri());
        if (!isEmptyFolder(file)) {
            compressFolder(
                    file,
                    getPath("backup").resolve(
                            substring
                    ).resolve(substring + "-" + getTime() + ".zip").toFile()
            );
        }
    }

    /**
     * 判断文件夹内是否有文件
     */
    private boolean isEmptyFolder(File file) {
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return true;
        for (File listFile : files) {
            if (listFile.isDirectory()) {
                return isEmptyFolder(listFile);
            } else {
                return false;
            }
        }
        return false;
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

    /**
     * 通过相对路径获取获取绝对路径
     * @param s 相对位置
     */
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

    public ConfigManager<Setting> getSetting() {
        return settingManager;
    }
}
