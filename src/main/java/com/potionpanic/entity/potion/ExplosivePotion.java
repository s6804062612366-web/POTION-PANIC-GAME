package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาระเบิดเวทมนตร์ (Explosive Potion)
 * ระเบิดเวทมนตร์รุนแรงรัศมีกว้าง สร้างความเสียหายอย่างหนัก
 */
public class ExplosivePotion extends Potion {

    public ExplosivePotion() {
        super("ยาระเบิดเวท (Explosive)", "ระเบิดรุนแรงรัศมีกว้างมาก",
                ElementType.FIRE, 55, new Color(255, 112, 67), "potion_explosive");
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters) {
        double radius = 150.0 * globalRadiusMultiplier;
        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                double falloff = 1.0 - (dist / radius) * 0.3;
                m.takeDamage((int) (baseDamage * falloff), element);
                m.applyStatus(StatusEffect.BURN, 2.5);
            }
        }
    }
}
