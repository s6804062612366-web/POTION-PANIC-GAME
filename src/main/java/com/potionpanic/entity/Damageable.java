package com.potionpanic.entity;

import com.potionpanic.util.ElementType;

/**
 * Interface สำหรับออบเจกต์ที่สามารถรับความเสียหายได้ (OOP Abstraction)
 */
public interface Damageable {
    void takeDamage(int amount, ElementType element);
    boolean isDead();
    int getHp();
    int getMaxHp();
}
