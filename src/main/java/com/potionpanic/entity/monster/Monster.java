package com.potionpanic.entity.monster;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Damageable;
import com.potionpanic.entity.Renderable;
import com.potionpanic.entity.Tower;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.StatusEffect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * คลาสนามธรรม Monster (Abstract Class) เป็นต้นแบบของมอนสเตอร์ทุกตัว
 * ประยุกต์ใช้ทั้ง 4 เสาหลักของ OOP: Abstraction, Inheritance, Polymorphism, Encapsulation
 */
public abstract class Monster implements Damageable, Renderable {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected double x;
    protected double y;
    protected int width = 72;
    protected int height = 72;
    protected double baseSpeed;
    protected int attackPower;
    protected ElementType weakness;
    protected final Map<StatusEffect, Double> statusDurations = new HashMap<>();

    // การโจมตีหอคอย
    protected double attackCooldown = 0;
    protected boolean reachedTower = false;

    // ข้อความลอยแสดงผลดาเมจ (Floating combat text)
    protected final List<FloatingText> floatingTexts = new ArrayList<>();

    public Monster(String name, int maxHp, double speed, int attackPower, ElementType weakness, double startX, double startY) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.baseSpeed = speed;
        this.attackPower = attackPower;
        this.weakness = weakness;
        this.x = startX;
        this.y = startY;
    }

    public void update(double dt, Tower tower) {
        // อัปเดตและลดเวลาสถานะผิดปกติ
        updateStatuses(dt);

        // จัดการข้อความลอย
        floatingTexts.removeIf(ft -> {
            ft.update(dt);
            return ft.isExpired();
        });

        if (isDead()) return;

        // คำนวณความเร็วหลังหักผลสถานะ
        double speed = baseSpeed;
        if (statusDurations.containsKey(StatusEffect.STUN)) {
            speed = 0;
        } else if (statusDurations.containsKey(StatusEffect.FREEZE)) {
            speed *= 0.5; // เดินช้าลง 50%
        }

        // เดินเข้าหาหอคอย
        int towerRightEdge = tower.getX() + tower.getWidth() - 10;
        if (x > towerRightEdge) {
            x -= speed * dt * 60;
        } else {
            // ถึงหอคอยแล้ว ทำการโจมตีหอคอยตาม Cooldown
            reachedTower = true;
            attackCooldown -= dt;
            if (attackCooldown <= 0) {
                tower.takeDamage(attackPower, ElementType.NONE);
                attackCooldown = 1.2; // โจมตีทุก 1.2 วินาที
            }
        }
    }

    private double dotTickTimer = 0;
    private void updateStatuses(double dt) {
        dotTickTimer += dt;
        boolean triggerTick = false;
        if (dotTickTimer >= 0.5) { // ติ๊กดาเมจต่อเนื่องทุก 0.5 วิ
            triggerTick = true;
            dotTickTimer = 0;
        }

        List<StatusEffect> expired = new ArrayList<>();
        for (Map.Entry<StatusEffect, Double> entry : statusDurations.entrySet()) {
            double remaining = entry.getValue() - dt;
            if (remaining <= 0) {
                expired.add(entry.getKey());
            } else {
                entry.setValue(remaining);

                if (triggerTick) {
                    if (entry.getKey() == StatusEffect.BURN) {
                        int burnDmg = 4;
                        this.hp = Math.max(0, this.hp - burnDmg);
                        addFloatingText("-" + burnDmg, Color.ORANGE);
                    } else if (entry.getKey() == StatusEffect.POISON_DOT) {
                        int poisonDmg = 5;
                        this.hp = Math.max(0, this.hp - poisonDmg);
                        addFloatingText("-" + poisonDmg, Color.GREEN);
                    }
                }
            }
        }
        for (StatusEffect s : expired) {
            statusDurations.remove(s);
        }
    }

    @Override
    public void takeDamage(int amount, ElementType element) {
        int finalDamage;
        boolean isCriticalWeakness = false;

        // คำนวณธาตุแพ้ทางเด็ดขาด (Strict Elemental Resistance vs Critical Weakness)
        if (element != ElementType.NONE && element == this.weakness) {
            finalDamage = (int) (amount * 2.5); // โจมตีตรงจุดอ่อน รุนแรง 2.5 เท่า
            isCriticalWeakness = true;
            SoundManager.getInstance().playCritical();
        } else {
            // โจมตีผิดธาตุ ดาเมจแทบไม่เข้า (ต้านทาน 85%)
            finalDamage = Math.max(1, (int) (amount * 0.15));
        }

        this.hp = Math.max(0, this.hp - finalDamage);

        if (isCriticalWeakness) {
            addFloatingText("CRIT! -" + finalDamage, Color.YELLOW);
        } else {
            addFloatingText("RESIST! -" + finalDamage, new Color(200, 200, 200));
        }
    }

    public void applyStatus(StatusEffect effect, double duration) {
        // ต่ออายุสถานะถ้ามีอยู่แล้ว หรือเพิ่มสถานะใหม่
        double current = statusDurations.getOrDefault(effect, 0.0);
        statusDurations.put(effect, Math.max(current, duration));
    }

    public void addFloatingText(String text, Color color) {
        floatingTexts.add(new FloatingText(text, x + width / 2.0, y - 8, color));
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ElementType getWeakness() {
        return weakness;
    }

    public String getName() {
        return name;
    }

    /**
     * คลาสลูกต้องระบุชื่อ Sprite ที่ใช้ (Polymorphic abstract method)
     */
    public abstract String getSpriteName();

    @Override
    public void render(Graphics2D g) {
        if (isDead()) return;

        // วาดรูปมอนสเตอร์
        BufferedImage sprite = AssetLoader.getImage(getSpriteName());
        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, width, height, null);
        }

        // วาดเอฟเฟกต์ติดสถานะครอบตัวมอนสเตอร์
        if (statusDurations.containsKey(StatusEffect.FREEZE)) {
            g.setColor(new Color(64, 196, 255, 90));
            g.fillOval((int) x, (int) y, width, height);
        }
        if (statusDurations.containsKey(StatusEffect.BURN)) {
            g.setColor(new Color(255, 87, 34, 90));
            g.fillOval((int) x, (int) y, width, height);
        }
        if (statusDurations.containsKey(StatusEffect.POISON_DOT)) {
            g.setColor(new Color(76, 175, 80, 90));
            g.fillOval((int) x, (int) y, width, height);
        }

        // วาดป้ายชื่อมอนสเตอร์และจุดอ่อนภาษาไทยชัดเจน พร้อมไอคอนยาเหนือหัว
        String weaknessThai = switch (weakness) {
            case FIRE -> "ยาเพลิง";
            case ICE -> "ยาน้ำแข็ง";
            case POISON -> "ยากรดพิษ";
            case LIGHTNING -> "ยาสายฟ้า";
            default -> "ทั่วไป";
        };
        String badgeText = name + " [แพ้: " + weaknessThai + "]";

        String potionSprite = switch (weakness) {
            case FIRE -> "potion_fire";
            case ICE -> "potion_ice";
            case POISON -> "potion_poison";
            case LIGHTNING -> "potion_lightning";
            default -> null;
        };
        BufferedImage potionImg = (potionSprite != null) ? AssetLoader.getImage(potionSprite) : null;

        g.setFont(com.potionpanic.util.FontHelper.bold(11));
        FontMetrics fm = g.getFontMetrics();
        int iconPad = (potionImg != null) ? 20 : 0;
        int badgeW = fm.stringWidth(badgeText) + 14 + iconPad;
        int badgeH = 20;
        int badgeX = (int) x + (width - badgeW) / 2;
        int badgeY = (int) y - 36;

        g.setColor(new Color(15, 12, 25, 230));
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);
        g.setColor(weakness.getColor());
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);

        if (potionImg != null) {
            g.drawImage(potionImg, badgeX + 4, badgeY + 1, 18, 18, null);
        }

        g.setColor(Color.WHITE);
        g.drawString(badgeText, badgeX + 8 + iconPad, badgeY + 14);

        // วาดหลอด HP เหนือหัว
        int barW = width;
        int barH = 7;
        int barX = (int) x;
        int barY = (int) y - 13;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(barX, barY, barW, barH);

        double ratio = (double) hp / maxHp;
        g.setColor(ratio > 0.4 ? new Color(244, 67, 54) : Color.RED);
        g.fillRect(barX, barY, (int) (barW * ratio), barH);

        // วาดข้อความลอย (Floating Texts)
        for (FloatingText ft : floatingTexts) {
            ft.render(g);
        }
    }

    /**
     * คลาสย่อยภายในสำหรับแสดงผลตัวเลขดาเมจลอยขึ้นแล้วจางหาย
     */
    public static class FloatingText {
        private final String text;
        private double fx, fy;
        private final Color color;
        private double lifetime = 0.8;
        private final double maxLifetime = 0.8;

        public FloatingText(String text, double x, double y, Color color) {
            this.text = text;
            this.fx = x;
            this.fy = y;
            this.color = color;
        }

        public void update(double dt) {
            lifetime -= dt;
            fy -= 25 * dt; // ลอยขึ้นด้านบน
        }

        public boolean isExpired() {
            return lifetime <= 0;
        }

        public void render(Graphics2D g) {
            float alpha = (float) Math.max(0, Math.min(1.0, lifetime / maxLifetime));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
            g.setFont(com.potionpanic.util.FontHelper.bold(13));
            g.drawString(text, (int) fx - 10, (int) fy);
        }
    }
}
