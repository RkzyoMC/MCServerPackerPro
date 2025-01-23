package org.rkzyomc.mcserverpackerpro.utils;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rkzyomc.mcserverpackerpro.Main;
import org.rkzyomc.mcserverpackerpro.interfaces.FileX;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.rkzyomc.mcserverpackerpro.utils.Tool.readFileToString;

public class FileManager extends File implements FileX {

    private FileManager(@NotNull File file) {
        super(file.getPath());
    }

    public static FileX getInstance(File file) {
        return new FileManager(file);
    }

    /**
     * 获取文件夹下所有文件
     */
    @Override
    public @NotNull List<FileX> allListFiles(boolean directory) {
        List<FileX> files = new ArrayList<>();
        File[] listFiles = listFiles();
        if (listFiles == null) {
            if (directory) {
                files.add(this);
            }
            return files;
        }
        for (File file : listFiles) {
            if (file.isDirectory()) {
                files.addAll(
                        new FileManager(file).allListFiles(directory)
                );
            } else {
                files.add(
                        new FileManager(file)
                );
            }
        }
        return files;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteAllListFiles(boolean the) {
        allListFiles(true).forEach(fileX -> ((File) fileX).delete());
        if (the) delete();
    }

    @Override
    public void copyListFilesTo(@NotNull Path path, StandardCopyOption option) {
        if (isFile()) return;
        Tool.copyFile(this.toPath(), path, option);
    }

    @Override
    public @Nullable String read() {
        if (isFile()) {
            return readFileToString(toPath());
        }
        return null;
    }

    @Override
    public void write(@NotNull String body) {
        Tool.writeFileOverwrite(this.toPath(), body);
    }
}
