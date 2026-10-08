package com.potionpanic.entity;

import java.awt.Graphics2D;

/**
 * Interface สำหรับออบเจกต์ที่สามารถวาดแสดงผลบนหน้าจอได้ (OOP Abstraction)
 */
public interface Renderable {
    void render(Graphics2D g);
}
