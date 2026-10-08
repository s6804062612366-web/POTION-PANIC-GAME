package com.potionpanic.item;

import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.ElementType;

import java.awt.image.BufferedImage;

/**
 * วัตถุดิบเวทมนตร์ 4 ชนิดหลัก
 */
public enum IngredientType {
    RED_HERB("สมุนไพรเพลิง", "ไฟ", ElementType.FIRE, "red_herb"),
    BLUE_CRYSTAL("ผลึกน้ำแข็ง", "น้ำแข็ง", ElementType.ICE, "blue_crystal"),
    TOXIC_MUSHROOM("เห็ดพิษ", "กรด/พิษ", ElementType.POISON, "toxic_mushroom"),
    THUNDER_ROOT("รากสายฟ้า", "สายฟ้า", ElementType.LIGHTNING, "thunder_root");

    private final String shortName;
    private final String elementThai;
    private final ElementType element;
    private final String spriteName;

    IngredientType(String shortName, String elementThai, ElementType element, String spriteName) {
        this.shortName = shortName;
        this.elementThai = elementThai;
        this.element = element;
        this.spriteName = spriteName;
    }

    public String getDisplayName() {
        return shortName + " (" + elementThai + ")";
    }

    public String getShortName() {
        return shortName;
    }

    public String getElementThai() {
        return elementThai;
    }

    public ElementType getElement() {
        return element;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public BufferedImage getImage() {
        return AssetLoader.getImage(spriteName);
    }
}
