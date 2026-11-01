package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
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
                ElementType.FIRE, 38, new Color(244, 67, 54), "potion_fire");
    }

    @Override
    public double getRadius() {
        return 130.0 * globalRadiusMultiplier;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        if (effects != null) {
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(255, 87, 34)));
            effects.add(VisualEffect.createFireBurst(impactX, impactY, radius));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(255, 87, 34)));
        }

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                double falloff = 1.0 - (dist / radius) * 0.35;
                int dmg = (int) (baseDamage * falloff);
                m.takeDamage(dmg, element);
                m.applyStatus(StatusEffect.BURN, 4.0);
            }
        }
    }
}
