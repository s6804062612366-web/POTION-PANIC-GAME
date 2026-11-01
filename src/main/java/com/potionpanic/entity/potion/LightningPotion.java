package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * ยาสายฟ้า (Lightning Potion)
 * พลังระเบิดสายฟ้าความเสียหายสูง พร้อมสายฟ้าชิ่งและทำให้ชะงัก (Stun) 1.0 วินาที
 */
public class LightningPotion extends Potion {

    public LightningPotion() {
        super("ยาสายฟ้า (Lightning Potion)", "ดาเมจสูง + สายฟ้าชิ่งช็อตชะงัก (Stun)",
                ElementType.LIGHTNING, 48, new Color(255, 235, 59), "potion_lightning");
    }

    @Override
    public double getRadius() {
        return 100.0 * globalRadiusMultiplier;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        List<Monster> struck = new ArrayList<>();

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
                m.applyStatus(StatusEffect.STUN, 1.0);
                struck.add(m);
            }
        }

        if (effects != null) {
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(255, 238, 88)));
            effects.add(VisualEffect.createSkyLightningStrike(impactX, impactY, radius));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(255, 238, 88)));
            if (!struck.isEmpty()) {
                effects.add(VisualEffect.createChainLightning(impactX, impactY, struck, new Color(255, 255, 140)));
            }
        }
    }
}
