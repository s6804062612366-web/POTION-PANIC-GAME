package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;

import java.awt.Color;
import java.util.List;

/**
 * ยาล้มเหลว / ขยะ (Failed / Dud Potion)
 * เกิดจากการผสมสูตรวัตถุดิบไม่ถูกต้อง ปาแล้วเกิดเพียงควันและดาเมจเล็กน้อย
 */
public class FailedPotion extends Potion {

    public FailedPotion() {
        super("ยาล้มเหลว (Failed Potion)", "สูตรผิดพลาด! ดาเมจต่ำมาก ไม่มีผลสถานะ",
                ElementType.NONE, 8, new Color(120, 110, 100), "potion_failed");
    }

    @Override
    public double getRadius() {
        return 70.0;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        if (effects != null) {
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(120, 110, 100)));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(120, 110, 100)));
        }

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                m.takeDamage(baseDamage, element);
            }
        }
    }
}
