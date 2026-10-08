package com.potionpanic.ui;

import com.potionpanic.item.RecipeBook;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ElementType;

import java.awt.*;

/**
 * หน้าต่างแสดงสูตรยาและจุดอ่อนมอนสเตอร์ (Recipe Book Overlay)
 */
public class RecipeBookDialog {

    public static void render(Graphics2D g, int screenWidth, int screenHeight) {
        // ฉากทึบแสงด้านหลัง (Dim background)
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // แผ่นกระดาษโบราณ (Parchment Box)
        int boxW = 860;
        int boxH = 540;
        int boxX = (screenWidth - boxW) / 2;
        int boxY = (screenHeight - boxH) / 2;

        // ขอบและพื้นกระดาษ
        g.setColor(new Color(245, 235, 215));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g.setColor(new Color(133, 90, 61));
        g.setStroke(new BasicStroke(6));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // หัวข้อสมุดสูตรยา
        g.setColor(new Color(75, 45, 25));
        g.setFont(com.potionpanic.util.FontHelper.bold(24));
        g.drawString("สมุดสูตรยาเวทมนตร์ (Recipe Book)", boxX + 30, boxY + 45);

        g.setFont(com.potionpanic.util.FontHelper.plain(13));
        g.setColor(new Color(120, 80, 50));
        g.drawString("กด [Tab] หรือคลิกปุ่มปิด เพื่อกลับเข้าสู่เกม (ขณะเปิดหน้านี้เกมจะหยุดชั่วคราว)", boxX + 30, boxY + 70);

        // เส้นคั่น
        g.setColor(new Color(200, 180, 150));
        g.setStroke(new BasicStroke(2));
        g.drawLine(boxX + 30, boxY + 85, boxX + boxW - 30, boxY + 85);

        // ฝั่งซ้าย: ตารางสูตรยา
        int col1X = boxX + 35;
        int rowY = boxY + 115;
        g.setColor(new Color(60, 35, 20));
        g.setFont(com.potionpanic.util.FontHelper.bold(16));
        g.drawString("สูตรการผสมยา (ใส่ของในหม้อ 2 ชิ้นแล้วกด Spacebar):", col1X, rowY);
        rowY += 25;

        for (RecipeBook.RecipeEntry r : RecipeBook.getRecipes()) {
            g.setColor(new Color(40, 25, 15));
            g.setFont(com.potionpanic.util.FontHelper.bold(14));
            g.drawString("• " + r.getPotionName() + ":", col1X, rowY);

            g.setFont(com.potionpanic.util.FontHelper.plain(13));
            g.setColor(new Color(100, 50, 20));
            g.drawString("  ใช้วัตถุดิบ: " + r.getIngredientsDesc(), col1X + 10, rowY + 18);

            g.setColor(new Color(60, 90, 60));
            g.drawString("  ผลลัพธ์: " + r.getEffectDesc(), col1X + 10, rowY + 36);

            rowY += 56;
        }

        // ฝั่งขวา: ตารางจุดอ่อนมอนสเตอร์ (Elemental Weakness Matrix)
        int col2X = boxX + 460;
        int mRowY = boxY + 115;

        g.setColor(new Color(60, 35, 20));
        g.setFont(com.potionpanic.util.FontHelper.bold(16));
        g.drawString("จุดอ่อนมอนสเตอร์ (ดาเมจ Critical x2.5):", col2X, mRowY);
        mRowY += 30;

        String[][] weaknesses = {
                {"โกเลมไฟ (Fire Golem)", "แพ้ ยาน้ำแข็ง (Ice Potion)", "[ดาเมจ x2.5]"},
                {"อสูรพฤกษา (Plant Creeper)", "แพ้ ยาเพลิง (Fire Potion)", "[ดาเมจ x2.5]"},
                {"สัตว์ร้ายเกราะ (Armored Beast)", "แพ้ ยากรดพิษ (Poison)", "[ดาเมจ x2.5]"},
                {"อิมป์ว่องไว (Swift Imp)", "แพ้ ยาสายฟ้า (Stun 0.8s)", "[ดาเมจ x2.5]"}
        };

        for (String[] w : weaknesses) {
            g.setColor(new Color(50, 30, 20));
            g.setFont(com.potionpanic.util.FontHelper.bold(14));
            g.drawString(w[0], col2X, mRowY);

            g.setFont(com.potionpanic.util.FontHelper.plain(13));
            g.setColor(new Color(180, 40, 40));
            g.drawString("  " + w[1] + "  " + w[2], col2X + 10, mRowY + 20);

            mRowY += 50;
        }

        // ปุ่มปิด [X] ที่มุมขวาบน
        g.setColor(new Color(180, 50, 50));
        g.fillRoundRect(boxX + boxW - 50, boxY + 20, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("✕", boxX + boxW - 42, boxY + 43);
    }
}
