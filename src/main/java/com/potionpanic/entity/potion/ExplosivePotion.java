package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาระเบิดมหาเวท (Explosive Potion)
 * ระเบิดเวทมนตร์รุนแรงรัศมีกว้าง สร้างความเสียหายมหาเวท Universal เจาะเกราะทุกธาตุ
 */
public class ExplosivePotion extends Potion {

    public ExplosivePotion() {
        super("ยาระเบิดมหาเวท (Explosive)", "ระเบิดรุนแรงรัศมีกว้างมาก เจาะเกราะทุกธาตุและทำลายบอส!",
                ElementType.UNIVERSAL, 55, new Color(255, 112, 67), "potion_explosive");
    }

    @Override
    public double getRadius() {
        return 165.0 * globalRadiusMultiplier;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        if (effects != null) {
            effects.add(VisualEffect.createSupernova(impactX, impactY, radius));
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(255, 112, 67)));
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius * 0.6, new Color(255, 235, 59)));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(255, 112, 67)));
        }

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                double falloff = 1.0 - (dist / radius) * 0.25;
                int dmg = (int) (baseDamage * falloff);
                m.takeDamage(dmg, ElementType.UNIVERSAL);
                m.applyStatus(StatusEffect.BURN, 3.0);
            }
        }
    }
}
