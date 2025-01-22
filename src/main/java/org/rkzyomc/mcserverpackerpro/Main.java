package org.rkzyomc.mcserverpackerpro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rkzyomc.mcserverpackerpro.configs.Setting;
import org.rkzyomc.mcserverpackerpro.interfaces.Placeholder;
import org.rkzyomc.mcserverpackerpro.utils.FilesManager;
import org.rkzyomc.mcserverpackerpro.utils.PlaceholderManager;

public class Main {
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

         */
        Placeholder ph = PlaceholderManager.getInstance(
                logger,
                filesManager.getPlaceholderJson()
        );

        logger.info("info {}", ph.parse("$(mcp.test3)"));

        logger.info("程序退出");
    }

    public static Logger getLogger() {
        return logger;
    }
}
