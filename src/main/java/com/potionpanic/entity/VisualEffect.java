package com.potionpanic.entity;

import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.FontHelper;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * เอฟเฟกต์ภาพพลวัตบนสนามรบ (Dynamic Visual Effects)
 * แสดงผลคลื่นกระแทก (Shockwave), บ่อน้ำยาตกค้าง (Lingering Puddle),
 * สายฟ้าชิ่ง (Chain Lightning), ไอเทมลอย (Flying Item), การระเบิดสลายของมอนสเตอร์ (Death Burst),
 * ป้ายประกาศ Wave Banner, ละอองน้ำยากระจาย (Elemental Splash), เสาเพลิงปะทุ (Fire Burst),
 * หนามน้ำแข็งพุ่ง (Ice Spikes), สายฟ้าฟาดจากฟ้า (Sky Lightning Strike), และมหาเวทระเบิด (Supernova)
 */
public class VisualEffect implements Renderable {
    public enum EffectType {
        SHOCKWAVE,
        LINGERING_PUDDLE,
        CHAIN_LIGHTNING,
        FLYING_ITEM,
        DEATH_BURST,
        WAVE_BANNER,
        ELEMENTAL_SPLASH,
        FIRE_BURST,
        ICE_SPIKES,
        SKY_LIGHTNING_STRIKE,
        SUPERNOVA
    }

    private final EffectType type;
    private double x, y;
    private final double radius;
    private final Color color;
    private double lifetime;
    private final double maxLifetime;

    // สำหรับ Chain Lightning และ Sky Lightning
    private final List<Point> chainPoints = new ArrayList<>();

    // สำหรับ Flying Item
    private double startX, startY;
    private double targetX, targetY;
    private BufferedImage itemImage;

    // สำหรับ Death Burst, Elemental Splash, Fire Burst, Supernova
    private static class BurstParticle {
        double px, py;
        double vx, vy;
        Color pColor;
        double size;
    }
    private final List<BurstParticle> burstParticles = new ArrayList<>();

    // สำหรับ Ice Spikes (มุมและความยาวของผลึกหนามน้ำแข็ง)
    private static class IceSpikeData {
        double angle;
        double maxLen;
        double width;
    }
    private final List<IceSpikeData> iceSpikes = new ArrayList<>();

    // สำหรับ Wave Banner
    private String bannerText;
    private String bannerSubtext;

