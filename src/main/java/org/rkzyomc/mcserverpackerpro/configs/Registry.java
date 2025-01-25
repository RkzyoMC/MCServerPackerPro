package org.rkzyomc.mcserverpackerpro.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@SuppressWarnings("unused")
public interface Registry {
    @ConfDefault.DefaultStrings({"./default"})
    @ConfComments("# 要处理的文件夹")
    @AnnotationBasedSorter.Order(10)
    List<String> paths();

    @ConfDefault.DefaultStrings({
            "\\$\\(random\\.uuid\\)",
            "\\$\\(mcp\\.([^)]+)\\)"
    })
    @ConfComments("# 屏蔽错误的匹配规则 请输入转义之后的值")
    @AnnotationBasedSorter.Order(20)
    List<String> expressions();
}
