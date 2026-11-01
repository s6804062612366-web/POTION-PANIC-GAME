package com.potionpanic.ui;

import com.potionpanic.item.IngredientType;
import com.potionpanic.item.RecipeBook;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * หน้าต่างแสดงตำราสูตรยาและสารานุกรมมอนสเตอร์ (Visual Alchemy Grimoire & Bestiary)
 * ออกแบบเป็นสมุดเวทมนตร์โบราณ 2 หน้าเปิดคู่ (Double-Page Grimoire)
 * แสดงสูตรผสมยาด้วยสมการรูปภาพไอคอนจริง [รูป] + [รูป] + [รูป] = [รูปยา]
 * และสารานุกรมมอนสเตอร์ 5 ชนิดพร้อมรูปขวดยาจุดอ่อนจริง ไร้อีโมจิ 100%
 */
public class RecipeBookDialog {

    public static void render(Graphics2D g, int screenWidth, int screenHeight) {
        // ฉากทึบแสงด้านหลัง (Dim Background)
        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // แผ่นสมุดเวทมนตร์โบราณ (Grimoire Book)
        int bookW = 1060;
        int bookH = 620;
        int bookX = (screenWidth - bookW) / 2;
        int bookY = (screenHeight - bookH) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // สันปกหนังสีน้ำตาลเข้ม
        g2.setColor(new Color(45, 26, 16));
        g2.fillRoundRect(bookX - 10, bookY - 8, bookW + 20, bookH + 16, 26, 26);
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRoundRect(bookX - 10, bookY - 8, bookW + 20, bookH + 16, 26, 26);

        // หน้ากระดาษพับคู่ (Left & Right Pages)
        int pageW = (bookW - 36) / 2;
        int pageH = bookH - 28;
        int leftX = bookX + 14;
        int rightX = bookX + 22 + pageW;
        int pageY = bookY + 14;

        // วาดหน้าซ้ายและหน้าขวา
        drawParchmentPage(g2, leftX, pageY, pageW, pageH);
        drawParchmentPage(g2, rightX, pageY, pageW, pageH);

        // สันหนังสือตรงกลาง
        GradientPaint spineGrad = new GradientPaint(leftX + pageW, pageY, new Color(130, 95, 60), rightX, pageY, new Color(90, 60, 35));
        g2.setPaint(spineGrad);
        g2.fillRect(leftX + pageW - 4, pageY, 16, pageH);

        // ==================== หน้าซ้าย: ตำราสูตรยา 6 ชนิด ====================
        renderRecipesPage(g2, leftX + 16, pageY + 16, pageW - 32, pageH - 32);

        // ==================== หน้าขวา: สารานุกรมมอนสเตอร์ ====================
        renderBestiaryPage(g2, rightX + 16, pageY + 16, pageW - 32, pageH - 32);

        // ปุ่มปิด [X] ที่มุมขวาบน
        int closeX = bookX + bookW - 44;
        int closeY = bookY - 4;
        g2.setColor(new Color(185, 45, 45));
        g2.fillRoundRect(closeX, closeY, 32, 32, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(FontHelper.bold(18));
        g2.drawString("X", closeX + 10, closeY + 23);

        g2.dispose();
    }

    private static void drawParchmentPage(Graphics2D g, int x, int y, int w, int h) {
        GradientPaint grad = new GradientPaint(x, y, new Color(248, 240, 224), x + w, y + h, new Color(236, 222, 198));
        g.setPaint(grad);
        g.fillRoundRect(x, y, w, h, 14, 14);

        g.setColor(new Color(175, 145, 110));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 14, 14);

        // เส้นกรอบประดับด้านใน
        g.setColor(new Color(210, 185, 155, 120));
        g.drawRoundRect(x + 8, y + 8, w - 16, h - 16, 10, 10);
    }

