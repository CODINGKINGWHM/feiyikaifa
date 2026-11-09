package com.codingkingwhm.feiyikaifa.data.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ExhibitCatalog {
    private static final List<ExhibitItem> ITEMS = Collections.unmodifiableList(Arrays.asList(
            new ExhibitItem(1, "花旦·牡丹冠", "旦角", "陕西华县", "清末民初",
                    "花旦影人以灵动身段和精巧冠饰见长。通透皮料在灯下显出温润色泽，衣袖展开时像一朵缓缓盛放的花。",
                    "牡丹象征富贵圆满，卷草纹寓意生生不息。", 0, 1),
            new ExhibitItem(2, "武生·赤焰甲", "生角", "河北唐山", "近现代",
                    "武生造型强调盔甲、靠旗和利落轮廓。关节以线连接，操纵杆带动手臂完成出枪、亮相等舞台动作。",
                    "火焰纹表现英武与守护，几何甲片增强节奏感。", 1, 2),
            new ExhibitItem(3, "青衣·莲云袖", "旦角", "山西孝义", "清代风格",
                    "青衣形象端庄含蓄，长袖和云肩形成柔和曲线。影人刻工讲究阴刻与阳刻交替，让衣纹在幕布上层次分明。",
                    "莲花寓意清雅，云纹寓意吉祥与升腾。", 2, 3),
            new ExhibitItem(4, "灵兽·瑞狮", "神怪", "甘肃环县", "传统样式",
                    "瑞狮把夸张轮廓与镂空纹饰结合，常在热闹场面中登台。灯光穿过密集刻纹，形成闪耀的金红光斑。",
                    "团花和回纹共同表达纳福、守护与绵延。", 3, 2)
    ));

    private ExhibitCatalog() { }

    public static List<ExhibitItem> all() { return ITEMS; }

    public static ExhibitItem find(long id) {
        for (ExhibitItem item : ITEMS) {
            if (item.getId() == id) return item;
        }
        return ITEMS.get(0);
    }
}
