package org.rkzyomc.mcserverpackerpro.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

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

}
