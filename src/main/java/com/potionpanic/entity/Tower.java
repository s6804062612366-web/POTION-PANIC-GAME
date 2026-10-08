package com.potionpanic.entity;

import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ElementType;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * หอคอยเวทมนตร์ของผู้เล่น (Tower)
 * ต้องปกป้องไม่ให้มอนสเตอร์บุกเข้ามาตีจนพลังชีวิตเหลือ 0
 */
public class Tower implements Damageable, Renderable {
    private int maxHp;
    private int hp;
    private final int x;
    private final int y;
    private final int width = 120;
    private final int height = 180;

    public Tower(int maxHp, int x, int y) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.x = x;
        this.y = y;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    @Override
    public void takeDamage(int amount, ElementType element) {
        this.hp = Math.max(0, this.hp - amount);
    }

    @Override
    public boolean isDead() {
        return hp <= 0;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    public void reset() {
        this.hp = maxHp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public void render(Graphics2D g) {
        // วาดภาพหอคอย
        BufferedImage towerImg = AssetLoader.getImage("tower");
        if (towerImg != null) {
            g.drawImage(towerImg, x, y, width, height, null);
        }

        // วาดแถบพลังชีวิตหอคอย (Tower HP Bar)
        int barWidth = 140;
        int barHeight = 16;
        int barX = x - 10;
        int barY = y - 24;

        // พื้นหลังหลอดเลือด
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4, 8, 8);

        // แถบเลือดปัจจุบัน
        double hpRatio = (double) hp / maxHp;
        Color barColor;
        if (hpRatio > 0.5) {
            barColor = new Color(76, 175, 80);
        } else if (hpRatio > 0.25) {
            barColor = new Color(255, 193, 7);
        } else {
            barColor = new Color(244, 67, 54);
        }
        g.setColor(barColor);
        g.fillRoundRect(barX, barY, (int) (barWidth * hpRatio), barHeight, 6, 6);

        // ตัวเลขเลือด
        g.setColor(Color.WHITE);
        g.setFont(Constants.FONT_SMALL);
        String hpText = "หอคอย HP: " + hp + "/" + maxHp;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hpText, barX + (barWidth - fm.stringWidth(hpText)) / 2, barY + 12);
    }
}
