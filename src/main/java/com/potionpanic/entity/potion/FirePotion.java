package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาเพลิงกัลป์ (Fire Potion)
 * ระเบิดเพลิงสร้างความเสียหายเป็นกลุ่ม (AOE) ในรัศมี พร้อมเผาไหม้ (Burn) ต่อเนื่อง
 */
public class FirePotion extends Potion {

    public FirePotion() {
        super("ยาเพลิงกัลป์ (Fire Potion)", "ดาเมจวงกว้าง (AOE) + เผาไหม้ต่อเนื่อง",
                ElementType.FIRE, 35, new Color(244, 67, 54), "potion_fire");
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters) {
        double radius = 130.0 * globalRadiusMultiplier;
        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                // ดาเมจลดหลั่นตามระยะห่าง
                double falloff = 1.0 - (dist / radius) * 0.4;
                int dmg = (int) (baseDamage * falloff);
                m.takeDamage(dmg, element);
                m.applyStatus(StatusEffect.BURN, 4.0); // เผาไหม้ 4 วินาที
            }
        }
    }
}
