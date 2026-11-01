package com.potionpanic.util;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * ตัวเรนเดอร์งานศิลป์เวทมนตร์ด้วย Java Graphics2D (Handcrafted Procedural Vector Art)
 * ออกแบบในสไตล์ Classic Fantasy สะอาดตา คมชัดทุกความละเอียด ไร้อีโมจิ 100%
 */
public final class VectorArtRenderer {
    private VectorArtRenderer() {}

    /**
     * สร้าง BufferedImage ขนาด width x height สำหรับไอเทม/ตัวละครที่ระบุ
     */
    public static BufferedImage createProceduralImage(String name, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        switch (name) {
            // วัตถุดิบ 4 ชนิด
            case "red_herb" -> drawRedHerb(g2, width, height);
            case "blue_crystal" -> drawBlueCrystal(g2, width, height);
            case "toxic_mushroom" -> drawToxicMushroom(g2, width, height);
            case "thunder_root" -> drawThunderRoot(g2, width, height);

            // ขวดยา 7 ชนิด
            case "potion_fire" -> drawPotionBottle(g2, width, height, new Color(244, 67, 54), new Color(255, 179, 0), "FIRE", ElementType.FIRE);
            case "potion_ice" -> drawPotionBottle(g2, width, height, new Color(33, 150, 243), new Color(179, 229, 252), "ICE", ElementType.ICE);
            case "potion_poison" -> drawPotionBottle(g2, width, height, new Color(76, 175, 80), new Color(205, 220, 57), "ACID", ElementType.POISON);
            case "potion_lightning" -> drawPotionBottle(g2, width, height, new Color(255, 214, 0), new Color(255, 249, 196), "VOLT", ElementType.LIGHTNING);
            case "potion_frost_venom" -> drawPotionBottle(g2, width, height, new Color(0, 150, 136), new Color(128, 222, 234), "MIST", ElementType.POISON);
            case "potion_explosive" -> drawPotionBottle(g2, width, height, new Color(255, 87, 34), new Color(255, 235, 59), "BLAST", ElementType.UNIVERSAL);
            case "potion_failed" -> drawPotionBottle(g2, width, height, new Color(100, 95, 95), new Color(150, 140, 140), "DUD", ElementType.NONE);

            // มอนสเตอร์ 5 ชนิด
            case "monster_fire_golem" -> drawFireGolem(g2, width, height);
            case "monster_plant_creeper" -> drawPlantCreeper(g2, width, height);
            case "monster_armored_beast" -> drawArmoredBeast(g2, width, height);
            case "monster_swift_imp" -> drawSwiftImp(g2, width, height);
            case "monster_chaos_boss" -> drawChaosBoss(g2, width, height);

            // สิ่งก่อสร้างและอุปกรณ์
            case "cauldron" -> drawCauldron(g2, width, height);
            case "tower" -> drawTower(g2, width, height);

            default -> drawGenericEmblem(g2, width, height, name);
        }

        g2.dispose();
        return image;
    }

    // ==========================================
    // 1. วัตถุดิบเวทมนตร์ (INGREDIENTS)
    // ==========================================

    private static void drawRedHerb(Graphics2D g, int w, int h) {
        // ออร่าความร้อนเบาๆ
        RadialGradientPaint aura = new RadialGradientPaint(w * 0.5f, h * 0.5f, w * 0.45f,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{new Color(255, 110, 64, 90), new Color(255, 61, 0, 30), new Color(255, 61, 0, 0)});
        g.setPaint(aura);
        g.fillOval(4, 4, w - 8, h - 8);

        // ก้านสมุนไพร
        g.setColor(new Color(110, 38, 20));
        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D stem = new Path2D.Double();
        stem.moveTo(w * 0.5, h * 0.85);
        stem.curveTo(w * 0.48, h * 0.65, w * 0.52, h * 0.45, w * 0.5, h * 0.22);
        g.draw(stem);

        // ใบไม้ทรงเปลวไฟ 3 กลีบ
        drawFlameLeaf(g, w * 0.5, h * 0.22, 28, -Math.PI / 2);
        drawFlameLeaf(g, w * 0.42, h * 0.48, 22, -Math.PI * 0.75);
        drawFlameLeaf(g, w * 0.58, h * 0.48, 22, -Math.PI * 0.25);

        // ประกายเกสรทอง
        g.setColor(new Color(255, 235, 59));
        g.fillOval((int) (w * 0.5 - 4), (int) (h * 0.38 - 4), 8, 8);
        g.setColor(Color.WHITE);
        g.fillOval((int) (w * 0.5 - 2), (int) (h * 0.38 - 2), 4, 4);
    }

    private static void drawFlameLeaf(Graphics2D g, double cx, double cy, double length, double angle) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.rotate(angle);

