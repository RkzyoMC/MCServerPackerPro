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
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.rkzyomc.mcserverpackerpro.Main.clazz;

public class Tool {

    private static final @NotNull Logger logger = Main.getLogger();

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

    /**
     * 将文本内容全覆盖写入指定文件
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @throws RuntimeException 如果写入失败
     */
    public static void writeFileOverwrite(Path filePath, String content) {
        // 使用 try-with-resources 自动关闭资源
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(content);
        } catch (IOException e) {
            logger.error("Fail to write file {}", filePath);
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查文件夹名称是否以指定的后缀结尾
     *
     * @param folderName 文件夹名称
     * @param suffixes   后缀列表
     * @return 如果名称以任意一个后缀结尾，则返回 true
     */
    public static boolean matchesSuffix(String folderName, List<String> suffixes) {
        if (suffixes == null || suffixes.isEmpty()) {
            return true; // 如果没有指定后缀，默认匹配所有
        }

        for (String suffix : suffixes) {
            if (folderName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static @NotNull String getRunningPathString() {
        return  clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * 递归复制文件夹和文件
     *
     * @param source      源文件夹或文件路径
     * @param destination 目标文件夹路径
     */
    public static void copyFile(Path source, Path destination, StandardCopyOption option) {
        File sourceFile = source.toFile();
        File destinationFile = destination.toFile();

        // 如果源是文件，直接复制文件
        if (sourceFile.isFile()) {
            try {
                Files.copy(source, destination, option);
            } catch (IOException e) {
                logger.error("Failed to copy: {} -> {}", source, destination);
                logger.trace("Failed to copy: {} -> {}", source, destination);
                return;
            }
            logger.info("copied {} to {}", source, destination);
            return;
        }

        // 如果源是文件夹，创建目标文件夹
        if (!destinationFile.exists() && !destinationFile.mkdirs()) {
            try {
                throw new IOException();
            } catch (IOException e) {
                logger.error("Failed to create directory: {}", destinationFile);
                logger.trace("Failed to create directory: {}", destinationFile);
                return;
            }
        }

        // 遍历源文件夹中的所有文件和子文件夹
        File[] files = sourceFile.listFiles();
        if (files == null) return; // 空文件夹

        for (File file : files) {
            Path subSource = file.toPath();
            Path subDestination = destination.resolve(file.getName());

            // 递归处理子文件或文件夹
            copyFile(subSource, subDestination, option);
        }
    }


    /**
     * 获取文本一段范围内的字符
     * @param text 文本
     * @param i1 起始范围
     * @param i2 终止范围
     * @throws IndexOutOfBoundsException 起始范围 > 终止范围
     */
    public static String charAt(String text, int i1, int i2) {
        if (i1 > i2) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder();
        for (int i = i1; i <= i2; i++) {
            try {
                char c = text.charAt(i);
                builder.append(c);
            } catch (IndexOutOfBoundsException e) {
                return builder.toString();
            }
        }
        return builder.toString();
    }
}