    /**
     * วาดหน้าซ้าย: รายการสูตรยา 6 ชนิดด้วยสมการรูปภาพ
     */
    private static void renderRecipesPage(Graphics2D g, int x, int y, int w, int h) {
        // หัวข้อหน้าซ้าย
        g.setFont(FontHelper.bold(20));
        g.setColor(new Color(75, 38, 16));
        g.drawString("ตำราปรุงยาเวทมนตร์ (Grimoire)", x + 10, y + 24);

        g.setFont(FontHelper.plain(11));
        g.setColor(new Color(125, 75, 40));
        g.drawString("ใส่ของในหม้อครบ 3 ชิ้น แล้วกด [Spacebar] เพื่อต้มผสมยา", x + 10, y + 42);

        g.setColor(new Color(185, 155, 120));
        g.drawLine(x + 10, y + 50, x + w - 10, y + 50);

        // รายการสูตรยา 6 ชนิด (จัดระเบียบ 3 บรรทัดกระชับ ไม่ล้นขอบ 100%)
        record RecipeRow(IngredientType i1, IngredientType i2, IngredientType i3, String potSprite, String potName, String effect, String subEffect) {}
        List<RecipeRow> rows = List.of(
                new RecipeRow(IngredientType.RED_HERB, IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, "potion_fire", "ยาเพลิงกัลป์", "ระเบิดเพลิงหมู่ + ติดไฟเผาไหม้", "ชนะทาง: อสูรพฤกษา (ดาเมจ x2.5)"),
                new RecipeRow(IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB, "potion_ice", "ยาน้ำแข็ง", "แช่แข็งเยือกแข็ง + สโลว์ 50%", "ชนะทาง: โกเลมไฟ (ดาเมจ x2.5)"),
                new RecipeRow(IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM, IngredientType.RED_HERB, "potion_poison", "ยากรดพิษ", "กรดกัดเกราะหนา + บ่อพิษตกค้าง", "ชนะทาง: สัตว์ร้ายเกราะ (ดาเมจ x2.5)"),
                new RecipeRow(IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT, IngredientType.BLUE_CRYSTAL, "potion_lightning", "ยาสายฟ้า", "ดาเมจสายฟ้าสูง + ช็อตสตั๊นชะงัก", "ชนะทาง: อิมป์ว่องไว (ดาเมจ x2.5)"),
                new RecipeRow(IngredientType.TOXIC_MUSHROOM, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB, "potion_frost_venom", "ยาหมอกพายุพิษ", "ไอเย็นแช่แข็งและติดพิษคู่ 2 ธาตุ", "ชนะทาง: โกเลม & สัตว์ร้ายเกราะ"),
                new RecipeRow(null, null, null, "potion_explosive", "ยาระเบิดมหาเวท (ของแรร์!)", "เจาะเกราะทุกชนิด & ทุบบอส x3.0", "โอกาสสุ่มได้ 15-25% จากการปรุงทุกสูตร")
        );

        int rowStartY = y + 58;
        int rowH = 76;
        int iconSize = 34;

        for (int i = 0; i < rows.size(); i++) {
            RecipeRow r = rows.get(i);
            int ry = rowStartY + i * rowH;

            // พื้นหลังแถวบางๆ
            g.setColor(new Color(230, 215, 190, 140));
            g.fillRoundRect(x + 6, ry, w - 12, rowH - 6, 8, 8);

            if (r.i1 == null) {
                // วาดแถวของแรร์พิเศษ: สุ่มออกจากการปรุงยาทุกชนิด
                int cx = x + 12;
                int cy = ry + 8;
                g.setColor(new Color(215, 200, 175));
                g.fillRoundRect(cx, cy, 96, iconSize, 6, 6);
                g.setColor(new Color(130, 95, 60));
                g.drawRoundRect(cx, cy, 96, iconSize, 6, 6);
                g.setFont(FontHelper.bold(10));
                g.setColor(new Color(60, 25, 10));
                g.drawString("ปรุงยาใดๆ สำเร็จ", cx + 8, cy + 21);

                cx += 102;
                drawSign(g, cx, cy + iconSize / 2, "->");
                cx += 14;

                // ขวดยาผลลัพธ์
                BufferedImage pImg = AssetLoader.getImage(r.potSprite);
                drawItemBox(g, cx, cy, iconSize + 2, pImg);
                cx += iconSize + 12;

                // ชื่อยาและผลลัพธ์ 3 บรรทัดสวยงามไม่ล้น
                g.setFont(FontHelper.bold(13));
                g.setColor(new Color(185, 45, 20));
                g.drawString(r.potName, cx, ry + 19);

                g.setFont(FontHelper.plain(11));
                g.setColor(new Color(90, 40, 10));
                g.drawString(r.effect, cx, ry + 35);

                g.setFont(FontHelper.plain(10));
                g.setColor(new Color(175, 75, 20));
                g.drawString(r.subEffect, cx, ry + 49);
            } else {
                // วาดสมการรูปภาพ [Icon1] + [Icon2] + [Icon3] = [PotionIcon] (ระยะห่างกระชับลงตัว)
                int cx = x + 12;
                int cy = ry + 8;

                drawItemBox(g, cx, cy, iconSize, r.i1.getImage());
                cx += iconSize + 3;

                drawSign(g, cx, cy + iconSize / 2, "+");
                cx += 9;

                drawItemBox(g, cx, cy, iconSize, r.i2.getImage());
                cx += iconSize + 3;

                drawSign(g, cx, cy + iconSize / 2, "+");
                cx += 9;

                drawItemBox(g, cx, cy, iconSize, r.i3.getImage());
                cx += iconSize + 3;

                drawSign(g, cx, cy + iconSize / 2, "=");
                cx += 11;

                // ขวดยาผลลัพธ์
                BufferedImage pImg = AssetLoader.getImage(r.potSprite);
                drawItemBox(g, cx, cy, iconSize + 2, pImg);
                cx += iconSize + 12;

                // ชื่อยาและผลลัพธ์ 3 บรรทัดสวยงามไม่ล้น
                g.setFont(FontHelper.bold(13));
                g.setColor(new Color(60, 25, 10));
                g.drawString(r.potName, cx, ry + 19);

                g.setFont(FontHelper.plain(11));
                g.setColor(new Color(90, 40, 10));
                g.drawString(r.effect, cx, ry + 35);

                g.setFont(FontHelper.plain(10));
                g.setColor(new Color(125, 75, 25));
                g.drawString(r.subEffect, cx, ry + 49);
            }
        }
    }

