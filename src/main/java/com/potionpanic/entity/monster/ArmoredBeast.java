package com.potionpanic.entity.monster;

import com.potionpanic.util.ElementType;

/**
 * มอนสเตอร์สัตว์ร้ายหุ้มเกราะ (Armored Beast)
 * มีเกราะแข็งทนทานต่อการโจมตีทั่วไป จุดอ่อนเด็ดขาดคือยากรดพิษ (POISON)
 */
public class ArmoredBeast extends Monster {

    public ArmoredBeast(double startX, double startY) {
        super("สัตว์ร้ายเกราะ", 110, 0.55, 12, ElementType.POISON, startX, startY);
    }

    @Override
    public String getSpriteName() {
        return "monster_armored_beast";
    }
}
