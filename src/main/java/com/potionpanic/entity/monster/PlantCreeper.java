package com.potionpanic.entity.monster;

import com.potionpanic.entity.Tower;
import com.potionpanic.util.ElementType;

/**
 * มอนสเตอร์อสูรพฤกษา (Plant Creeper)
 * พลังชีวิตและความเร็วปานกลาง สามารถฟื้นฟูพลังชีวิตช้าๆ ได้ จุดอ่อนคือธาตุไฟ (FIRE)
 */
public class PlantCreeper extends Monster {
    private double regenTimer = 0;

    public PlantCreeper(double startX, double startY) {
        super("อสูรพฤกษา", 90, 0.65, 10, ElementType.FIRE, startX, startY);
    }

    @Override
    public void update(double dt, Tower tower) {
        super.update(dt, tower);
        // ฟื้นฟูเลือดช้าๆ วินาทีละ 2 หน่วย หากไม่ตาย
        if (!isDead()) {
            regenTimer += dt;
            if (regenTimer >= 1.0) {
                regenTimer = 0;
                if (hp < maxHp) {
                    hp = Math.min(maxHp, hp + 2);
                }
            }
        }
    }

    @Override
    public String getSpriteName() {
        return "monster_plant_creeper";
    }
}
