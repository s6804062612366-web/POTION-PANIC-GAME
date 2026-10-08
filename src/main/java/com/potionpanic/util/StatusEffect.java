package com.potionpanic.util;

import java.awt.Color;

/**
 * สถานะพิเศษที่เกิดขึ้นกับมอนสเตอร์เมื่อถูกขวดยาโจมตี
 */
public enum StatusEffect {
    BURN("เผาไหม้ (Burn)", new Color(255, 87, 34), "[Burn]"),
    FREEZE("สโลว์/แช่แข็ง (Freeze)", new Color(64, 196, 255), "[Freeze]"),
    POISON_DOT("ติดพิษ (Poison)", new Color(139, 195, 74), "[Poison]"),
    STUN("ชะงัก (Stun)", new Color(255, 235, 59), "[Stun]");

    private final String title;
    private final Color color;
    private final String symbol;

    StatusEffect(String title, Color color, String symbol) {
        this.title = title;
        this.color = color;
        this.symbol = symbol;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }
}
