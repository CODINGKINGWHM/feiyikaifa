package com.codingkingwhm.feiyikaifa.data.model;

import com.codingkingwhm.feiyikaifa.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PatternCatalog {

    public static final long DEFAULT_PATTERN_ID = 1L;

    private static final List<PatternItem> ITEMS = Collections.unmodifiableList(Arrays.asList(
            new PatternItem(1L, "牡丹云肩纹", "peony_v2", 1, R.drawable.pattern_peony_v2),
            new PatternItem(2L, "进阶几何纹", "geometric_medium", 2, R.drawable.pattern_geometric_medium),
            new PatternItem(3L, "挑战莲云纹", "lotus_cloud_hard", 3, R.drawable.pattern_lotus_cloud_hard)
    ));

    private PatternCatalog() {
    }

    public static List<PatternItem> getItems() {
        return ITEMS;
    }

    public static PatternItem findById(long id) {
        for (PatternItem item : ITEMS) {
            if (item.getId() == id) {
                return item;
            }
        }
        return ITEMS.get(0);
    }

    public static PatternItem findByGuideKey(String guideKey) {
        if (guideKey != null) {
            for (PatternItem item : ITEMS) {
                if (guideKey.equals(item.getGuideKey())) {
                    return item;
                }
            }
        }
        return ITEMS.get(0);
    }
}
