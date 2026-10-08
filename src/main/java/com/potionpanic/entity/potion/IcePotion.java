package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาน้ำแข็ง (Ice Potion)
 * สร้างความเสียหายน้ำแข็งและทำให้มอนสเตอร์สโลว์เคลื่อนที่ช้าลง 50% เป็นเวลา 4 วินาที
 */
public class IcePotion extends Potion {

    public IcePotion() {
        super("ยาน้ำแข็ง (Ice Potion)", "ดาเมจน้ำแข็ง + สโลว์มอนสเตอร์ช้าลง 50%",
                ElementType.ICE, 25, new Color(33, 150, 243), "potion_ice");
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters) {
        double radius = 110.0 * globalRadiusMultiplier;
        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
                m.applyStatus(StatusEffect.FREEZE, 4.0); // สโลว์ 4 วินาที
            }
        }
    }
}
