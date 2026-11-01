package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยากรดพิษ (Poison / Acid Potion)
 * สร้างความเสียหายกรดกัดกร่อน ละลายเกราะ และทิ้งบ่อพิษกัดกร่อนต่อเนื่อง 5 วินาที
 */
public class PoisonPotion extends Potion {

    public PoisonPotion() {
        super("ยากรดพิษ (Acid/Poison)", "ละลายเกราะ + ทำดาเมจพิษต่อเนื่อง 5 วินาที",
                ElementType.POISON, 25, new Color(76, 175, 80), "potion_poison");
    }

    @Override
    public double getRadius() {
        return 110.0 * globalRadiusMultiplier;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        if (effects != null) {
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(118, 255, 3)));
            effects.add(VisualEffect.createLingeringPuddle(impactX, impactY, radius * 0.85, new Color(76, 175, 80)));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(76, 175, 80)));
        }

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
                m.applyStatus(StatusEffect.POISON_DOT, 5.0);
            }
        }
    }
}
