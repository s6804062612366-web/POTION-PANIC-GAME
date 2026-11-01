package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.engine.StageConfig;
import com.potionpanic.entity.monster.*;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.item.IngredientType;
import com.potionpanic.item.RecipeBook;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * ป๊อปอัพสไลด์การ์ดสอนเล่นก่อนเริ่มด่าน (Pre-Stage Interactive Slide Briefing)
 * แสดงขึ้นอัตโนมัติก่อนเริ่มเวฟแรกของแต่ละด่าน
 * สไลด์การ์ด 2 หน้า:
 * - หน้าที่ 1: แนะนำมอนสเตอร์และขวดยาที่แพ้ทาง (Monster Briefing)
 * - หน้าที่ 2: สอนสูตรยาประจำด่านด้วยสมการภาพ (Visual Recipe Tutorial)
 */
public class StageIntroOverlay {
    private boolean active = true;
    private int currentPage = 1; // 1 = Monster Intel, 2 = Recipes
    private final StageConfig config;

    // ปุ่มควบคุม
    private final Rectangle prevBtn = new Rectangle();
    private final Rectangle nextBtn = new Rectangle();
    private final Rectangle startBtn = new Rectangle();
    private final Rectangle closeBtn = new Rectangle();

    // แอนิเมชันเปิดหน้าต่าง
    private double animProgress = 0.0;
    private final double animSpeed = 4.5;

    public StageIntroOverlay(StageConfig config) {
        this.config = config;
        this.active = true;
        this.currentPage = 1;
        this.animProgress = 0.0;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void update(double dt) {
        if (!active) return;
        if (animProgress < 1.0) {
            animProgress = Math.min(1.0, animProgress + dt * animSpeed);
        }
    }

    public boolean handleMouseClick(int mx, int my) {
        if (!active) return false;

        if (closeBtn.contains(mx, my)) {
            SoundManager.getInstance().playGrab();
            close();
            return true;
        }

        if (currentPage == 1) {
            if (nextBtn.contains(mx, my)) {
                SoundManager.getInstance().playGrab();
                currentPage = 2;
                return true;
            }
        } else if (currentPage == 2) {
            if (prevBtn.contains(mx, my)) {
                SoundManager.getInstance().playGrab();
                currentPage = 1;
                return true;
            }
            if (startBtn.contains(mx, my)) {
                SoundManager.getInstance().playBoil();
                close();
                return true;
            }
        }

        return true; // ป้องกันคลิกทะลุลงไปในเกม
    }

    public boolean handleKeyPress(int keyCode) {
        if (!active) return false;

        if (keyCode == java.awt.event.KeyEvent.VK_SPACE || keyCode == java.awt.event.KeyEvent.VK_ENTER) {
            if (currentPage == 1) {
                SoundManager.getInstance().playGrab();
                currentPage = 2;
            } else {
                SoundManager.getInstance().playBoil();
                close();
            }
            return true;
        } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT || keyCode == java.awt.event.KeyEvent.VK_D) {
            if (currentPage == 1) {
                SoundManager.getInstance().playGrab();
                currentPage = 2;
                return true;
            }
        } else if (keyCode == java.awt.event.KeyEvent.VK_LEFT || keyCode == java.awt.event.KeyEvent.VK_A) {
            if (currentPage == 2) {
                SoundManager.getInstance().playGrab();
                currentPage = 1;
                return true;
            }
        } else if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            close();
            return true;
        }

