package com.potionpanic.entity;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * ขวดยาเวทมนตร์ที่ถูกขว้างลอยไปตามวิถีโค้ง (Parabolic Arc Projectile)
 */
public class Projectile implements Renderable {
    private final Potion potion;
    private final double startX, startY;
    private final double targetX, targetY;
    private double currentX, currentY;
    private double progress = 0.0;
    private final double flightDuration = 0.55; // บินถึงเป้าใน 0.55 วินาที
    private boolean dead = false;
    private double rotation = 0;

    public Projectile(Potion potion, double startX, double startY, double targetX, double targetY) {
        this.potion = potion;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.currentX = startX;
        this.currentY = startY;
    }

    public void update(double dt, List<Monster> monsters) {
        if (dead) return;

        progress += dt / flightDuration;
        rotation += dt * 12; // หมุนติ้วขณะลอย

        if (progress >= 1.0) {
            progress = 1.0;
            dead = true;
            currentX = targetX;
            currentY = targetY;

            // ตกกระทบเป้าหมาย เกิดเอฟเฟกต์ตามชนิดขวดยา (Polymorphic Effect)
            potion.onImpact(targetX, targetY, monsters);
            SoundManager.getInstance().playHit();
        } else {
            // คำนวณพิกัดตามเส้นโค้งพาราโบลา
            currentX = startX + (targetX - startX) * progress;
            double linearY = startY + (targetY - startY) * progress;
            double arcHeight = 120.0 * Math.sin(progress * Math.PI); // ยกลอยสูงขึ้นตรงกลาง
            currentY = linearY - arcHeight;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public Potion getPotion() {
        return potion;
    }

    @Override
    public void render(Graphics2D g) {
        if (dead) return;

        AffineTransform old = g.getTransform();
        g.translate(currentX, currentY);
        g.rotate(rotation);

        BufferedImage img = potion.getImage();
        int size = 44;
        if (img != null) {
            g.drawImage(img, -size / 2, -size / 2, size, size, null);
        }

        g.setTransform(old);
    }
}
