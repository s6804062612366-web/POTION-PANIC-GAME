package com.potionpanic.entity;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.util.ElementType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * ขวดยาเวทมนตร์ที่ถูกขว้างลอยไปตามวิถีโค้งพาราโบลา (Parabolic Arc Projectile)
 * พร้อมระบบหางละอองแสงเวทมนตร์ตามธาตุ (Elemental Comet Trail),
 * ออร่าเรืองแสงรอบขวดยา, และวงแหวนเป้าหมายล็อกจุดตกกระทบ
 */
public class Projectile implements Renderable {
    private final Potion potion;
    private final double startX, startY;
    private final double targetX, targetY;
    private double currentX, currentY;
    private double progress = 0.0;
    private final double flightDuration = 0.52; // บินถึงเป้าใน ~0.5 วินาที
    private boolean dead = false;
    private double rotation = 0;

    // ระบบละอองหางแสงเวทมนตร์ตามหลังขวดยา
    private static class TrailParticle {
        double px, py;
        double vx, vy;
        Color color;
        double size;
        double life;
        double maxLife;
    }
    private final List<TrailParticle> trail = new ArrayList<>();

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
        update(dt, monsters, null);
    }

    public void update(double dt, List<Monster> monsters, List<VisualEffect> effects) {
        if (dead) return;

        progress += dt / flightDuration;
        rotation += dt * 14; // หมุนติ้วขณะลอยกลางอากาศ

        // อัปเดตละอองแสงหางยา
        for (TrailParticle tp : trail) {
            tp.px += tp.vx * dt;
            tp.py += tp.vy * dt;
            tp.life -= dt;
        }
        trail.removeIf(tp -> tp.life <= 0);

        if (progress >= 1.0) {
            progress = 1.0;
            dead = true;
            currentX = targetX;
            currentY = targetY;

            // ตกกระทบเป้าหมาย เกิดเอฟเฟกต์ตามชนิดขวดยา (Polymorphic Effect)
            potion.onImpact(targetX, targetY, monsters, effects);
            SoundManager.getInstance().playHit();
        } else {
            // คำนวณพิกัดตามเส้นโค้งพาราโบลา
            currentX = startX + (targetX - startX) * progress;
            double linearY = startY + (targetY - startY) * progress;
            double arcHeight = 120.0 * Math.sin(progress * Math.PI); // ยกลอยสูงขึ้นตรงกลาง
            currentY = linearY - arcHeight;

            // เสกละอองหางแสงเวทมนตร์ตามชนิดขวดยา
            Color baseCol = potion.getElement().getColor();
            if (potion.getElement() == ElementType.UNIVERSAL) {
                baseCol = new Color(225, 110, 255);
            } else if (potion.getElement() == ElementType.NONE) {
                baseCol = new Color(160, 150, 140);
            }

            for (int i = 0; i < 3; i++) {
                TrailParticle tp = new TrailParticle();
                tp.px = currentX + (Math.random() - 0.5) * 12;
                tp.py = currentY + (Math.random() - 0.5) * 12;
                tp.vx = (Math.random() - 0.5) * 35;
                tp.vy = (Math.random() - 0.5) * 35 + 15; // ค่อยๆ ลอยทิ้งหางลง
                tp.color = (Math.random() < 0.35) ? Color.WHITE : baseCol;
                tp.size = 4 + Math.random() * 6;
                tp.life = 0.25 + Math.random() * 0.12;
                tp.maxLife = tp.life;
                trail.add(tp);
            }
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

        Color elCol = potion.getElement().getColor();
        if (potion.getElement() == ElementType.UNIVERSAL) {
            elCol = new Color(225, 110, 255);
        } else if (potion.getElement() == ElementType.NONE) {
            elCol = new Color(160, 150, 140);
        }

        // 1. วาดวงแหวนเป้าหมายล็อกจุดตกกระทบบนพื้น (Target Landing Reticle)
        double ringProgress = Math.min(1.0, progress * 1.15);
        int ringRadius = (int) (38 * (1.0 - ringProgress * 0.45));
        int ringAlpha = (int) (210 * progress);
        g.setColor(new Color(elCol.getRed(), elCol.getGreen(), elCol.getBlue(), Math.min(255, ringAlpha)));
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval((int) (targetX - ringRadius), (int) (targetY - ringRadius / 2), ringRadius * 2, ringRadius);

        // เส้นกากบาทจุดศูนย์กลางเป้าหมาย
        int crossLen = (int) (ringRadius * 0.65);
        g.drawLine((int) targetX - crossLen, (int) targetY, (int) targetX + crossLen, (int) targetY);
        g.drawLine((int) targetX, (int) targetY - crossLen / 2, (int) targetX, (int) targetY + crossLen / 2);

        // 2. วาดเงาขวดยาบนพื้น
        double shadowY = startY + (targetY - startY) * progress;
        g.setColor(new Color(0, 0, 0, (int) (70 * (1.0 - Math.sin(progress * Math.PI) * 0.45))));
        g.fillOval((int) (currentX - 16), (int) (shadowY - 7), 32, 14);

        // 3. วาดละอองหางแสงเวทมนตร์ (Elemental Trail Particles)
        for (TrailParticle tp : trail) {
            float alpha = (float) Math.max(0.0, Math.min(1.0, tp.life / tp.maxLife));
            int a = (int) (alpha * 200);
            g.setColor(new Color(tp.color.getRed(), tp.color.getGreen(), tp.color.getBlue(), a));
            int s = (int) (tp.size * alpha);
            g.fillOval((int) (tp.px - s / 2), (int) (tp.py - s / 2), Math.max(1, s), Math.max(1, s));
        }

        // 4. วาดออร่าเรืองแสงรอบขวดยาขณะลอย
        g.setColor(new Color(elCol.getRed(), elCol.getGreen(), elCol.getBlue(), 80));
        g.fillOval((int) (currentX - 24), (int) (currentY - 24), 48, 48);
        g.setColor(new Color(255, 255, 255, 110));
        g.fillOval((int) (currentX - 11), (int) (currentY - 11), 22, 22);

        // 5. วาดรูปขวดยาที่กำลังหมุนติ้วกลางอากาศ
        AffineTransform old = g.getTransform();
        g.translate(currentX, currentY);
        g.rotate(rotation);

        BufferedImage img = potion.getImage();
        int size = 46;
        if (img != null) {
            g.drawImage(img, -size / 2, -size / 2, size, size, null);
        }

        g.setTransform(old);
    }
}