    public VisualEffect(EffectType type, double x, double y, double radius, Color color, double duration) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.lifetime = duration;
        this.maxLifetime = duration;
    }

    public static VisualEffect createShockwave(double x, double y, double radius, Color color) {
        return new VisualEffect(EffectType.SHOCKWAVE, x, y, radius, color, 0.45);
    }

    public static VisualEffect createLingeringPuddle(double x, double y, double radius, Color color) {
        return new VisualEffect(EffectType.LINGERING_PUDDLE, x, y, radius, color, 3.2);
    }

    public static VisualEffect createChainLightning(double startX, double startY, List<Monster> struckMonsters, Color color) {
        VisualEffect eff = new VisualEffect(EffectType.CHAIN_LIGHTNING, startX, startY, 100, color, 0.35);
        eff.chainPoints.add(new Point((int) startX, (int) startY));
        for (Monster m : struckMonsters) {
            eff.chainPoints.add(new Point((int) (m.getX() + m.getWidth() / 2.0), (int) (m.getY() + m.getHeight() / 2.0)));
        }
        return eff;
    }

    public static VisualEffect createFlyingItem(BufferedImage img, double sx, double sy, double tx, double ty) {
        VisualEffect eff = new VisualEffect(EffectType.FLYING_ITEM, sx, sy, 40, Color.WHITE, 0.32);
        eff.startX = sx;
        eff.startY = sy;
        eff.targetX = tx;
        eff.targetY = ty;
        eff.itemImage = img;
        return eff;
    }

    public static VisualEffect createDeathBurst(double x, double y, Color color) {
        VisualEffect eff = new VisualEffect(EffectType.DEATH_BURST, x, y, 60, color, 0.55);
        for (int i = 0; i < 18; i++) {
            BurstParticle p = new BurstParticle();
            p.px = x;
            p.py = y;
            double angle = Math.random() * Math.PI * 2.0;
            double speed = 40.0 + Math.random() * 90.0;
            p.vx = Math.cos(angle) * speed;
            p.vy = Math.sin(angle) * speed;
            p.pColor = color;
            p.size = 4 + Math.random() * 6;
            eff.burstParticles.add(p);
        }
        return eff;
    }

    public static VisualEffect createWaveBanner(String text, String subtext) {
        VisualEffect eff = new VisualEffect(EffectType.WAVE_BANNER, 640, 140, 0, Color.ORANGE, 2.2);
        eff.bannerText = text;
        eff.bannerSubtext = subtext;
        return eff;
    }

    /**
     * ละอองน้ำยาและเศษแก้วกระจายตัวรอบจุดตกกระทบ
     */
    public static VisualEffect createElementalSplash(double x, double y, Color color) {
        VisualEffect eff = new VisualEffect(EffectType.ELEMENTAL_SPLASH, x, y, 70, color, 0.50);
        for (int i = 0; i < 22; i++) {
            BurstParticle p = new BurstParticle();
            p.px = x;
            p.py = y;
            double angle = Math.random() * Math.PI * 2.0;
            double speed = 50.0 + Math.random() * 110.0;
            p.vx = Math.cos(angle) * speed;
            p.vy = Math.sin(angle) * speed * 0.65; // แผ่ออกแนวระนาบวงรี
            p.pColor = (Math.random() < 0.35) ? Color.WHITE : color;
            p.size = 3.5 + Math.random() * 5.5;
            eff.burstParticles.add(p);
        }
        return eff;
    }

    /**
     * เสาเพลิงปะทุพร้อมเปลวไฟและสะเก็ดไฟลอยขึ้นสู่อากาศ
     */
    public static VisualEffect createFireBurst(double x, double y, double radius) {
        VisualEffect eff = new VisualEffect(EffectType.FIRE_BURST, x, y, radius, new Color(255, 87, 34), 0.55);
        for (int i = 0; i < 20; i++) {
            BurstParticle p = new BurstParticle();
            p.px = x + (Math.random() - 0.5) * (radius * 0.7);
            p.py = y + (Math.random() - 0.5) * 15;
            p.vx = (Math.random() - 0.5) * 45;
            p.vy = -60.0 - Math.random() * 95.0; // ลอยพุ่งขึ้นข้างบน
            p.pColor = (Math.random() < 0.4) ? new Color(255, 235, 59) : new Color(255, 87, 34);
            p.size = 5.0 + Math.random() * 7.0;
            eff.burstParticles.add(p);
        }
        return eff;
    }

    /**
     * หนามผลึกน้ำแข็งพุ่งแผ่ออกจากจุดศูนย์กลาง (Ice Spikes / Frost Nova)
     */
    public static VisualEffect createIceSpikes(double x, double y, double radius) {
        VisualEffect eff = new VisualEffect(EffectType.ICE_SPIKES, x, y, radius, new Color(64, 196, 255), 0.52);
        int spikeCount = 10;
        for (int i = 0; i < spikeCount; i++) {
            IceSpikeData sp = new IceSpikeData();
            sp.angle = (i * (Math.PI * 2.0 / spikeCount)) + (Math.random() - 0.5) * 0.25;
            sp.maxLen = (radius * 0.65) + Math.random() * (radius * 0.45);
            sp.width = 6.0 + Math.random() * 5.0;
            eff.iceSpikes.add(sp);
        }
        return eff;
    }

    /**
     * สายฟ้าฟาดลงมาจากฟากฟ้าผ่าลงสู่จุดกระทบ (Sky Lightning Strike)
     */
    public static VisualEffect createSkyLightningStrike(double x, double y, double radius) {
        VisualEffect eff = new VisualEffect(EffectType.SKY_LIGHTNING_STRIKE, x, y, radius, new Color(255, 238, 88), 0.42);
        int segCount = 6;
        double currentSegY = 0;
        double stepY = y / segCount;
        eff.chainPoints.add(new Point((int) (x + (Math.random() - 0.5) * 40), 0));

        for (int i = 1; i < segCount; i++) {
            currentSegY += stepY;
            int jagX = (int) (x + (Math.random() - 0.5) * 35);
            eff.chainPoints.add(new Point(jagX, (int) currentSegY));
        }
        eff.chainPoints.add(new Point((int) x, (int) y));
        return eff;
    }

    /**
     * มหาเวทระเบิด Supernova อานุภาพสูง รังสีดวงดาวซ้อน 3 ชั้น
     */
    public static VisualEffect createSupernova(double x, double y, double radius) {
        VisualEffect eff = new VisualEffect(EffectType.SUPERNOVA, x, y, radius, new Color(225, 110, 255), 0.65);
        for (int i = 0; i < 26; i++) {
            BurstParticle p = new BurstParticle();
            p.px = x;
            p.py = y;
            double angle = Math.random() * Math.PI * 2.0;
            double speed = 60.0 + Math.random() * 140.0;
            p.vx = Math.cos(angle) * speed;
            p.vy = Math.sin(angle) * speed * 0.75;
            p.pColor = (Math.random() < 0.45) ? new Color(255, 215, 64) : new Color(225, 110, 255);
            p.size = 5.0 + Math.random() * 7.0;
            eff.burstParticles.add(p);
        }
        return eff;
    }

    public void update(double dt) {
        lifetime -= dt;

        if (type == EffectType.DEATH_BURST || type == EffectType.ELEMENTAL_SPLASH) {
            for (BurstParticle p : burstParticles) {
                p.px += p.vx * dt;
                p.py += p.vy * dt;
                p.vy += 65.0 * dt; // gravity
            }
        } else if (type == EffectType.FIRE_BURST) {
            for (BurstParticle p : burstParticles) {
                p.px += p.vx * dt;
                p.py += p.vy * dt;
                p.size = Math.max(1.0, p.size - dt * 4.0);
            }
        } else if (type == EffectType.SUPERNOVA) {
            for (BurstParticle p : burstParticles) {
                p.px += p.vx * dt;
                p.py += p.vy * dt;
            }
        }
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    @Override
    public void render(Graphics2D g) {
        if (isExpired()) return;

        float progress = 1.0f - (float) Math.max(0, lifetime / maxLifetime);
        float alpha = (float) Math.max(0.0, Math.min(1.0, lifetime / maxLifetime));

        switch (type) {
            case SHOCKWAVE -> {
                int currentR = (int) (radius * (0.3 + progress * 0.7));
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 160)));
                g.setStroke(new BasicStroke(4.0f * (1.0f - progress) + 1.0f));
                g.drawOval((int) (x - currentR), (int) (y - currentR / 2), currentR * 2, currentR);

                int innerR = (int) (currentR * 0.5);
                g.setColor(new Color(255, 255, 255, (int) (alpha * 120)));
                g.fillOval((int) (x - innerR), (int) (y - innerR / 2), innerR * 2, innerR);
            }
            case LINGERING_PUDDLE -> {
                int pw = (int) (radius * 1.6);
                int ph = (int) (radius * 0.7);
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 110)));
                g.fillOval((int) (x - pw / 2), (int) (y - ph / 2), pw, ph);

                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 190)));
                g.setStroke(new BasicStroke(2.0f));
                g.drawOval((int) (x - pw / 2), (int) (y - ph / 2), pw, ph);

                long t = System.currentTimeMillis();
                for (int i = 0; i < 4; i++) {
                    double bx = x + Math.sin(t / 200.0 + i * 1.5) * (pw * 0.35);
                    double by = y + Math.cos(t / 250.0 + i * 1.2) * (ph * 0.3);
                    g.setColor(new Color(255, 255, 255, (int) (alpha * 150)));
                    g.fillOval((int) bx, (int) by, 5, 5);
                }
            }
            case CHAIN_LIGHTNING -> {
                if (chainPoints.size() < 2) return;
                g.setColor(new Color(255, 238, 88, (int) (alpha * 255)));
                g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                for (int i = 0; i < chainPoints.size() - 1; i++) {
                    Point p1 = chainPoints.get(i);
                    Point p2 = chainPoints.get(i + 1);

                    Path2D bolt = new Path2D.Double();
                    bolt.moveTo(p1.x, p1.y);
                    int midX = (p1.x + p2.x) / 2 + (int) ((Math.random() - 0.5) * 20);
                    int midY = (p1.y + p2.y) / 2 + (int) ((Math.random() - 0.5) * 20);
                    bolt.lineTo(midX, midY);
                    bolt.lineTo(p2.x, p2.y);
                    g.draw(bolt);

                    g.setColor(new Color(255, 255, 255, (int) (alpha * 200)));
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
            case FLYING_ITEM -> {
                double t = progress;
                double curX = startX + (targetX - startX) * t;
                double curY = startY + (targetY - startY) * t - 70.0 * Math.sin(t * Math.PI);

                // หางแสงประกาย (Sparkle Trail)
                g.setColor(new Color(255, 215, 0, (int) (alpha * 180)));
                g.fillOval((int) curX - 6, (int) curY - 6, 12, 12);
                g.setColor(Color.WHITE);
                g.fillOval((int) curX - 3, (int) curY - 3, 6, 6);

                if (itemImage != null) {
                    int size = 42;
                    g.drawImage(itemImage, (int) curX - size / 2, (int) curY - size / 2, size, size, null);
                }
            }
            case DEATH_BURST, ELEMENTAL_SPLASH -> {
                for (BurstParticle p : burstParticles) {
                    g.setColor(new Color(p.pColor.getRed(), p.pColor.getGreen(), p.pColor.getBlue(), (int) (alpha * 220)));
                    int s = (int) (p.size * (1.0f - progress * 0.45f));
                    g.fillOval((int) (p.px - s / 2), (int) (p.py - s / 2), Math.max(1, s), Math.max(1, s));
                    g.setColor(new Color(255, 255, 255, (int) (alpha * 170)));
                    g.fillOval((int) (p.px - s / 4), (int) (p.py - s / 4), Math.max(1, s / 2), Math.max(1, s / 2));
                }
            }
            case FIRE_BURST -> {
                // แกนกลางเปลวไฟลุกท่วม
                int plumeW = (int) (radius * 0.9 * (1.0f - progress * 0.3f));
                int plumeH = (int) (radius * 1.3 * (0.4f + progress * 0.6f));
                g.setColor(new Color(255, 87, 34, (int) (alpha * 175)));
                g.fillOval((int) (x - plumeW / 2), (int) (y - plumeH), plumeW, plumeH);

                int coreW = (int) (plumeW * 0.55);
                int coreH = (int) (plumeH * 0.65);
                g.setColor(new Color(255, 235, 59, (int) (alpha * 220)));
                g.fillOval((int) (x - coreW / 2), (int) (y - coreH), coreW, coreH);

                for (BurstParticle p : burstParticles) {
                    g.setColor(new Color(p.pColor.getRed(), p.pColor.getGreen(), p.pColor.getBlue(), (int) (alpha * 220)));
                    int s = (int) p.size;
                    g.fillOval((int) (p.px - s / 2), (int) (p.py - s / 2), Math.max(1, s), Math.max(1, s));
                }
            }
            case ICE_SPIKES -> {
                // หนามผลึกน้ำแข็งพุ่งจากพื้น
                for (IceSpikeData sp : iceSpikes) {
                    double curLen = sp.maxLen * Math.sin(Math.min(1.0, progress * 2.0) * Math.PI * 0.5);
                    double tipX = x + Math.cos(sp.angle) * curLen;
                    double tipY = y + Math.sin(sp.angle) * (curLen * 0.55); // สัดส่วนวงรีพื้น

                    double perpAngle = sp.angle + Math.PI / 2.0;
                    double baseOffset = sp.width * (1.0f - progress * 0.5f);
                    double b1x = x + Math.cos(perpAngle) * baseOffset;
                    double b1y = y + Math.sin(perpAngle) * (baseOffset * 0.55);
                    double b2x = x - Math.cos(perpAngle) * baseOffset;
                    double b2y = y - Math.sin(perpAngle) * (baseOffset * 0.55);

                    Polygon poly = new Polygon();
                    poly.addPoint((int) b1x, (int) b1y);
                    poly.addPoint((int) tipX, (int) tipY);
                    poly.addPoint((int) b2x, (int) b2y);

                    g.setColor(new Color(64, 196, 255, (int) (alpha * 185)));
                    g.fillPolygon(poly);
                    g.setColor(new Color(255, 255, 255, (int) (alpha * 230)));
                    g.setStroke(new BasicStroke(1.2f));
                    g.drawPolygon(poly);
                }

                // หมอกไอเย็นปกคลุม
                int mistR = (int) (radius * (0.4 + progress * 0.6));
                g.setColor(new Color(220, 245, 255, (int) (alpha * 90)));
                g.fillOval((int) (x - mistR), (int) (y - mistR / 2), mistR * 2, mistR);
            }
            case SKY_LIGHTNING_STRIKE -> {
                // ลำแสงสายฟ้าผ่าลงมาจากท้องฟ้า
                if (chainPoints.size() >= 2) {
                    // แสงเรืองรองรอบสายฟ้า
                    g.setColor(new Color(255, 238, 88, (int) (alpha * 190)));
                    g.setStroke(new BasicStroke(5.0f * (1.0f - progress) + 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for (int i = 0; i < chainPoints.size() - 1; i++) {
                        Point p1 = chainPoints.get(i);
                        Point p2 = chainPoints.get(i + 1);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }

                    // แกนกลางสีขาวเจิดจ้า
                    g.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
                    g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for (int i = 0; i < chainPoints.size() - 1; i++) {
                        Point p1 = chainPoints.get(i);
                        Point p2 = chainPoints.get(i + 1);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }

                // คลื่นวงแหวนไฟฟ้าปะทุบนพื้น
                int groundR = (int) (radius * (0.3 + progress * 0.7));
                g.setColor(new Color(255, 255, 140, (int) (alpha * 210)));
                g.setStroke(new BasicStroke(2.2f));
                g.drawOval((int) (x - groundR), (int) (y - groundR / 2), groundR * 2, groundR);
            }
            case SUPERNOVA -> {
                // คลื่นดาราจักรซ้อน 3 ชั้น
                int r1 = (int) (radius * (0.25 + progress * 0.75));
                int r2 = (int) (radius * (0.45 + progress * 0.55));

                g.setColor(new Color(225, 110, 255, (int) (alpha * 170)));
                g.setStroke(new BasicStroke(4.5f * (1.0f - progress) + 1.2f));
                g.drawOval((int) (x - r1), (int) (y - r1 / 2), r1 * 2, r1);

                g.setColor(new Color(255, 215, 64, (int) (alpha * 190)));
                g.setStroke(new BasicStroke(2.5f * (1.0f - progress) + 1.0f));
                g.drawOval((int) (x - r2), (int) (y - r2 / 2), r2 * 2, r2);

                // แสงวาบจุดศูนย์กลาง
                int coreR = (int) (radius * 0.35 * (1.0f - progress));
                g.setColor(new Color(255, 255, 255, (int) (alpha * 240)));
                g.fillOval((int) (x - coreR), (int) (y - coreR / 2), coreR * 2, coreR);

                for (BurstParticle p : burstParticles) {
                    g.setColor(new Color(p.pColor.getRed(), p.pColor.getGreen(), p.pColor.getBlue(), (int) (alpha * 230)));
                    int s = (int) (p.size * (1.0f - progress * 0.4f));
                    g.fillOval((int) (p.px - s / 2), (int) (p.py - s / 2), Math.max(1, s), Math.max(1, s));
                }
            }
            case WAVE_BANNER -> {
                int bw = 460;
                int bh = 64;
                int bx = (int) x - bw / 2;
                int by = (int) y - bh / 2;

                // แอนิเมชันสไลด์เข้าและจางออก
                float bannerAlpha = (progress < 0.2f) ? (progress / 0.2f) : ((progress > 0.8f) ? ((1.0f - progress) / 0.2f) : 1.0f);
                int aInt = (int) (bannerAlpha * 230);

                g.setColor(new Color(20, 14, 30, aInt));
                g.fillRoundRect(bx, by, bw, bh, 14, 14);
                g.setColor(new Color(255, 179, 0, aInt));
                g.setStroke(new BasicStroke(2.0f));
                g.drawRoundRect(bx, by, bw, bh, 14, 14);

                g.setFont(FontHelper.bold(22));
                g.setColor(new Color(255, 220, 80, aInt));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(bannerText, bx + (bw - fm.stringWidth(bannerText)) / 2, by + 30);

                if (bannerSubtext != null && !bannerSubtext.isEmpty()) {
                    g.setFont(FontHelper.plain(12));
                    g.setColor(new Color(255, 255, 255, aInt));
                    FontMetrics sfm = g.getFontMetrics();
                    g.drawString(bannerSubtext, bx + (bw - sfm.stringWidth(bannerSubtext)) / 2, by + 50);
                }
            }
        }
    }
}
