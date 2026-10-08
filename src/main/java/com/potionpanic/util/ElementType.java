package com.potionpanic.util;

import java.awt.Color;

/**
 * กำหนดประเภทธาตุของวัตถุดิบ ขวดยา และจุดอ่อนของมอนสเตอร์
 */
public enum ElementType {
    FIRE("ไฟ (Fire)", new Color(255, 87, 34), "[Fire]"),
    ICE("น้ำแข็ง (Ice)", new Color(33, 150, 243), "[Ice]"),
    POISON("กรดพิษ (Poison)", new Color(76, 175, 80), "[Poison]"),
    LIGHTNING("สายฟ้า (Lightning)", new Color(255, 235, 59), "[Lightning]"),
    NONE("ธรรมดา (Neutral)", new Color(158, 158, 158), "[Normal]");

    private final String displayName;
    private final Color color;
    private final String icon;

    ElementType(String displayName, Color color, String icon) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }
}
