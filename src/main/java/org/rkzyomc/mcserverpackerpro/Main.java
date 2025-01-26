package org.rkzyomc.mcserverpackerpro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.rkzyomc.mcserverpackerpro.configs.Setting;
import org.rkzyomc.mcserverpackerpro.interfaces.Placeholder;
import org.rkzyomc.mcserverpackerpro.utils.FilesManager;
import org.rkzyomc.mcserverpackerpro.utils.PlaceholderManager;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.rkzyomc.mcserverpackerpro.utils.Tool.matchesSuffix;

public class Main {
    public static final @NotNull Class<Main> clazz = Main.class;
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final FilesManager filesManager = new FilesManager(logger, Main.class);

    public static void main(String[] args) {
        logger.info("开始运行");
        filesManager.initAll(); // 初始化文件

        Setting setting = filesManager.getSettingManager().getConfigData(); // 获取配置文件

        /*
        backup
         */
        if (setting.backup().compressDefault()) {
            filesManager.backupFile("./default");
        }
        if (setting.backup().compressBuilt()) {
            filesManager.backupFile("./built");
        }

        /*
        placeholder
         */
        Placeholder ph = PlaceholderManager.getInstance(
                logger,
                filesManager.getPlaceholderJson()
        );

        /*
        移动文件
         */
        filesManager.getFileX("./built/files").deleteAllListFiles(false);
        filesManager.getFileX("./built/servers").deleteAllListFiles(false);
        filesManager.getFileX("./default").copyListFilesTo(
                filesManager.getPath("./built"),
                StandardCopyOption.REPLACE_EXISTING
        );

        /*
        替换
         */
        filesManager.getFileX("./built").allListFiles(false).forEach(fileX -> {
            Path path = fileX.toPath();
            if (!matchesSuffix(path.toString(), setting.work().suffixes())) return;
            logger.info("parsing [{}]", fileX);
            String parse = ph.parse(
                    fileX.read()
            );
            fileX.write(parse);
        });

        logger.info("程序退出");
    }

    public static Logger getLogger() {
        return logger;
    }
}
