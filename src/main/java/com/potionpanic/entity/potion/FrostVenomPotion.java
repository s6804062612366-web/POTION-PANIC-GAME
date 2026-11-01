package com.potionpanic.entity.potion;

import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.Color;
import java.util.List;

/**
 * ยาหมอกพายุพิษ (Frost-Venom Potion)
 * สูตรผสาน 3 ธาตุ (เห็ดพิษ + ผลึกน้ำแข็ง + สมุนไพรเพลิง)
 * ชนะทางทั้งมอนสเตอร์ที่แพ้น้ำแข็งและแพ้พิษ พร้อมสโลว์และทำดาเมจพิษต่อเนื่อง
 */
public class FrostVenomPotion extends Potion {

    public FrostVenomPotion() {
        super("ยาหมอกพายุพิษ (Frost-Venom)", "ชนะทางทั้ง โกเลมไฟ & สัตว์ร้ายเกราะ (สโลว์ + พิษ)",
                ElementType.POISON, 32, new Color(0, 150, 136), "potion_frost_venom");
    }

    @Override
    public double getRadius() {
        return 130.0 * globalRadiusMultiplier;
    }

    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        double radius = getRadius();
        if (effects != null) {
            effects.add(VisualEffect.createShockwave(impactX, impactY, radius, new Color(0, 188, 212)));
            effects.add(VisualEffect.createIceSpikes(impactX, impactY, radius * 0.85));
            effects.add(VisualEffect.createLingeringPuddle(impactX, impactY, radius * 0.9, new Color(0, 150, 136)));
            effects.add(VisualEffect.createElementalSplash(impactX, impactY, new Color(0, 229, 255)));
        }

        for (Monster m : allMonsters) {
            if (m.isDead()) continue;
            double dx = (m.getX() + m.getWidth() / 2.0) - impactX;
            double dy = (m.getY() + m.getHeight() / 2.0) - impactY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist <= radius) {
                // ถ้ามอนสเตอร์แพ้น้ำแข็ง ให้คิดดาเมจเป็นธาตุน้ำแข็ง ถ้าแพ้พิษให้คิดเป็นธาตุพิษ
                ElementType hitElement = (m.getWeakness() == ElementType.ICE) ? ElementType.ICE : ElementType.POISON;
                m.takeDamage(baseDamage, hitElement);
                m.applyStatus(StatusEffect.FREEZE, 3.5);
                m.applyStatus(StatusEffect.POISON_DOT, 4.0);
            }
        }
    }
}

