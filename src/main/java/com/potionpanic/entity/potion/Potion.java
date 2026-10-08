package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.ElementType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * คลาสนามธรรม Potion (Abstract Class) เป็นต้นแบบของขวดยาเวทมนตร์ทุกชนิด
 * รองรับ Polymorphism ในการสร้างผลกระทบ (Effect) แตกต่างกันไปตามชนิดยา
 */
public abstract class Potion {
    protected static double globalRadiusMultiplier = 1.0;

    protected String name;
    protected String description;
    protected ElementType element;
    protected int baseDamage;
    protected Color flaskColor;
    protected String spriteName;

    public static void setGlobalRadiusMultiplier(double mult) {
        globalRadiusMultiplier = Math.max(1.0, mult);
    }

    public static double getGlobalRadiusMultiplier() {
        return globalRadiusMultiplier;
    }

    public Potion(String name, String description, ElementType element, int baseDamage, Color flaskColor, String spriteName) {
        this.name = name;
        this.description = description;
        this.element = element;
        this.baseDamage = baseDamage;
        this.flaskColor = flaskColor;
        this.spriteName = spriteName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ElementType getElement() {
        return element;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public Color getFlaskColor() {
        return flaskColor;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public BufferedImage getImage() {
        return AssetLoader.getImage(spriteName);
    }

    /**
     * ทำงานเมื่อขวดยาตกกระทบเป้าหมาย (Polymorphism method)
     */
    public abstract void onImpact(double impactX, double impactY, List<Monster> allMonsters);
}