        Path2D leaf = new Path2D.Double();
        leaf.moveTo(0, 0);
        leaf.curveTo(length * 0.4, -length * 0.35, length * 0.8, -length * 0.2, length, 0);
        leaf.curveTo(length * 0.8, length * 0.2, length * 0.4, length * 0.35, 0, 0);

        GradientPaint leafGrad = new GradientPaint(0, 0, new Color(255, 87, 34), (float) length, 0, new Color(255, 214, 0));
        g.setPaint(leafGrad);
        g.fill(leaf);

        g.setColor(new Color(191, 54, 12));
        g.setStroke(new BasicStroke(1.8f));
        g.draw(leaf);

        // เส้นกลางใบ
        g.setColor(new Color(255, 235, 59, 180));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(2, 0, (int) (length * 0.75), 0);

        g.setTransform(old);
    }

    private static void drawBlueCrystal(Graphics2D g, int w, int h) {
        // ออร่าไอเย็น
        RadialGradientPaint aura = new RadialGradientPaint(w * 0.5f, h * 0.5f, w * 0.45f,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{new Color(129, 212, 250, 110), new Color(33, 150, 243, 30), new Color(33, 150, 243, 0)});
        g.setPaint(aura);
        g.fillOval(4, 4, w - 8, h - 8);

        // ฐานหินรองผลึก
        g.setColor(new Color(55, 71, 79));
        g.fillRoundRect((int) (w * 0.28), (int) (h * 0.74), (int) (w * 0.44), (int) (h * 0.16), 8, 8);

        // ผลึกหลักยอดแหลม 3 มิติ
        int cx = w / 2;
        int topY = (int) (h * 0.14);
        int midY = (int) (h * 0.54);
        int botY = (int) (h * 0.78);

        // ซีกซ้าย (สว่างปานกลาง)
        Polygon leftFacet = new Polygon(
                new int[]{cx, (int) (w * 0.30), (int) (w * 0.35), cx},
                new int[]{topY, midY, botY, (int) (h * 0.72)}, 4);
        g.setPaint(new GradientPaint(cx, topY, new Color(79, 195, 247), (float) (w * 0.3), midY, new Color(2, 136, 209)));
        g.fill(leftFacet);

        // ซีกกลางขวา (โดนแสงเต็มๆ ไฮไลท์)
        Polygon centerFacet = new Polygon(
                new int[]{cx, cx, (int) (w * 0.70), (int) (w * 0.55)},
                new int[]{topY, (int) (h * 0.72), botY, midY}, 4);
        g.setPaint(new GradientPaint(cx, topY, new Color(225, 245, 254), (float) (w * 0.6), midY, new Color(3, 169, 244)));
        g.fill(centerFacet);

        // ซีกขวาข้าง (เงา)
        Polygon rightFacet = new Polygon(
                new int[]{cx, (int) (w * 0.70), (int) (w * 0.55)},
                new int[]{topY, botY, midY}, 3);
        g.setPaint(new GradientPaint(cx, topY, new Color(3, 155, 229), (float) (w * 0.7), midY, new Color(1, 87, 155)));
        g.fill(rightFacet);

        // ขอบผลึกคมกริบ
        g.setColor(new Color(255, 255, 255, 220));
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(leftFacet);
        g.draw(centerFacet);

        // แสงประกายกากบาทบนยอด
        drawSparkle(g, cx, topY + 6, 8, Color.WHITE);
    }

    private static void drawToxicMushroom(Graphics2D g, int w, int h) {
        // ออร่าหมอกพิษ
        RadialGradientPaint aura = new RadialGradientPaint(w * 0.5f, h * 0.5f, w * 0.45f,
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{new Color(139, 195, 74, 90), new Color(76, 175, 80, 25), new Color(76, 175, 80, 0)});
        g.setPaint(aura);
        g.fillOval(4, 4, w - 8, h - 8);

        // ก้านเห็ดโค้งมน
        Path2D stem = new Path2D.Double();
        stem.moveTo(w * 0.42, h * 0.5);
        stem.curveTo(w * 0.38, h * 0.7, w * 0.36, h * 0.82, w * 0.4, h * 0.88);
        stem.lineTo(w * 0.6, h * 0.88);
        stem.curveTo(w * 0.64, h * 0.82, w * 0.62, h * 0.7, w * 0.58, h * 0.5);
        stem.closePath();

        g.setPaint(new GradientPaint(0, (float) (h * 0.5), new Color(236, 239, 241), 0, (float) (h * 0.88), new Color(176, 190, 197)));
        g.fill(stem);
        g.setColor(new Color(90, 105, 110));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(stem);

        // หมวกเห็ดพิษ
        Path2D cap = new Path2D.Double();
        cap.moveTo(w * 0.16, h * 0.54);
        cap.curveTo(w * 0.18, h * 0.16, w * 0.82, h * 0.16, w * 0.84, h * 0.54);
        cap.curveTo(w * 0.65, h * 0.58, w * 0.35, h * 0.58, w * 0.16, h * 0.54);
        cap.closePath();

        GradientPaint capGrad = new GradientPaint(0, (float) (h * 0.18), new Color(106, 27, 154), 0, (float) (h * 0.56), new Color(46, 125, 50));
        g.setPaint(capGrad);
        g.fill(cap);

        g.setColor(new Color(27, 94, 32));
        g.setStroke(new BasicStroke(2.2f));
        g.draw(cap);

        // จุดสปอร์เรืองแสงสีเขียวนีออน
        g.setColor(new Color(118, 255, 3));
        g.fillOval((int) (w * 0.32), (int) (h * 0.28), 10, 10);
        g.fillOval((int) (w * 0.55), (int) (h * 0.24), 12, 12);
        g.fillOval((int) (w * 0.68), (int) (h * 0.38), 9, 9);
        g.fillOval((int) (w * 0.26), (int) (h * 0.42), 8, 8);

        g.setColor(Color.WHITE);
        g.fillOval((int) (w * 0.34), (int) (h * 0.30), 4, 4);
        g.fillOval((int) (w * 0.58), (int) (h * 0.26), 5, 5);
    }

    private static void drawThunderRoot(Graphics2D g, int w, int h) {
        // ออร่าสายฟ้า
        RadialGradientPaint aura = new RadialGradientPaint(w * 0.5f, h * 0.5f, w * 0.45f,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{new Color(255, 235, 59, 100), new Color(255, 193, 7, 25), new Color(255, 193, 7, 0)});
        g.setPaint(aura);
        g.fillOval(4, 4, w - 8, h - 8);

        // ลำรากไม้คดเคี้ยว
        g.setColor(new Color(93, 64, 55));
        g.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D root = new Path2D.Double();
        root.moveTo(w * 0.52, h * 0.15);
        root.lineTo(w * 0.44, h * 0.38);
        root.lineTo(w * 0.58, h * 0.56);
        root.lineTo(w * 0.46, h * 0.85);
        g.draw(root);

        // แขนงรากย่อย
        g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) (w * 0.44), (int) (h * 0.38), (int) (w * 0.28), (int) (h * 0.52));
        g.drawLine((int) (w * 0.58), (int) (h * 0.56), (int) (w * 0.72), (int) (h * 0.70));

        // ประกายสายฟ้าสีทองนีออนผ่าพาดลำต้น
        Path2D bolt = new Path2D.Double();
        bolt.moveTo(w * 0.54, h * 0.12);
        bolt.lineTo(w * 0.42, h * 0.40);
        bolt.lineTo(w * 0.62, h * 0.40);
        bolt.lineTo(w * 0.36, h * 0.88);
        bolt.lineTo(w * 0.50, h * 0.55);
        bolt.lineTo(w * 0.34, h * 0.55);
        bolt.closePath();

        g.setColor(new Color(255, 238, 88));
        g.fill(bolt);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(bolt);
    }

    // ==========================================
    // 2. ขวดยาเวทมนตร์ (POTION FLASKS)
    // ==========================================

    public static void drawPotionBottle(Graphics2D g, int w, int h, Color liquidMain, Color liquidGlow, String label, ElementType element) {
        int cx = w / 2;

        // เงาฐานขวด
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(cx - 26, (int) (h * 0.84), 52, 12);

        // รัศมีเรืองแสงรอบขวดแก้ว
        RadialGradientPaint glow = new RadialGradientPaint(cx, h * 0.55f, w * 0.44f,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{new Color(liquidMain.getRed(), liquidMain.getGreen(), liquidMain.getBlue(), 75),
                        new Color(liquidMain.getRed(), liquidMain.getGreen(), liquidMain.getBlue(), 20),
                        new Color(liquidMain.getRed(), liquidMain.getGreen(), liquidMain.getBlue(), 0)});
        g.setPaint(glow);
        g.fillOval(6, 6, w - 12, h - 12);

        // คอขวดแก้ว
        int neckW = 20;
        int neckH = 18;
        int neckX = cx - neckW / 2;
        int neckY = (int) (h * 0.16);

        // ตัวขวดแก้วทรงกลม
        int bodyDiam = (int) (w * 0.62);
        int bodyX = cx - bodyDiam / 2;
        int bodyY = (int) (h * 0.28);

        // 1. เติมเนื้อน้ำยาในขวด (Liquid Fill)
        g.setColor(liquidMain);
        g.fillOval(bodyX + 4, bodyY + 4, bodyDiam - 8, bodyDiam - 8);

        // การไล่เฉดสีประกายน้ำยาภายใน (Liquid Gradient Core)
        GradientPaint liquidGrad = new GradientPaint(
                bodyX, bodyY, liquidGlow,
                bodyX + bodyDiam, bodyY + bodyDiam, liquidMain.darker());
        g.setPaint(liquidGrad);
        g.fillOval(bodyX + 8, bodyY + 8, bodyDiam - 16, bodyDiam - 16);

        // สัญลักษณ์ธาตุเวกเตอร์ตรงกลางน้ำยา
        if (element != ElementType.NONE) {
            drawVectorElementSymbol(g, cx, bodyY + bodyDiam / 2, 14, element);
        }

        // ฟองอากาศลอยในน้ำยา
        g.setColor(new Color(255, 255, 255, 180));
        g.fillOval(cx - 14, bodyY + bodyDiam / 2 + 8, 7, 7);
        g.fillOval(cx + 8, bodyY + bodyDiam / 2 - 10, 5, 5);
        g.fillOval(cx - 6, bodyY + bodyDiam / 2 - 16, 4, 4);

        // 2. ผนังแก้วและไฮไลท์สะท้อนแสงโค้ง (Glass Shell & Glare)
        g.setColor(new Color(255, 255, 255, 110));
        g.fillRect(neckX, neckY, neckW, neckH);

        g.setColor(new Color(255, 255, 255, 210));
        g.setStroke(new BasicStroke(2.8f));
        g.drawOval(bodyX, bodyY, bodyDiam, bodyDiam);
        g.drawRect(neckX, neckY, neckW, neckH);

        // แสงสะท้อนทรงเสี้ยวพระจันทร์ที่ขอบแก้วซ้ายบน
        g.setColor(new Color(255, 255, 255, 190));
        g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(bodyX + 8, bodyY + 8, bodyDiam - 16, bodyDiam - 16, 100, 65);

        // 3. ฝาจุกไม้คอร์กและเชือกผูกปากขวด
        int corkW = neckW + 6;
        int corkH = 10;
        int corkX = cx - corkW / 2;
        int corkY = neckY - corkH + 2;

        g.setColor(new Color(141, 110, 99));
        g.fillRoundRect(corkX, corkY, corkW, corkH, 4, 4);
        g.setColor(new Color(93, 64, 55));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(corkX, corkY, corkW, corkH, 4, 4);

        // สายรัดปากขวดและห่วงโลหะ
        g.setColor(new Color(62, 39, 35));
        g.fillRect(neckX - 2, neckY + 4, neckW + 4, 5);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.drawOval(cx - 4, neckY + 9, 8, 8);
    }

    // ==========================================
    // 3. ตัวละครมอนสเตอร์ (MONSTERS)
    // ==========================================

    private static void drawFireGolem(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // เงาใต้เท้า
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(cx - 30, (int) (h * 0.82), 60, 14);

        // หินลำตัวโกเลม (ภูเขาไฟแกรนิต)
        g.setColor(new Color(62, 39, 35));
        g.fillRoundRect((int) (w * 0.20), (int) (h * 0.24), (int) (w * 0.60), (int) (h * 0.60), 24, 24);

        // รอยแตกของลาวาเรืองแสงสีส้ม-ทอง
        g.setColor(new Color(255, 112, 67));
        g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D lava = new Path2D.Double();
        lava.moveTo(w * 0.32, h * 0.35);
        lava.lineTo(w * 0.50, h * 0.45);
        lava.lineTo(w * 0.42, h * 0.65);
        lava.lineTo(w * 0.65, h * 0.72);
        g.draw(lava);

        // เส้นแกนลาวาเหลืองสว่าง
        g.setColor(new Color(255, 238, 88));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(lava);

        // เขาหิน 2 ข้างบนหัว
        Polygon hornL = new Polygon(
                new int[]{(int) (w * 0.28), (int) (w * 0.22), (int) (w * 0.36)},
                new int[]{(int) (h * 0.26), (int) (h * 0.10), (int) (h * 0.20)}, 3);
        Polygon hornR = new Polygon(
                new int[]{(int) (w * 0.72), (int) (w * 0.78), (int) (w * 0.64)},
                new int[]{(int) (h * 0.26), (int) (h * 0.10), (int) (h * 0.20)}, 3);
        g.setColor(new Color(46, 26, 23));
        g.fill(hornL);
        g.fill(hornR);

        // ดวงตาลาวาเรืองแสง
        g.setColor(new Color(255, 235, 59));
        g.fillOval((int) (w * 0.34), (int) (h * 0.38), 11, 8);
        g.fillOval((int) (w * 0.55), (int) (h * 0.38), 11, 8);
        g.setColor(Color.WHITE);
        g.fillOval((int) (w * 0.37), (int) (h * 0.40), 5, 4);
        g.fillOval((int) (w * 0.58), (int) (h * 0.40), 5, 4);

        // ขอบหินนูน
        g.setColor(new Color(110, 60, 45));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect((int) (w * 0.20), (int) (h * 0.24), (int) (w * 0.60), (int) (h * 0.60), 24, 24);
    }

    private static void drawPlantCreeper(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // เงาใต้เท้า
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(cx - 26, (int) (h * 0.82), 52, 12);

        // ลำตัวพุ่มไม้หนามสีเขียวเข้ม
        g.setColor(new Color(27, 94, 32));
        g.fillOval((int) (w * 0.22), (int) (h * 0.26), (int) (w * 0.56), (int) (h * 0.56));

        // ใบไม้ประดับบนหัว
        g.setColor(new Color(76, 175, 80));
        g.fillOval(cx - 16, (int) (h * 0.12), 16, 22);
        g.fillOval(cx, (int) (h * 0.12), 16, 22);

        // หนามแหลมข้างลำตัว
        g.setColor(new Color(139, 195, 74));
        Polygon thornL = new Polygon(new int[]{(int) (w * 0.24), (int) (w * 0.12), (int) (w * 0.26)},
                new int[]{(int) (h * 0.48), (int) (h * 0.52), (int) (h * 0.60)}, 3);
        Polygon thornR = new Polygon(new int[]{(int) (w * 0.76), (int) (w * 0.88), (int) (w * 0.74)},
                new int[]{(int) (h * 0.48), (int) (h * 0.52), (int) (h * 0.60)}, 3);
        g.fill(thornL);
        g.fill(thornR);

        // ตาแดงนักล่า
        g.setColor(new Color(244, 67, 54));
        g.fillOval((int) (w * 0.36), (int) (h * 0.42), 10, 10);
        g.fillOval((int) (w * 0.54), (int) (h * 0.42), 10, 10);
        g.setColor(new Color(255, 205, 210));
        g.fillOval((int) (w * 0.39), (int) (h * 0.44), 4, 4);
        g.fillOval((int) (w * 0.57), (int) (h * 0.44), 4, 4);

        // ปากหนามหยัก
        g.setColor(new Color(20, 60, 25));
        g.setStroke(new BasicStroke(2.0f));
        g.drawArc((int) (w * 0.38), (int) (h * 0.58), (int) (w * 0.24), 14, 0, -180);
    }

    private static void drawArmoredBeast(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // เงาใต้เท้า
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(cx - 30, (int) (h * 0.82), 60, 14);

        // ตัวสัตว์ร้าย
        g.setColor(new Color(55, 71, 79));
        g.fillRoundRect((int) (w * 0.20), (int) (h * 0.24), (int) (w * 0.60), (int) (h * 0.58), 16, 16);

        // แผ่นเกราะเหล็กซ้อน 3 ชั้น
        g.setColor(new Color(120, 144, 156));
        g.fillRoundRect((int) (w * 0.24), (int) (h * 0.28), (int) (w * 0.52), 14, 6, 6);
        g.fillRoundRect((int) (w * 0.22), (int) (h * 0.46), (int) (w * 0.56), 16, 6, 6);
        g.fillRoundRect((int) (w * 0.25), (int) (h * 0.65), (int) (w * 0.50), 14, 6, 6);

        // หมุดเกราะโลหะ
        g.setColor(new Color(236, 239, 241));
        g.fillOval((int) (w * 0.28), (int) (h * 0.32), 4, 4);
        g.fillOval((int) (w * 0.68), (int) (h * 0.32), 4, 4);
        g.fillOval((int) (w * 0.26), (int) (h * 0.51), 4, 4);
        g.fillOval((int) (w * 0.70), (int) (h * 0.51), 4, 4);

        // ตาฟ้าดุดัน
        g.setColor(new Color(0, 229, 255));
        g.fillOval((int) (w * 0.35), (int) (h * 0.37), 9, 6);
        g.fillOval((int) (w * 0.56), (int) (h * 0.37), 9, 6);

        // ขอบโครงเกราะ
        g.setColor(new Color(38, 50, 56));
        g.setStroke(new BasicStroke(2.2f));
        g.drawRoundRect((int) (w * 0.20), (int) (h * 0.24), (int) (w * 0.60), (int) (h * 0.58), 16, 16);
    }

    private static void drawSwiftImp(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // ปีกค้างคาว 2 ข้าง
        g.setColor(new Color(74, 20, 140));
        Polygon wingL = new Polygon(
                new int[]{(int) (w * 0.34), (int) (w * 0.08), (int) (w * 0.18), (int) (w * 0.28)},
                new int[]{(int) (h * 0.44), (int) (h * 0.30), (int) (h * 0.52), (int) (h * 0.56)}, 4);
        Polygon wingR = new Polygon(
                new int[]{(int) (w * 0.66), (int) (w * 0.92), (int) (w * 0.82), (int) (w * 0.72)},
                new int[]{(int) (h * 0.44), (int) (h * 0.30), (int) (h * 0.52), (int) (h * 0.56)}, 4);
        g.fill(wingL);
        g.fill(wingR);

        // ตัวอิมป์สีม่วงเข้ม
        g.setColor(new Color(123, 31, 162));
        g.fillOval((int) (w * 0.28), (int) (h * 0.30), (int) (w * 0.44), (int) (h * 0.48));

        // เขาปีศาจแดงเล็กๆ
        g.setColor(new Color(255, 64, 129));
        Polygon hornL = new Polygon(
                new int[]{(int) (w * 0.36), (int) (w * 0.28), (int) (w * 0.42)},
                new int[]{(int) (h * 0.32), (int) (h * 0.15), (int) (h * 0.26)}, 3);
        Polygon hornR = new Polygon(
                new int[]{(int) (w * 0.64), (int) (w * 0.72), (int) (w * 0.58)},
                new int[]{(int) (h * 0.32), (int) (h * 0.15), (int) (h * 0.26)}, 3);
        g.fill(hornL);
        g.fill(hornR);

        // ตาโตขี้เล่นสีเหลืองอำพัน
        g.setColor(new Color(255, 235, 59));
        g.fillOval((int) (w * 0.36), (int) (h * 0.42), 12, 12);
        g.fillOval((int) (w * 0.52), (int) (h * 0.42), 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval((int) (w * 0.41), (int) (h * 0.45), 5, 5);
        g.fillOval((int) (w * 0.54), (int) (h * 0.45), 5, 5);

        // รอยยิ้มเจ้าเล่ห์
        g.setColor(Color.WHITE);
        g.fillArc((int) (w * 0.44), (int) (h * 0.60), 12, 8, 0, -180);
    }

    private static void drawChaosBoss(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // วงแหวนเวทมนตร์หมุนวนด้านหลังบอส
        g.setColor(new Color(156, 39, 176, 70));
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{8, 6}, 0));
        g.drawOval((int) (w * 0.08), (int) (h * 0.08), (int) (w * 0.84), (int) (h * 0.84));

        // เงาฐานยักษ์
        g.setColor(new Color(0, 0, 0, 110));
        g.fillOval(cx - 40, (int) (h * 0.86), 80, 18);

        // ปีกเงาราชันย์ปีศาจ
        g.setColor(new Color(49, 27, 146));
        Polygon wingL = new Polygon(
                new int[]{(int) (w * 0.30), (int) (w * 0.04), (int) (w * 0.14), (int) (w * 0.26)},
                new int[]{(int) (h * 0.40), (int) (h * 0.18), (int) (h * 0.60), (int) (h * 0.65)}, 4);
        Polygon wingR = new Polygon(
                new int[]{(int) (w * 0.70), (int) (w * 0.96), (int) (w * 0.86), (int) (w * 0.74)},
                new int[]{(int) (h * 0.40), (int) (h * 0.18), (int) (h * 0.60), (int) (h * 0.65)}, 4);
        g.fill(wingL);
        g.fill(wingR);

        // ลำตัวบอสเกราะทมิฬ
        g.setColor(new Color(33, 33, 33));
        g.fillRoundRect((int) (w * 0.22), (int) (h * 0.26), (int) (w * 0.56), (int) (h * 0.60), 22, 22);

        // อกเกราะคริสตัลพลังงานเคออส (เปลี่ยนเฉดสีม่วงแดง)
        g.setPaint(new GradientPaint(cx, (float) (h * 0.35), new Color(255, 64, 129), cx, (float) (h * 0.70), new Color(106, 27, 154)));
        Polygon coreGem = new Polygon(
                new int[]{cx, (int) (w * 0.62), cx, (int) (w * 0.38)},
                new int[]{(int) (h * 0.42), (int) (h * 0.55), (int) (h * 0.70), (int) (h * 0.55)}, 4);
        g.fill(coreGem);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.8f));
        g.draw(coreGem);

        // มงกุฎเขาราชันย์ปีศาจสีทอง
        g.setColor(new Color(255, 179, 0));
        Polygon crown = new Polygon(
                new int[]{(int) (w * 0.28), (int) (w * 0.34), cx, (int) (w * 0.66), (int) (w * 0.72), (int) (w * 0.62), cx, (int) (w * 0.38)},
                new int[]{(int) (h * 0.28), (int) (h * 0.12), (int) (h * 0.20), (int) (h * 0.12), (int) (h * 0.28), (int) (h * 0.25), (int) (h * 0.25), (int) (h * 0.25)}, 8);
        g.fill(crown);

        // ดวงตาปีศาจเรืองแสงแดงฉาน
        g.setColor(new Color(255, 23, 68));
        g.fillOval((int) (w * 0.36), (int) (h * 0.34), 11, 7);
        g.fillOval((int) (w * 0.53), (int) (h * 0.34), 11, 7);

        // ขอบเส้นคม
        g.setColor(new Color(156, 39, 176));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect((int) (w * 0.22), (int) (h * 0.26), (int) (w * 0.56), (int) (h * 0.60), 22, 22);
    }

    // ==========================================
    // 4. สิ่งก่อสร้าง (CAULDRON & TOWER)
    // ==========================================

    private static void drawCauldron(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // กองถ่านไฟและเปลวไฟด้านล่าง
        g.setColor(new Color(40, 25, 20));
        g.fillRoundRect((int) (w * 0.18), (int) (h * 0.82), (int) (w * 0.64), (int) (h * 0.14), 10, 10);
        g.setColor(new Color(255, 112, 67));
        g.fillOval(cx - 24, (int) (h * 0.80), 16, 12);
        g.fillOval(cx + 8, (int) (h * 0.78), 18, 14);
        g.fillOval(cx - 8, (int) (h * 0.76), 16, 16);

        // ขาตั้งเหล็ก 3 ขา
        g.setColor(new Color(33, 33, 33));
        g.fillRect((int) (w * 0.22), (int) (h * 0.70), 14, (int) (h * 0.18));
        g.fillRect((int) (w * 0.66), (int) (h * 0.70), 14, (int) (h * 0.18));
        g.fillRect(cx - 7, (int) (h * 0.72), 14, (int) (h * 0.16));

        // ท้องหม้อโลหะเหล็กหล่อสีเข้ม
        g.setPaint(new GradientPaint(0, (float) (h * 0.25), new Color(66, 66, 66), 0, (float) (h * 0.80), new Color(33, 33, 33)));
        g.fillArc((int) (w * 0.12), (int) (h * 0.25), (int) (w * 0.76), (int) (h * 0.56), 180, 180);
        g.fillOval((int) (w * 0.12), (int) (h * 0.20), (int) (w * 0.76), (int) (h * 0.30));

        // ปากหม้อและขอบหนา
        g.setColor(new Color(97, 97, 97));
        g.setStroke(new BasicStroke(4.5f));
        g.drawOval((int) (w * 0.12), (int) (h * 0.20), (int) (w * 0.76), (int) (h * 0.30));

        // น้ำยาเวทมนตร์เดือดปุดๆ ในหม้อ
        g.setPaint(new GradientPaint(0, (float) (h * 0.22), new Color(102, 187, 106), 0, (float) (h * 0.40), new Color(46, 125, 50)));
        g.fillOval((int) (w * 0.16), (int) (h * 0.23), (int) (w * 0.68), (int) (h * 0.22));

        // ฟองอากาศเดือด
        g.setColor(new Color(200, 255, 200, 220));
        g.fillOval(cx - 20, (int) (h * 0.28), 12, 10);
        g.fillOval(cx + 12, (int) (h * 0.26), 14, 11);
        g.fillOval(cx - 4, (int) (h * 0.31), 9, 8);

        // หูจับหม้อโลหะ 2 ข้าง
        g.setColor(new Color(117, 117, 117));
        g.setStroke(new BasicStroke(3.5f));
        g.drawArc((int) (w * 0.06), (int) (h * 0.32), 16, 26, 90, 180);
        g.drawArc((int) (w * 0.83), (int) (h * 0.32), 16, 26, -90, 180);
    }

    private static void drawTower(Graphics2D g, int w, int h) {
        int cx = w / 2;

        // ตัวอาคารหอคอยหินทรงกระบอก
        int bodyW = (int) (w * 0.62);
        int bodyH = (int) (h * 0.64);
        int bodyX = cx - bodyW / 2;
        int bodyY = (int) (h * 0.32);

        g.setPaint(new GradientPaint(bodyX, 0, new Color(97, 85, 115), bodyX + bodyW, 0, new Color(55, 45, 75)));
        g.fillRect(bodyX, bodyY, bodyW, bodyH);

        // ลายก้อนหินโบราณ
        g.setColor(new Color(40, 32, 55));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRect(bodyX, bodyY, bodyW, bodyH);
        for (int y = bodyY + 25; y < bodyY + bodyH - 10; y += 30) {
            g.drawLine(bodyX, y, bodyX + bodyW, y);
        }

        // หน้าต่างโค้งส่องแสงสีส้มอบอุ่น
        g.setColor(new Color(255, 193, 7));
        g.fillRoundRect(cx - 16, bodyY + 35, 32, 45, 14, 14);
        g.setColor(new Color(255, 238, 88));
        g.fillRoundRect(cx - 12, bodyY + 39, 24, 37, 10, 10);
        g.setColor(new Color(60, 45, 30));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(cx - 16, bodyY + 35, 32, 45, 14, 14);
        g.drawLine(cx, bodyY + 35, cx, bodyY + 80);
        g.drawLine(cx - 16, bodyY + 55, cx + 16, bodyY + 55);

        // ประตูไม้โค้งมนด้านล่าง
        g.setColor(new Color(110, 75, 50));
        g.fillRoundRect(cx - 20, bodyY + bodyH - 55, 40, 55, 12, 12);
        g.setColor(new Color(60, 38, 22));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(cx - 20, bodyY + bodyH - 55, 40, 55, 12, 12);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.fillOval(cx + 8, bodyY + bodyH - 28, 5, 5);

        // หลังคาทรงกรวยยอดแหลมสีม่วงเวทมนตร์
        int roofW = bodyW + 28;
        int roofX = cx - roofW / 2;
        int roofY = bodyY - 4;
        Polygon roof = new Polygon(
                new int[]{roofX, cx, roofX + roofW},
                new int[]{roofY, (int) (h * 0.05), roofY}, 3);
        g.setPaint(new GradientPaint(roofX, 0, new Color(142, 68, 173), roofX + roofW, 0, new Color(74, 20, 140)));
        g.fill(roof);
        g.setColor(new Color(49, 27, 146));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(roof);

        // ดาวทองประดับยอดหอคอย
        drawSparkle(g, cx, (int) (h * 0.05) - 4, 10, Constants.COLOR_ACCENT_GOLD);
    }

    // ==========================================
    // 5. ตราสัญลักษณ์ธาตุและเวกเตอร์ผู้ช่วย (NO EMOJIS)
    // ==========================================

    public static void drawVectorElementSymbol(Graphics2D g, int cx, int cy, int radius, ElementType element) {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (element) {
            case FIRE -> {
                // เปลวไฟ 3 แฉก
                Path2D flame = new Path2D.Double();
                flame.moveTo(cx, cy - radius);
                flame.curveTo(cx + radius * 0.6, cy - radius * 0.2, cx + radius * 0.8, cy + radius * 0.6, cx, cy + radius);
                flame.curveTo(cx - radius * 0.8, cy + radius * 0.6, cx - radius * 0.6, cy - radius * 0.2, cx, cy - radius);
                g.setColor(new Color(255, 235, 59));
                g.fill(flame);
                g.setColor(new Color(255, 87, 34));
                g.draw(flame);
            }
            case ICE -> {
                // ผลึกดาวหิมะ 6 แฉก
                g.setColor(new Color(225, 245, 254));
                for (int i = 0; i < 3; i++) {
                    double rad = i * Math.PI / 3.0;
                    int dx = (int) (Math.cos(rad) * radius);
                    int dy = (int) (Math.sin(rad) * radius);
                    g.drawLine(cx - dx, cy - dy, cx + dx, cy + dy);
                }
                g.drawOval(cx - 3, cy - 3, 6, 6);
            }
            case POISON -> {
                // หยดกรดเดือด
                Path2D drop = new Path2D.Double();
                drop.moveTo(cx, cy - radius);
                drop.curveTo(cx + radius * 0.8, cy, cx + radius * 0.8, cy + radius * 0.8, cx, cy + radius);
                drop.curveTo(cx - radius * 0.8, cy + radius * 0.8, cx - radius * 0.8, cy, cx, cy - radius);
                g.setColor(new Color(118, 255, 3));
                g.fill(drop);
                g.setColor(new Color(46, 125, 50));
                g.draw(drop);
            }
            case LIGHTNING -> {
                // สายฟ้าซิกแซก
                Path2D bolt = new Path2D.Double();
                bolt.moveTo(cx + radius * 0.2, cy - radius);
                bolt.lineTo(cx - radius * 0.4, cy);
                bolt.lineTo(cx + radius * 0.3, cy);
                bolt.lineTo(cx - radius * 0.2, cy + radius);
                bolt.lineTo(cx + radius * 0.4, cy - radius * 0.1);
                bolt.lineTo(cx - radius * 0.1, cy - radius * 0.1);
                bolt.closePath();
                g.setColor(new Color(255, 238, 88));
                g.fill(bolt);
                g.setColor(new Color(245, 127, 23));
                g.draw(bolt);
            }
            case UNIVERSAL -> {
                // ระเบิดมหาเวท 8 แฉก
                g.setColor(new Color(255, 214, 0));
                drawSparkle(g, cx, cy, radius, new Color(255, 214, 0));
            }
            case NONE -> {
                g.drawOval(cx - radius / 2, cy - radius / 2, radius, radius);
            }
        }
    }

    public static void drawSparkle(Graphics2D g, int cx, int cy, int size, Color color) {
        g.setColor(color);
        int half = size / 2;
        Polygon p = new Polygon(
                new int[]{cx, cx + 3, cx + size, cx + 3, cx, cx - 3, cx - size, cx - 3},
                new int[]{cy - size, cy - 3, cy, cy + 3, cy + size, cy + 3, cy, cy - 3}, 8);
        g.fill(p);
    }

    private static void drawGenericEmblem(Graphics2D g, int w, int h, String name) {
        g.setColor(new Color(45, 35, 60));
        g.fillRoundRect(8, 8, w - 16, h - 16, 14, 14);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(8, 8, w - 16, h - 16, 14, 14);
        g.setFont(FontHelper.bold(11));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name, (w - fm.stringWidth(name)) / 2, h / 2 + 4);
    }
}