    /**
     * วาดหน้าขวา: สารานุกรมมอนสเตอร์ 5 ชนิด และจุดอ่อน
     */
    private static void renderBestiaryPage(Graphics2D g, int x, int y, int w, int h) {
        // หัวข้อหน้าขวา
        g.setFont(FontHelper.bold(20));
        g.setColor(new Color(75, 38, 16));
        g.drawString("สารานุกรมมอนสเตอร์ (Bestiary)", x + 10, y + 24);

        g.setFont(FontHelper.plain(11));
        g.setColor(new Color(125, 75, 40));
        g.drawString("โจมตีตรงจุดอ่อนสร้างความเสียหายรุนแรง (CRIT x2.5 / บอส x3.0)", x + 10, y + 42);

        g.setColor(new Color(185, 155, 120));
        g.drawLine(x + 10, y + 50, x + w - 10, y + 50);

        record MonsterRow(String sprite, String name, String trait, String weakPotSprite, String weakName, String weakNote) {}
        List<MonsterRow> monsters = List.of(
                new MonsterRow("monster_plant_creeper", "อสูรพฤกษา (Plant Creeper)", "พืชดูดซับ เลือดปานกลาง เดินช้า", "potion_fire", "ยาเพลิงกัลป์", "แพ้ไฟ x2.5"),
                new MonsterRow("monster_fire_golem", "โกเลมไฟ (Fire Golem)", "อสูรลาวา พลังชีวิตสูง หมัดหนัก", "potion_ice", "ยาน้ำแข็ง", "แพ้น้ำแข็ง x2.5"),
                new MonsterRow("monster_armored_beast", "สัตว์ร้ายเกราะหิน (Armored Beast)", "เกราะศิลาหนาพิเศษ เลือดมหาศาล", "potion_poison", "ยากรดพิษ", "แพ้กรดพิษ x2.5"),
                new MonsterRow("monster_swift_imp", "อิมป์ว่องไว (Swift Imp)", "ปิศาจตัวจิ๋ว ความเร็วในการวิ่งสูงมาก", "potion_lightning", "ยาสายฟ้า", "แพ้สายฟ้า x2.5"),
                new MonsterRow("monster_chaos_boss", "ราชันย์อสูรเวท (Chaos Overlord)", "บอสใหญ่ สลับเกราะ 4 ธาตุตลอดเวลา", "potion_explosive", "ยาระเบิดมหาเวท", "ฉีกเกราะ x3.0!")
        );

        int rowStartY = y + 60;
        int rowH = 92;

        for (int i = 0; i < monsters.size(); i++) {
            MonsterRow m = monsters.get(i);
            int ry = rowStartY + i * rowH;

            // พื้นหลังการ์ดมอนสเตอร์
            g.setColor(new Color(230, 215, 190, 140));
            g.fillRoundRect(x + 6, ry, w - 12, rowH - 8, 8, 8);

            // รูปภาพมอนสเตอร์
            BufferedImage mImg = AssetLoader.getImage(m.sprite);
            int mSize = 58;
            g.setColor(new Color(245, 235, 215));
            g.fillRoundRect(x + 14, ry + 12, mSize, mSize, 6, 6);
            if (mImg != null) {
                g.drawImage(mImg, x + 14, ry + 12, mSize, mSize, null);
            }

            // ข้อมูลมอนสเตอร์
            int tx = x + 82;
            g.setFont(FontHelper.bold(14));
            g.setColor(new Color(55, 20, 10));
            g.drawString(m.name, tx, ry + 28);

            g.setFont(FontHelper.plain(11));
            g.setColor(new Color(105, 55, 20));
            g.drawString("พฤติกรรม: " + m.trait, tx, ry + 46);

            // กล่องจุดอ่อนขวา
            int wBoxW = 160;
            int wBoxX = x + w - wBoxW - 14;
            int wBoxY = ry + 12;
            int wBoxH = rowH - 32;

            g.setColor(new Color(240, 225, 200));
            g.fillRoundRect(wBoxX, wBoxY, wBoxW, wBoxH, 6, 6);
            g.setColor(new Color(190, 155, 110));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(wBoxX, wBoxY, wBoxW, wBoxH, 6, 6);

            BufferedImage pImg = AssetLoader.getImage(m.weakPotSprite);
            if (pImg != null) {
                g.drawImage(pImg, wBoxX + 6, wBoxY + 6, 42, 42, null);
            }

            g.setFont(FontHelper.bold(11));
            g.setColor(new Color(175, 45, 25));
            g.drawString(m.weakName, wBoxX + 54, wBoxY + 22);
            g.setFont(FontHelper.bold(10));
            g.setColor(new Color(40, 110, 40));
            g.drawString(m.weakNote, wBoxX + 54, wBoxY + 38);
        }
    }

    private static void drawItemBox(Graphics2D g, int x, int y, int size, BufferedImage img) {
        g.setColor(new Color(245, 235, 215));
        g.fillRoundRect(x, y, size, size, 6, 6);
        g.setColor(new Color(170, 140, 105));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, size, size, 6, 6);

        if (img != null) {
            g.drawImage(img, x + 2, y + 2, size - 4, size - 4, null);
        }
    }

    private static void drawSign(Graphics2D g, int cx, int cy, String text) {
        g.setFont(FontHelper.bold(15));
        g.setColor(new Color(125, 75, 35));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, cy + 5);
    }
}
