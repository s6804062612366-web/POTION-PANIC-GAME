package com.potionpanic.entity.monster;

import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

/**
 * มอนสเตอร์โกเลมเพลิง (Fire Golem)
 * พลังชีวิตสูง เคลื่อนที่ช้า มีจุดอ่อนต่อน้ำแข็ง (ICE) และมีภูมิคุ้มกันไฟ (Immune to Burn)
 */
public class FireGolem extends Monster {

    public FireGolem(double startX, double startY) {
        super("โกเลมไฟ", 150, 0.45, 15, ElementType.ICE, startX, startY);
    }

    @Override
    public void applyStatus(StatusEffect effect, double duration) {
        // มีภูมิคุ้มกันสถานะไฟไหม้
        if (effect == StatusEffect.BURN) return;
        super.applyStatus(effect, duration);
    }

    @Override
    public String getSpriteName() {
        return "monster_fire_golem";
    }
}
