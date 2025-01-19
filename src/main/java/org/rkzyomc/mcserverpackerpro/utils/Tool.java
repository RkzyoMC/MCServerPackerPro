package org.rkzyomc.mcserverpackerpro.utils;

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
}
