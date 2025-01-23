package org.rkzyomc.mcserverpackerpro.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@SuppressWarnings("unused")
@ConfHeader("# 配置文件 MCServerPackerPro Config ver 1.0.0 by xiantiao")
public interface Setting {

    @ConfComments("\n# 备份设置")
    @AnnotationBasedSorter.Order(10)
    Backup backup();

    @SubSection
    interface Backup {
        @ConfDefault.DefaultBoolean(true)
        @ConfComments("# 每次启动备份 default 文件夹")
        @AnnotationBasedSorter.Order(10)
        boolean compressDefault();

        @ConfDefault.DefaultBoolean(false)
        @ConfComments("# 每次启动备份 built 文件夹")
        @AnnotationBasedSorter.Order(20)
        boolean compressBuilt();
    }

    @ConfComments("\n# 工作设置")
    @AnnotationBasedSorter.Order(20)
    Work work();

    @SubSection
    interface Work {
        @ConfDefault.DefaultStrings({".yml", ".txt"})
        @ConfComments("# 运行时要处理的文件后缀")
        @AnnotationBasedSorter.Order(10)
        List<String> suffixes();
    }
}