        return true;
    }

    public void close() {
        this.active = false;
    }

    public void render(Graphics2D g, int screenW, int screenH) {
        if (!active) return;

        // พื้นหลังมืดสนิท Dim 94% ป้องกันฉากหลังลอดขึ้นมารบกวน
        float alpha = (float) (0.94 * animProgress);
        g.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
        g.fillRect(0, 0, screenW, screenH);

        // คำนวณขนาดกรอบหน้าต่างหลัก (ขยายให้กว้างและสูงพอดี 1060 x 640)
        int modalW = 1060;
        int modalH = 640;
        int modalX = (screenW - modalW) / 2;
        int modalY = (screenH - modalH) / 2;

        // แอนิเมชันเปิดแบบ Pop-in
        double scale = 0.88 + 0.12 * Math.sin(animProgress * Math.PI / 2.0);
        int drawW = (int) (modalW * scale);
        int drawH = (int) (modalH * scale);
        int drawX = modalX + (modalW - drawW) / 2;
        int drawY = modalY + (modalH - drawH) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // กรอบนอกสีทองโบราณ
        g2.setColor(new Color(25, 18, 32, 252));
        g2.fillRoundRect(drawX, drawY, drawW, drawH, 22, 22);
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRoundRect(drawX, drawY, drawW, drawH, 22, 22);

        // ขอบในลายฉลุ
        g2.setColor(new Color(110, 85, 55, 140));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(drawX + 8, drawY + 8, drawW - 16, drawH - 16, 16, 16);

        // หัวข้อหลักแยก 2 บรรทัด (ไม่ชนแท็บฝั่งขวา 100%)
        g2.setFont(FontHelper.bold(20));
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        g2.drawString(config.getTitle(), drawX + 32, drawY + 36);

        g2.setFont(FontHelper.plain(12));
        g2.setColor(new Color(185, 175, 215));
        g2.drawString("เตรียมความพร้อมก่อนเริ่มศึก — ศึกษาจุดอ่อนศัตรูและสูตรยาประจำด่าน", drawX + 32, drawY + 56);

        // แท็บแสดงหน้า (Page Indicator Pills) อยู่ฝั่งขวา
        int pagePillX = drawX + drawW - 275;
        int pagePillY = drawY + 24;
        drawPagePill(g2, pagePillX, pagePillY, "1. มอนสเตอร์", currentPage == 1);
        drawPagePill(g2, pagePillX + 115, pagePillY, "2. ตำราสูตรยา", currentPage == 2);

        // ปุ่มปิด [X] ที่มุมขวาบน
        closeBtn.setBounds(drawX + drawW - 44, drawY + 22, 28, 28);
        g2.setColor(new Color(180, 50, 50));
        g2.fillRoundRect(closeBtn.x, closeBtn.y, closeBtn.width, closeBtn.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(FontHelper.bold(16));
        g2.drawString("X", closeBtn.x + 8, closeBtn.y + 20);

        // เส้นคั่นหัวเรื่อง
        g2.setColor(new Color(95, 75, 120));
        g2.drawLine(drawX + 30, drawY + 70, drawX + drawW - 30, drawY + 70);

        // แสดงเนื้อหาตามหน้าที่เลือก
        if (currentPage == 1) {
            renderMonsterIntelPage(g2, drawX, drawY, drawW, drawH);
        } else {
            renderRecipeTutorialPage(g2, drawX, drawY, drawW, drawH);
        }

        // วาดปุ่มนำทางด้านล่าง
        renderBottomNavButtons(g2, drawX, drawY, drawW, drawH);

        g2.dispose();
    }

    private void drawPagePill(Graphics2D g, int x, int y, String label, boolean active) {
        int w = 102;
        int h = 26;
        if (active) {
            g.setColor(new Color(255, 179, 0, 180));
            g.fillRoundRect(x, y, w, h, 12, 12);
            g.setColor(Color.WHITE);
            g.setFont(FontHelper.bold(11));
        } else {
            g.setColor(new Color(45, 35, 55, 180));
            g.fillRoundRect(x, y, w, h, 12, 12);
            g.setColor(new Color(160, 150, 175));
            g.setFont(FontHelper.plain(11));
        }
        g.drawRoundRect(x, y, w, h, 12, 12);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 17);
    }

    /**
     * หน้าที่ 1: แนะนำมอนสเตอร์และขวดยาที่แพ้ทาง (Monster Briefing)
     */
    private void renderMonsterIntelPage(Graphics2D g, int x, int y, int w, int h) {
        g.setFont(FontHelper.bold(14));
        g.setColor(new Color(220, 210, 240));
        g.drawString("ศัตรูที่กำลังจะบุกเข้ามาในด่านนี้ และจุดอ่อนธาตุ (Critical Damage x2.5 / บอส x3.0):", x + 35, y + 94);

        List<MonsterBriefingInfo> monsters = getMonstersForStage(config.getStageNumber());
        int count = monsters.size();
        int cardStartY = (count >= 5) ? (y + 104) : (y + 110);
        int cardH = (count >= 5) ? 72 : 92;
        int gapY = (count >= 5) ? 8 : 14;

        for (int i = 0; i < count; i++) {
            MonsterBriefingInfo info = monsters.get(i);
            int cy = cardStartY + i * (cardH + gapY);
            int cx = x + 35;
            int cw = w - 70;

            // กรอบการ์ดมอนสเตอร์
            g.setColor(new Color(36, 28, 50, 230));
            g.fillRoundRect(cx, cy, cw, cardH, 12, 12);
            g.setColor(new Color(85, 65, 115));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(cx, cy, cw, cardH, 12, 12);

            // รูปภาพมอนสเตอร์
            int spriteSize = (count >= 5) ? 56 : 74;
            int spriteY = cy + (cardH - spriteSize) / 2;
            BufferedImage mImg = AssetLoader.getImage(info.spriteName);
            if (mImg != null) {
                g.drawImage(mImg, cx + 12, spriteY, spriteSize, spriteSize, null);
            }

            // ชื่อและข้อมูลมอนสเตอร์
            int textX = cx + spriteSize + 22;
            g.setFont(FontHelper.bold(count >= 5 ? 15 : 16));
            g.setColor(Color.WHITE);
            g.drawString(info.name, textX, cy + (count >= 5 ? 24 : 28));

            g.setFont(FontHelper.plain(count >= 5 ? 12 : 13));
            g.setColor(new Color(190, 180, 210));
            g.drawString("ลักษณะ: " + info.trait, textX, cy + (count >= 5 ? 44 : 52));

            g.setColor(new Color(255, 190, 120));
            g.drawString("เคล็ดลับ: " + info.tip, textX, cy + (count >= 5 ? 62 : 72));

            // กล่องจุดอ่อนขวา: รูปขวดยาที่แพ้ทาง
            int weakBoxW = (count >= 5) ? 220 : 240;
            int weakBoxH = (count >= 5) ? 54 : (cardH - 24);
            int weakBoxX = cx + cw - weakBoxW - 12;
            int weakBoxY = cy + (cardH - weakBoxH) / 2;

            g.setColor(new Color(20, 14, 28, 210));
            g.fillRoundRect(weakBoxX, weakBoxY, weakBoxW, weakBoxH, 10, 10);
            g.setColor(Constants.COLOR_ACCENT_GOLD);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(weakBoxX, weakBoxY, weakBoxW, weakBoxH, 10, 10);

            // รูปขวดยาจุดอ่อน
            int pSize = (count >= 5) ? 44 : 54;
            BufferedImage pImg = AssetLoader.getImage(info.weakPotionSprite);
            if (pImg != null) {
                g.drawImage(pImg, weakBoxX + 8, weakBoxY + (weakBoxH - pSize) / 2, pSize, pSize, null);
            }

            g.setFont(FontHelper.bold(11));
            g.setColor(new Color(255, 220, 100));
            g.drawString("จุดอ่อนธาตุ (แพ้ทาง):", weakBoxX + pSize + 14, weakBoxY + (count >= 5 ? 20 : 23));

            g.setFont(FontHelper.bold(12));
            g.setColor(Color.WHITE);
            g.drawString(info.weakPotionName, weakBoxX + pSize + 14, weakBoxY + (count >= 5 ? 38 : 44));
        }
    }

    /**
     * หน้าที่ 2: สอนสูตรยาประจำด่านด้วยสมการภาพ (Visual Recipe Tutorial)
     */
    private void renderRecipeTutorialPage(Graphics2D g, int x, int y, int w, int h) {
        g.setFont(FontHelper.bold(14));
        g.setColor(new Color(220, 210, 240));
        g.drawString("สมการสูตรผสมยาประจำด่านนี้ (ดึงวัตถุดิบ 3 ชิ้นลงหม้อ แล้วกด Spacebar):", x + 35, y + 94);

        List<RecipeVisualTutorial> recipes = getRecipesForStage(config.getStageNumber());
        int count = recipes.size();

        if (count > 3) {
            // จัดเรียงแบบตาราง 2 คอลัมน์ (2-Column Grid) เมื่อมีมากกว่า 3 สูตร
            int colW = (w - 70 - 18) / 2;
            int rowH = 88;
            int gapY = 10;
            int startY = y + 104;

            for (int i = 0; i < count; i++) {
                RecipeVisualTutorial r = recipes.get(i);
                int col = i % 2;
                int row = i / 2;
                int rx = x + 35 + col * (colW + 18);
                int ry = startY + row * (rowH + gapY);

                // พื้นหลังแถวสูตร
                g.setColor(new Color(36, 28, 50, 230));
                g.fillRoundRect(rx, ry, colW, rowH, 12, 12);
                g.setColor(new Color(85, 65, 115));
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(rx, ry, colW, rowH, 12, 12);

                // ตรวจสอบว่าเป็นยาระเบิดมหาเวทแบบสุ่มออกหรือไม่
                if (r.ing1 == null) {
                    int imgSize = 36;
                    int itemX = rx + 12;
                    int itemY = ry + 10;

                    // ป้ายแสดง "ปรุงยาใดๆ สำเร็จ"
                    g.setColor(new Color(25, 18, 35));
                    g.fillRoundRect(itemX, itemY, 130, imgSize, 8, 8);
                    g.setColor(new Color(110, 85, 140));
                    g.setStroke(new BasicStroke(1.2f));
                    g.drawRoundRect(itemX, itemY, 130, imgSize, 8, 8);
                    g.setFont(FontHelper.bold(11));
                    g.setColor(Constants.COLOR_ACCENT_GOLD);
                    g.drawString("ปรุงยาใดๆ สำเร็จ", itemX + 16, itemY + 23);

                    itemX += 136;
                    drawSymbol(g, itemX + 8, itemY + imgSize / 2, "->");
                    itemX += 24;

                    // ขวดยาผลลัพธ์
                    BufferedImage potImg = AssetLoader.getImage(r.potionSprite);
                    g.setColor(new Color(25, 16, 36));
                    g.fillRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 8, 8);
                    g.setColor(new Color(255, 112, 67));
                    g.setStroke(new BasicStroke(1.8f));
                    g.drawRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 8, 8);
                    if (potImg != null) {
                        g.drawImage(potImg, itemX + 2, itemY + 2, imgSize, imgSize, null);
                    }
                    itemX += imgSize + 14;

                    // ข้อความชื่อยา
                    g.setFont(FontHelper.bold(13));
                    g.setColor(new Color(255, 160, 100));
                    g.drawString(r.potionName, itemX, ry + 24);

                    // ข้อความด้านล่าง
                    g.setFont(FontHelper.plain(11));
                    g.setColor(new Color(255, 215, 140));
                    g.drawString("โอกาส: " + r.effect, rx + 14, ry + 60);

                    g.setColor(new Color(175, 230, 180));
                    g.drawString("เป้าหมาย: ใช้จัดการ " + r.targetMonster, rx + 14, ry + 76);
                } else {
                    // วาดไอคอน 3 ชิ้น: [Ing1] + [Ing2] + [Ing3] -> [Potion]
                    int imgSize = 36;
                    int itemX = rx + 12;
                    int itemY = ry + 10;

                    drawIngredientBox(g, itemX, itemY, imgSize, r.ing1);
                    itemX += imgSize + 6;
                    drawSymbol(g, itemX, itemY + imgSize / 2, "+");
                    itemX += 14;

                    drawIngredientBox(g, itemX, itemY, imgSize, r.ing2);
                    itemX += imgSize + 6;
                    drawSymbol(g, itemX, itemY + imgSize / 2, "+");
                    itemX += 14;

                    drawIngredientBox(g, itemX, itemY, imgSize, r.ing3);
                    itemX += imgSize + 8;
                    drawSymbol(g, itemX, itemY + imgSize / 2, "->");
                    itemX += 20;

                    // ขวดยาผลลัพธ์
                    BufferedImage potImg = AssetLoader.getImage(r.potionSprite);
                    g.setColor(new Color(25, 16, 36));
                    g.fillRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 8, 8);
                    g.setColor(Constants.COLOR_ACCENT_GOLD);
                    g.setStroke(new BasicStroke(1.8f));
                    g.drawRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 8, 8);
                    if (potImg != null) {
                        g.drawImage(potImg, itemX + 2, itemY + 2, imgSize, imgSize, null);
                    }
                    itemX += imgSize + 16;

                    // ข้อความชื่อยา
                    g.setFont(FontHelper.bold(13));
                    g.setColor(Color.WHITE);
                    g.drawString(r.potionName, itemX, ry + 24);

                    // ข้อความด้านล่าง
                    g.setFont(FontHelper.plain(11));
                    g.setColor(new Color(255, 215, 140));
                    g.drawString("ผลลัพธ์: " + r.effect, rx + 14, ry + 60);

                    g.setColor(new Color(175, 230, 180));
                    g.drawString("เป้าหมาย: ใช้จัดการ " + r.targetMonster, rx + 14, ry + 76);
                }
            }
        } else {
            // ด่านที่มีสูตรไม่เกิน 3 ชนิด จัดแถวเดี่ยวโปร่งสบาย
            int startY = y + 110;
            int rowH = 92;
            int gapY = 16;
            int rw = w - 70;

            for (int i = 0; i < count; i++) {
                RecipeVisualTutorial r = recipes.get(i);
                int ry = startY + i * (rowH + gapY);
                int rx = x + 35;

                g.setColor(new Color(36, 28, 50, 230));
                g.fillRoundRect(rx, ry, rw, rowH, 12, 12);
                g.setColor(new Color(85, 65, 115));
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(rx, ry, rw, rowH, 12, 12);

                int imgSize = 52;
                int itemX = rx + 20;
                int itemY = ry + (rowH - imgSize) / 2;

                drawIngredientBox(g, itemX, itemY, imgSize, r.ing1);
                itemX += imgSize + 12;
                drawSymbol(g, itemX, itemY + imgSize / 2, "+");
                itemX += 24;

                drawIngredientBox(g, itemX, itemY, imgSize, r.ing2);
                itemX += imgSize + 12;
                drawSymbol(g, itemX, itemY + imgSize / 2, "+");
                itemX += 24;

                drawIngredientBox(g, itemX, itemY, imgSize, r.ing3);
                itemX += imgSize + 14;
                drawSymbol(g, itemX, itemY + imgSize / 2, "-->");
                itemX += 34;

                BufferedImage potImg = AssetLoader.getImage(r.potionSprite);
                g.setColor(new Color(25, 16, 36));
                g.fillRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 10, 10);
                g.setColor(Constants.COLOR_ACCENT_GOLD);
                g.setStroke(new BasicStroke(2.0f));
                g.drawRoundRect(itemX, itemY, imgSize + 4, imgSize + 4, 10, 10);
                if (potImg != null) {
                    g.drawImage(potImg, itemX + 2, itemY + 2, imgSize, imgSize, null);
                }
                itemX += imgSize + 22;

                g.setFont(FontHelper.bold(16));
                g.setColor(Color.WHITE);
                g.drawString(r.potionName, itemX, ry + 32);

                g.setFont(FontHelper.plain(12));
                g.setColor(new Color(255, 215, 140));
                g.drawString("ผลลัพธ์: " + r.effect, itemX, ry + 52);

                g.setColor(new Color(175, 230, 180));
                g.drawString("เป้าหมาย: ใช้จัดการ " + r.targetMonster, itemX, ry + 72);
            }
        }
    }

    private void drawIngredientBox(Graphics2D g, int x, int y, int size, IngredientType type) {
        g.setColor(new Color(20, 15, 30));
        g.fillRoundRect(x, y, size, size, 8, 8);
        g.setColor(new Color(90, 75, 120));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, size, size, 8, 8);

        BufferedImage img = type.getImage();
        if (img != null) {
            g.drawImage(img, x + 3, y + 3, size - 6, size - 6, null);
        }
    }

    private void drawSymbol(Graphics2D g, int cx, int cy, String text) {
        g.setFont(FontHelper.bold(18));
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, cy + 6);
    }

    private void renderBottomNavButtons(Graphics2D g, int x, int y, int w, int h) {
        int btnH = 46;
        int btnY = y + h - 60;

        if (currentPage == 1) {
            // ปุ่ม "ถัดไป: ดูสูตรยา ->" (กดคลิกหรือกด Space / D ก็ได้)
            int btnW = 220;
            nextBtn.setBounds(x + w - btnW - 35, btnY, btnW, btnH);
            drawStyledButton(g, nextBtn, "ถัดไป: ดูสูตรยา ->", new Color(175, 95, 25), true);
        } else if (currentPage == 2) {
            // ปุ่ม "<- ย้อนกลับ" (กดคลิกหรือกด A ก็ได้)
            int backW = 140;
            prevBtn.setBounds(x + 35, btnY, backW, btnH);
            drawStyledButton(g, prevBtn, "<- ย้อนกลับ", new Color(75, 60, 95), false);

            // ปุ่ม "เริ่มลุยเลย!" (กดคลิกหรือกด Space ได้ตามปกติ)
            int startW = 200;
            startBtn.setBounds(x + w - startW - 35, btnY, startW, btnH);
            drawStyledButton(g, startBtn, "เริ่มลุยเลย!", new Color(185, 55, 35), true);
        }
    }

    private void drawStyledButton(Graphics2D g, Rectangle r, String text, Color baseColor, boolean highlight) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(r.x + 3, r.y + 4, r.width, r.height, 12, 12);

        g.setColor(baseColor);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g.setColor(highlight ? Constants.COLOR_ACCENT_GOLD : new Color(150, 135, 175));
        g.setStroke(new BasicStroke(highlight ? 2.2f : 1.2f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g.setFont(FontHelper.bold(15));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2, r.y + 28);
    }

    // --- โครงสร้างข้อมูลแนะนำด่าน ---

    private record MonsterBriefingInfo(String name, String spriteName, String trait, String tip, String weakPotionName, String weakPotionSprite) {}
    private record RecipeVisualTutorial(IngredientType ing1, IngredientType ing2, IngredientType ing3, String potionName, String potionSprite, String effect, String targetMonster) {}

    private List<MonsterBriefingInfo> getMonstersForStage(int stage) {
        List<MonsterBriefingInfo> list = new ArrayList<>();
        list.add(new MonsterBriefingInfo("อสูรพฤกษา (Plant Creeper)", "monster_plant_creeper",
                "สัตว์อสูรพืช เดินช้าแต่มีเลือดปานกลาง", "ใช้ยาเพลิงกัลป์เผาไหม้แบบกลุ่ม (AOE)", "ยาเพลิงกัลป์ (Fire)", "potion_fire"));
        list.add(new MonsterBriefingInfo("โกเลมไฟ (Fire Golem)", "monster_fire_golem",
                "มนุษย์หินลาวา ทนทานและมีหมัดหนัก", "ใช้น้ำแข็งเพื่อดับความร้อนและสโลว์ 50%", "ยาน้ำแข็ง (Ice)", "potion_ice"));

        if (stage >= 2) {
            list.add(new MonsterBriefingInfo("สัตว์ร้ายเกราะหิน (Armored Beast)", "monster_armored_beast",
                    "สัตว์หุ้มเกราะหนาพิเศษ เลือดเยอะและเดินช้า", "ใช้ยากรดพิษละลายเกราะหนา และทิ้งบ่อกรด", "ยากรดพิษ (Acid/Poison)", "potion_poison"));
        }
        if (stage >= 3) {
            list.add(new MonsterBriefingInfo("อิมป์ว่องไว (Swift Imp)", "monster_swift_imp",
                    "ปิศาจตัวจิ๋วความเร็วสูงมาก มุ่งทำลายหอคอย", "ใช้ยาสายฟ้าฟาดหยุดชะงัก (Stun 1.0s) ทันที", "ยาสายฟ้า (Lightning)", "potion_lightning"));
            list.add(new MonsterBriefingInfo("ราชันย์อสูรเวท (Chaos Overlord)", "monster_chaos_boss",
                    "บอสใหญ่ประจำเกม สลับเกราะ 4 ธาตุตลอดเวลา", "โจมตีตรงธาตุเกราะ หรือใช้ยาระเบิดมหาเวท (RARE) ฉีกเกราะ 3 เท่า!", "ยาระเบิดมหาเวท (Universal)", "potion_explosive"));
        }
        return list;
    }

    private List<RecipeVisualTutorial> getRecipesForStage(int stage) {
        List<RecipeVisualTutorial> list = new ArrayList<>();
        list.add(new RecipeVisualTutorial(IngredientType.RED_HERB, IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL,
                "ยาเพลิงกัลป์ (Fire Potion)", "potion_fire", "ระเบิดไฟหมู่ + เผาไหม้ Burn ต่อเนื่อง", "อสูรพฤกษา"));
        list.add(new RecipeVisualTutorial(IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB,
                "ยาน้ำแข็ง (Ice Potion)", "potion_ice", "ไอเย็นเยือกแข็ง + สโลว์ลดความเร็ว 50%", "โกเลมไฟ"));

        if (stage >= 2) {
            list.add(new RecipeVisualTutorial(IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM, IngredientType.RED_HERB,
                    "ยากรดพิษ (Acid/Poison)", "potion_poison", "กัดกร่อนเกราะหนา + ทิ้งบ่อกรดบนพื้น", "สัตว์ร้ายเกราะหิน"));
            list.add(new RecipeVisualTutorial(IngredientType.TOXIC_MUSHROOM, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB,
                    "ยาหมอกพายุพิษ (Frost-Venom)", "potion_frost_venom", "สโลว์แช่แข็งและติดพิษกัดกร่อนพร้อมกัน", "กลุ่มมอนสเตอร์ผสม"));
        }
        if (stage >= 3) {
            list.add(new RecipeVisualTutorial(IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT, IngredientType.BLUE_CRYSTAL,
                    "ยาสายฟ้า (Lightning Potion)", "potion_lightning", "ดาเมจเฉียบพลัน + ช็อตชะงัก Stun 1.0s", "อิมป์ว่องไว"));
            list.add(new RecipeVisualTutorial(null, null, null,
                    "ยาระเบิดมหาเวท (RARE!)", "potion_explosive", "สุ่มแจ็กพอต 15-25% (คับขัน 25%)", "ราชันย์อสูรเวท & ทุกตัว"));
        }
        return list;
    }
}

