package org.rkzyomc.mcserverpackerpro.interfaces;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public interface FileX {
    @NotNull List<FileX> allListFiles(boolean directory);

    void deleteAllListFiles(boolean the);

    void copyListFilesTo(@NotNull Path path, StandardCopyOption option);

    @Nullable String read();

     void write(@NotNull String body);

    @NotNull Path toPath();
}
