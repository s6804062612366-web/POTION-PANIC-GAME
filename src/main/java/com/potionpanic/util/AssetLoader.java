package com.potionpanic.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * จัดการโหลดรูปภาพจาก resources/images พร้อมระบบ Fallback วาดกราฟิกจำลองสวยงาม
 * ทำให้เกมสามารถแสดงผลได้อย่างสวยงามทันทีแม้ยังไม่มีไฟล์รูปภาพจริง
 */
public class AssetLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage getImage(String name) {
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }

        BufferedImage img = null;
        try {
            String path = "/images/" + name + ".png";
            InputStream is = AssetLoader.class.getResourceAsStream(path);
            if (is != null) {
                img = ImageIO.read(is);
            }
        } catch (Exception e) {
            // ไม่พบไฟล์หรือมีข้อผิดพลาด ใช้ภาพ Fallback แทน
        }

        if (img == null) {
            img = generateFallbackImage(name);
        }

        imageCache.put(name, img);
        return img;
    }

    /**
     * สร้างกราฟิกจำลอง (Procedural Graphic) ตามชื่อของไอเทม/ตัวละคร
     */
    private static BufferedImage generateFallbackImage(String name) {
        int width = 96;
        int height = 96;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (name) {
            case "red_herb" -> drawHerb(g2, width, height, new Color(244, 67, 54), "เพลิง");
            case "blue_crystal" -> drawCrystal(g2, width, height, new Color(33, 150, 243), "น้ำแข็ง");
            case "toxic_mushroom" -> drawMushroom(g2, width, height, new Color(139, 195, 74), "พิษ");
            case "thunder_root" -> drawRoot(g2, width, height, new Color(255, 235, 59), "สายฟ้า");

            case "potion_fire" -> drawPotionFlask(g2, width, height, new Color(244, 67, 54), "FIRE");
            case "potion_ice" -> drawPotionFlask(g2, width, height, new Color(33, 150, 243), "ICE");
            case "potion_poison" -> drawPotionFlask(g2, width, height, new Color(76, 175, 80), "ACID");
            case "potion_lightning" -> drawPotionFlask(g2, width, height, new Color(255, 235, 59), "VOLT");
            case "potion_explosive" -> drawPotionFlask(g2, width, height, new Color(255, 112, 67), "BOOM");
            case "potion_failed" -> drawPotionFlask(g2, width, height, new Color(120, 110, 100), "DUD");

            case "monster_fire_golem" -> drawGolem(g2, width, height, new Color(230, 81, 0), "โกเลมเพลิง");
            case "monster_plant_creeper" -> drawPlantMonster(g2, width, height, new Color(46, 125, 50), "อสูรพฤกษา");
            case "monster_armored_beast" -> drawArmoredMonster(g2, width, height, new Color(120, 144, 156), "สัตว์เกราะหิน");
            case "monster_swift_imp" -> drawImp(g2, width, height, new Color(156, 39, 176), "อิมป์ว่องไว");

            case "cauldron" -> drawCauldron(g2, 140, 140);
            case "tower" -> drawTower(g2, 160, 220);
            default -> drawGenericIcon(g2, width, height, name);
        }

        g2.dispose();
        return img;
    }

    private static void drawHerb(Graphics2D g, int w, int h, Color color, String label) {
        g.setPaint(new RadialGradientPaint(w / 2f, h / 2f, w / 2f,
                new float[]{0f, 1f}, new Color[]{color.brighter(), new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}));
        g.fillOval(4, 4, w - 8, h - 8);

        g.setColor(color.darker());
        g.fillRoundRect(w / 2 - 4, h / 2 - 10, 8, 30, 4, 4);

        g.setColor(color);
        g.fillOval(w / 2 - 24, h / 2 - 20, 24, 20);
        g.fillOval(w / 2, h / 2 - 28, 24, 20);
        g.fillOval(w / 2 - 16, h / 2 - 32, 28, 22);

        g.setColor(Color.WHITE);
        g.setFont(FontHelper.bold(12));
        g.drawString(label, w / 2 - 14, h - 10);
    }

    private static void drawCrystal(Graphics2D g, int w, int h, Color color, String label) {
        Polygon poly = new Polygon();
        poly.addPoint(w / 2, 8);
        poly.addPoint(w - 18, h / 2 - 10);
        poly.addPoint(w - 28, h - 18);
        poly.addPoint(w / 2, h - 12);
        poly.addPoint(28, h - 18);
        poly.addPoint(18, h / 2 - 10);

        g.setColor(color.brighter());
        g.fill(poly);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(poly);

        g.setColor(Color.WHITE);
        g.setFont(FontHelper.bold(12));
        g.drawString(label, w / 2 - 18, h - 4);
    }

    private static void drawMushroom(Graphics2D g, int w, int h, Color color, String label) {
        // ก้านเห็ด
        g.setColor(new Color(230, 220, 190));
        g.fillRoundRect(w / 2 - 12, h / 2, 24, 30, 8, 8);

        // หมวกเห็ด
        g.setColor(color);
        g.fillArc(12, 12, w - 24, 55, 0, 180);

        // จุดบนเห็ด
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(26, 26, 12, 12);
        g.fillOval(w - 38, 28, 10, 10);
        g.fillOval(w / 2 - 6, 18, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(FontHelper.bold(12));
        g.drawString(label, w / 2 - 10, h - 4);
    }

    private static void drawRoot(Graphics2D g, int w, int h, Color color, String label) {
        g.setColor(new Color(130, 90, 45));
        g.fillRoundRect(w / 2 - 10, 12, 20, 56, 8, 8);

        // ประกายสายฟ้า
        g.setColor(color);
        Polygon bolt = new Polygon();
        bolt.addPoint(w / 2 + 8, 10);
        bolt.addPoint(w / 2 - 4, 32);
        bolt.addPoint(w / 2 + 10, 32);
        bolt.addPoint(w / 2 - 8, 58);
        bolt.addPoint(w / 2 + 2, 38);
        bolt.addPoint(w / 2 - 8, 38);
        g.fill(bolt);

        g.setColor(Color.WHITE);
        g.setFont(FontHelper.bold(12));
        g.drawString(label, w / 2 - 18, h - 8);
    }

    public static void drawPotionFlask(Graphics2D g, int w, int h, Color liquidColor, String label) {
        // คอขวด
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRoundRect(w / 2 - 10, 8, 20, 20, 4, 4);

        // ฝาจุกไม้คอร์ก
        g.setColor(new Color(160, 110, 60));
        g.fillRoundRect(w / 2 - 12, 4, 24, 8, 3, 3);

        // ตัวขวดแก้วกลม
        int bodySize = 58;
        int bx = (w - bodySize) / 2;
        int by = 26;

        // น้ำยาข้างในขวด
        g.setColor(liquidColor);
        g.fillOval(bx + 4, by + 4, bodySize - 8, bodySize - 8);

        // ฟองอากาศประกายในน้ำยา
        g.setColor(new Color(255, 255, 255, 160));
        g.fillOval(bx + 14, by + 16, 8, 8);
        g.fillOval(bx + 26, by + 28, 5, 5);

        // เปลือกแก้วใสและแสงสะท้อน
        g.setColor(new Color(255, 255, 255, 220));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(bx, by, bodySize, bodySize);
        g.drawArc(bx + 8, by + 8, bodySize - 16, bodySize - 16, 90, 70);

        // ฉลากชื่อย่อ
        g.setFont(FontHelper.bold(11));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int textX = (w - fm.stringWidth(label)) / 2;
        g.drawString(label, textX, by + bodySize / 2 + 4);
    }

    private static void drawGolem(Graphics2D g, int w, int h, Color bodyColor, String label) {
        g.setColor(bodyColor);
        g.fillRoundRect(12, 16, w - 24, h - 30, 20, 20);

        // ลาวาแตก
        g.setColor(new Color(255, 235, 59));
        g.fillRect(20, 36, w - 40, 6);
        g.fillRect(30, 52, w - 60, 5);

        // ตาเรืองแสง
        g.setColor(Color.YELLOW);
        g.fillOval(26, 26, 12, 8);
        g.fillOval(w - 38, 26, 12, 8);
    }

    private static void drawPlantMonster(Graphics2D g, int w, int h, Color color, String label) {
        g.setColor(color);
        g.fillOval(14, 18, w - 28, h - 30);

        // ใบไม้บนหัว
        g.setColor(new Color(129, 199, 132));
        g.fillOval(w / 2 - 16, 4, 16, 20);
        g.fillOval(w / 2, 4, 16, 20);

        // ตาแดงดุ
        g.setColor(Color.RED);
        g.fillOval(28, 36, 10, 10);
        g.fillOval(w - 38, 36, 10, 10);
    }

    private static void drawArmoredMonster(Graphics2D g, int w, int h, Color color, String label) {
        g.setColor(color);
        g.fillRoundRect(14, 14, w - 28, h - 28, 14, 14);

        // แผ่นเกราะเหล็ก
        g.setColor(new Color(84, 110, 122));
        g.drawRoundRect(14, 14, w - 28, h - 28, 14, 14);
        g.fillRect(24, 20, w - 48, 8);
        g.fillRect(20, 34, w - 40, 8);

        // ตาฟ้าดุดัน
        g.setColor(Color.CYAN);
        g.fillOval(30, 48, 8, 8);
        g.fillOval(w - 38, 48, 8, 8);
    }

    private static void drawImp(Graphics2D g, int w, int h, Color color, String label) {
        g.setColor(color);
        g.fillOval(20, 24, w - 40, h - 38);

        // เขาเล็กๆ 2 ข้าง
        g.setColor(new Color(255, 64, 129));
        Polygon horn1 = new Polygon(new int[]{24, 30, 20}, new int[]{24, 26, 8}, 3);
        Polygon horn2 = new Polygon(new int[]{w - 24, w - 30, w - 20}, new int[]{24, 26, 8}, 3);
        g.fill(horn1);
        g.fill(horn2);

        // ตาโตสีเหลือง
        g.setColor(Color.YELLOW);
        g.fillOval(28, 36, 14, 14);
        g.fillOval(w - 42, 36, 14, 14);
        g.setColor(Color.BLACK);
        g.fillOval(33, 40, 5, 5);
        g.fillOval(w - 37, 40, 5, 5);
    }

    public static void drawCauldron(Graphics2D g, int w, int h) {
        // หม้อต้มยาโลหะสีเข้ม
        g.setColor(new Color(40, 40, 50));
        g.fillArc(10, 25, w - 20, h - 40, 180, 180);
        g.fillOval(10, 20, w - 20, 35);

        // ขอบปากหม้อ
        g.setColor(new Color(70, 70, 85));
        g.setStroke(new BasicStroke(4));
        g.drawOval(10, 20, w - 20, 35);

        // น้ำยาเรืองแสงในหม้อ
        g.setColor(new Color(76, 175, 80, 220));
        g.fillOval(16, 26, w - 32, 25);

        // ฟองอากาศเดือดปุดๆ
        g.setColor(new Color(200, 255, 200, 200));
        g.fillOval(w / 2 - 18, 30, 10, 10);
        g.fillOval(w / 2 + 10, 32, 8, 8);
        g.fillOval(w / 2 - 4, 36, 6, 6);

        // ขาตั้งหม้อ 3 ขา
        g.setColor(new Color(30, 30, 35));
        g.fillRect(20, h - 25, 12, 22);
        g.fillRect(w - 32, h - 25, 12, 22);
        g.fillRect(w / 2 - 6, h - 20, 12, 18);
    }

    public static void drawTower(Graphics2D g, int w, int h) {
        // กำแพงหอคอย
        g.setColor(new Color(75, 68, 90));
        g.fillRect(15, 60, w - 30, h - 60);

        // หลังคาทรงกรวยยอดแหลม
        g.setColor(new Color(142, 68, 173));
        Polygon roof = new Polygon(new int[]{5, w / 2, w - 5}, new int[]{65, 5, 65}, 3);
        g.fill(roof);

        // หน้าต่างส่องแสงสีส้มอบอุ่น
        g.setColor(new Color(255, 193, 7));
        g.fillRoundRect(w / 2 - 18, 90, 36, 50, 12, 12);
        g.setColor(new Color(50, 40, 60));
        g.fillRect(w / 2 - 2, 90, 4, 50);
        g.fillRect(w / 2 - 18, 115, 36, 4);

        // ประตูไม้
        g.setColor(new Color(110, 75, 50));
        g.fillRoundRect(w / 2 - 22, h - 55, 44, 55, 14, 14);
    }

    private static void drawGenericIcon(Graphics2D g, int w, int h, String name) {
        g.setColor(new Color(100, 100, 140));
        g.fillRoundRect(6, 6, w - 12, h - 12, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(FontHelper.plain(10));
        g.drawString(name, 10, h / 2);
    }
}
