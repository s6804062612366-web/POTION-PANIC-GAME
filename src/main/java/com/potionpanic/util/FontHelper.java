package com.potionpanic.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * จัดการแบบอักษรสำหรับเกม (Typography System)
 * เลือกใช้ฟอนต์ภาษาไทยระดับพรีเมียม (Bai Jamjuree / Sukhumvit Set / Thonburi)
 * เพื่อให้ UI สวยงาม อ่านง่าย ไม่ตกหล่น ไม่ล้นกรอบ
 */
public final class FontHelper {
    private static final String PREFERRED_FONT_FAMILY;

    static {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        ));

        if (available.contains("Bai Jamjuree")) {
            PREFERRED_FONT_FAMILY = "Bai Jamjuree";
        } else if (available.contains("Sukhumvit Set")) {
            PREFERRED_FONT_FAMILY = "Sukhumvit Set";
        } else if (available.contains("Thonburi")) {
            PREFERRED_FONT_FAMILY = "Thonburi";
        } else if (available.contains("Sarabun")) {
            PREFERRED_FONT_FAMILY = "Sarabun";
        } else {
            PREFERRED_FONT_FAMILY = "SansSerif";
        }
    }

    private FontHelper() {}

    public static String getFontFamily() {
        return PREFERRED_FONT_FAMILY;
    }

    public static Font getFont(int style, float size) {
        return new Font(PREFERRED_FONT_FAMILY, style, (int) size);
    }

    public static Font bold(float size) {
        return new Font(PREFERRED_FONT_FAMILY, Font.BOLD, (int) size);
    }

    public static Font plain(float size) {
        return new Font(PREFERRED_FONT_FAMILY, Font.PLAIN, (int) size);
    }
}
