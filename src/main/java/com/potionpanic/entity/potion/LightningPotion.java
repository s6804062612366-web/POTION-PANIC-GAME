package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาสายฟ้า (Lightning Potion)
 * พลังระเบิดสายฟ้าความเสียหายสูงเป้าหมายเดี่ยวหรือกลุ่มแคบ พร้อมทำให้ชะงัก (Stun) 0.8 วินาที
 */
public class LightningPotion extends Potion {

    public LightningPotion() {
        super("ยาสายฟ้า (Lightning Potion)", "ดาเมจสูง + ทำให้มอนสเตอร์ชะงัก (Stun)",
                ElementType.LIGHTNING, 45, new Color(255, 235, 59), "potion_lightning");
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters) {
        double radius = 90.0 * globalRadiusMultiplier;
        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
                m.applyStatus(StatusEffect.STUN, 0.8); // ชะงัก 0.8 วินาที
            }
        }
    }
}
