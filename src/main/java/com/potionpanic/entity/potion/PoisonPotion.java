package com.potionpanic.entity.potion;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยากรดพิษ (Poison / Acid Potion)
 * สร้างความเสียหายกรดกัดกร่อน ละลายเกราะ และติดพิษ (Poison DoT) ต่อเนื่อง 5 วินาที
 */
public class PoisonPotion extends Potion {

    public PoisonPotion() {
        super("ยากรดพิษ (Acid/Poison)", "ละลายเกราะ + ทำดาเมจพิษต่อเนื่อง 5 วินาที",
                ElementType.POISON, 20, new Color(76, 175, 80), "potion_poison");
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters) {
        double radius = 100.0 * globalRadiusMultiplier;
        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
                m.applyStatus(StatusEffect.POISON_DOT, 5.0); // ติดพิษ 5 วินาที
            }
        }
    }
}
