package org.rkzyomc.mcserverpackerpro.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rkzyomc.mcserverpackerpro.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Tool {

    private static final @NotNull Logger logger = Main.getLogger();
    public static final @NotNull Class<Main> clazz = Main.class;

    /**
     * 释放资源文件到指定路径。
     *
     * @param resourcePath 资源路径
     * @param extractPath  输出路径
     * @param createEmpty  如果资源不存在是否创建空文件
     */
    public static void extractResourceFile(@NotNull String resourcePath, @NotNull Path extractPath, boolean createEmpty) {
        if (Files.exists(extractPath)) {
            logger.info("File already exists: {}", resourcePath);
            return;
        }

        try (InputStream inputStream = clazz.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                if (createEmpty) {
                    Files.createFile(extractPath);
                    logger.info("Created empty file at: {}", extractPath);
                } else {
                    logger.error("Resource file not found: {}", resourcePath);
                    throw new FileNotFoundException("Resource file not found: " + resourcePath);
                }
            } else {
                Files.copy(inputStream, extractPath);
                logger.info("Extracted resource file: {}", resourcePath);
            }
        } catch (IOException e) {
            logger.error("Error extracting resource file: {}", resourcePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 压缩整个文件夹
     * @param sourceFolder 文件夹
     * @param zipFileName 输出位置
     * @throws RuntimeException 失败
     */
    public static void compressFolder(File sourceFolder, File zipFileName){
        logger.info("compressing {}", sourceFolder);
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            addFolderToZip(sourceFolder, sourceFolder.getName(), zos);
        } catch (IOException e) {
            logger.error("压缩文件夹时出现错误", e);
            throw new RuntimeException();
        }
    }
    // 递归添加文件夹内容到Zip
    private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
            } else {
                addFileToZip(file, parentFolder + "/" + file.getName(), zos);
            }
        }
    }
    // 添加单个文件到Zip
    private static void addFileToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    public static @NotNull String getTime() {
        // 获取当前时间
        Date now = new Date();
        // 格式化时间：yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(now);
    }

    /**
     * Retrieves a value from the JSON object using a dot-separated path.
     *
     * @param path Dot-separated path (e.g., "key.subkey")
     * @return The value corresponding to the path
     */
    public static @NotNull JsonElement getValueByPath(@NotNull JsonObject current, @NotNull String path) {
        String[] keys = (path).split("\\.");

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            JsonElement element = current.get(key);

            if (element == null) {
                logger.error("Failed to retrieve path [{}]: not found in current.", path);
                throw new IllegalArgumentException("Invalid path: " + path);
            }

            if (i == keys.length - 1) { // Last key in path
                return element;
            }

            if (!element.isJsonObject()) {
                logger.error("Invalid structure for path [{}]: expected an object at key [{}]", path, key);
                throw new IllegalArgumentException("Invalid structure: " + path);
            }

            current = element.getAsJsonObject();
        }
        logger.error("getValueByPath() error [{}, {}]", current, path);
        throw new RuntimeException();
    }

    /**
     * 读取文件内容为 String
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static @Nullable String readFileToString(@NotNull Path filePath) {
        if (!Files.exists(filePath)) {
            logger.error("Failed to get file {}", filePath);
            return null;
        }
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Failed to read file {}", filePath);
            throw new RuntimeException(e);
        }
    }
}
