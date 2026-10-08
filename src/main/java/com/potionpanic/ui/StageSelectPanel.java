package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.engine.ProfileManager;
import com.potionpanic.engine.StageConfig;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * หน้าจอเลือกด่านและอัปเกรดเวทมนตร์ (Stage Select & Magic Upgrades)
 * ดีไซน์ UI สวยงาม คลีน เป็นมืออาชีพ ไร้อีโมจิ OS ใช้เวกเตอร์ไอคอนและฟอนต์เกมคมชัด
 * แก้ปัญหาตัวหนังสือล้นกรอบและทับซ้อน 100%
 */
public class StageSelectPanel extends JPanel {
    private final GameWindow window;
    private int selectedStage = 1;

    // ปุ่มเลือกด่าน (Stage Cards)
    private final Rectangle[] stageCards = new Rectangle[4];

    // ปุ่มอัปเกรดสกิล 4 สาย
    private final Rectangle[] upgradeBtns = new Rectangle[4];

    // ปุ่มเริ่มเล่น และปุ่มกลับเมนูหลัก
    private final Rectangle playStageBtn = new Rectangle(100, 615, 520, 52);
    private final Rectangle backMenuBtn = new Rectangle(60, 22, 140, 38);

    public StageSelectPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG_DARK);

        initLayoutCoordinates();
        initMouseListener();
    }

    private void initLayoutCoordinates() {
        // ตำแหน่งการ์ดเลือกด่าน 4 ด่าน (ฝั่งซ้าย)
        int cardX = 100;
        int startY = 95;
        int cardW = 520;
        int cardH = 112;
        int gapY = 16;

        for (int i = 0; i < 4; i++) {
            stageCards[i] = new Rectangle(cardX, startY + i * (cardH + gapY), cardW, cardH);
        }

        // ตำแหน่งปุ่มอัปเกรด 4 ชนิด (ฝั่งขวา)
        int upgX = 1000;
        int upgStartY = 195;
        int upgGapY = 114;
        for (int i = 0; i < 4; i++) {
            upgradeBtns[i] = new Rectangle(upgX, upgStartY + i * upgGapY, 140, 40);
        }
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                int mx = v.x;
                int my = v.y;
                ProfileManager profile = ProfileManager.getInstance();

                // 1. ปุ่มกลับเมนูหลัก
                if (backMenuBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.showScene("MENU");
                    return;
                }

                // 2. คลิกเลือกการ์ดด่าน
                for (int i = 0; i < 4; i++) {
                    int stageNum = i + 1;
                    if (stageCards[i].contains(mx, my)) {
                        if (profile.isStageUnlocked(stageNum)) {
                            selectedStage = stageNum;
                            SoundManager.getInstance().playGrab();
                            repaint();
                        } else {
                            SoundManager.getInstance().playHit();
                        }
                        return;
                    }
                }

                // 3. ปุ่มเริ่มเล่นด่านที่เลือก
                if (playStageBtn.contains(mx, my)) {
                    if (profile.isStageUnlocked(selectedStage)) {
                        SoundManager.getInstance().playGrab();
                        window.startStage(selectedStage);
                    } else {
                        SoundManager.getInstance().playHit();
                    }
                    return;
                }

                // 4. ปุ่มอัปเกรดสกิล 4 สาย
                ProfileManager.UpgradeType[] types = ProfileManager.UpgradeType.values();
                for (int i = 0; i < 4; i++) {
                    if (upgradeBtns[i].contains(mx, my)) {
                        ProfileManager.UpgradeType type = types[i];
                        if (profile.canUpgrade(type)) {
                            profile.buyUpgrade(type);
                            SoundManager.getInstance().playBoil();
                            repaint();
                        } else {
                            SoundManager.getInstance().playHit();
                        }
                        return;
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int screenW = getWidth();
        int screenH = getHeight();

        // พื้นหลัง Letterbox
        g2.setColor(new Color(10, 8, 20));
        g2.fillRect(0, 0, screenW, screenH);

        double scale = ScaleHelper.getScale(screenW, screenH);
        int ox = ScaleHelper.getOffsetX(screenW, screenH, scale);
        int oy = ScaleHelper.getOffsetY(screenW, screenH, scale);

        Shape origClip = g2.getClip();
        g2.clipRect(ox, oy, (int) Math.round(Constants.WINDOW_WIDTH * scale), (int) Math.round(Constants.WINDOW_HEIGHT * scale));

        AffineTransform origTx = g2.getTransform();
        g2.translate(ox, oy);
        g2.scale(scale, scale);

        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;

        // วาดฉากหลังค่ำคืนเวทมนตร์โทนลึก
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(15, 12, 28), 0, h, new Color(26, 17, 38));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // วาดภาพหอคอยประดับจางๆ มุมขวาล่าง
        BufferedImage tower = AssetLoader.getImage("tower");
        if (tower != null) {
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.drawImage(tower, w - 240, h - 380, 220, 340, null);
            g2.setComposite(oldComp);
        }

        // หัวข้อหลักกลางจอด้านบน
        g2.setFont(FontHelper.bold(24));
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        String headerTitle = "เลือกด่าน & อัปเกรดเวทมนตร์";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(headerTitle, (w - fm.stringWidth(headerTitle)) / 2, 45);

        // ปุ่มย้อนกลับ
        drawBackButton(g2);

        // ฝั่งซ้าย: การ์ดด่าน
        drawStageSelection(g2);

        // ฝั่งขวา: ร้านอัปเกรดสกิล
        drawUpgradeShop(g2);

        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    private void drawBackButton(Graphics2D g) {
        g.setColor(new Color(36, 28, 48, 230));
        g.fillRoundRect(backMenuBtn.x, backMenuBtn.y, backMenuBtn.width, backMenuBtn.height, 10, 10);
        g.setColor(new Color(110, 95, 140));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(backMenuBtn.x, backMenuBtn.y, backMenuBtn.width, backMenuBtn.height, 10, 10);

        // ไอคอนลูกศรย้อนกลับแบบเวกเตอร์
        g.setColor(Color.WHITE);
        int ax = backMenuBtn.x + 18;
        int ay = backMenuBtn.y + backMenuBtn.height / 2;
        int[] xPts = {ax + 8, ax, ax + 8};
        int[] yPts = {ay - 6, ay, ay + 6};
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawPolyline(xPts, yPts, 3);
        g.drawLine(ax, ay, ax + 14, ay);

        g.setFont(FontHelper.bold(14));
        g.setColor(Color.WHITE);
        g.drawString("เมนูหลัก", backMenuBtn.x + 48, backMenuBtn.y + 24);
    }

    private void drawStageSelection(Graphics2D g) {
        ProfileManager profile = ProfileManager.getInstance();

        // กรอบพื้นหลังโซนเลือกด่าน
        g.setColor(new Color(24, 18, 38, 200));
        g.fillRoundRect(80, 75, 560, 615, 18, 18);
        g.setColor(new Color(65, 52, 90));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(80, 75, 560, 615, 18, 18);

        // วาดการ์ดทั้ง 4 ด่าน
        for (int i = 0; i < 4; i++) {
            int stageNum = i + 1;
            StageConfig cfg = StageConfig.forStage(stageNum);
            Rectangle r = stageCards[i];
            boolean unlocked = profile.isStageUnlocked(stageNum);
            boolean isSelected = (selectedStage == stageNum);

            // พื้นหลังการ์ด
            if (!unlocked) {
                g.setColor(new Color(28, 22, 36, 180));
            } else if (isSelected) {
                g.setColor(new Color(55, 36, 85, 240));
            } else {
                g.setColor(new Color(36, 28, 54, 200));
            }
            g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            // ขอบการ์ด
            if (isSelected) {
                g.setColor(Constants.COLOR_ACCENT_GOLD);
                g.setStroke(new BasicStroke(2.5f));
            } else {
                g.setColor(unlocked ? new Color(90, 75, 120) : new Color(50, 42, 65));
                g.setStroke(new BasicStroke(1.2f));
            }
            g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            if (!unlocked) {
                // สถานะล็อค (ใช้เวกเตอร์แม่กุญแจ)
                drawVectorLock(g, r.x + 24, r.y + 36, 18, new Color(160, 145, 175));
                g.setFont(FontHelper.bold(16));
                g.setColor(new Color(160, 145, 175));
                g.drawString("ด่านที่ " + stageNum + " (ยังไม่ปลดล็อก)", r.x + 52, r.y + 50);

                g.setFont(FontHelper.plain(12));
                g.setColor(new Color(125, 115, 140));
                g.drawString("ต้องผ่านด่านก่อนหน้าก่อนเพื่อปลดล็อกการเล่น", r.x + 52, r.y + 75);
            } else {
                // ป้ายหมายเลขด่าน / ชื่อด่าน
                g.setFont(FontHelper.bold(15));
                g.setColor(isSelected ? Color.YELLOW : Color.WHITE);
                g.drawString(cfg.getTitle(), r.x + 18, r.y + 30);

                // ข้อมูลสรุปและวัตถุดิบ
                g.setFont(FontHelper.plain(12));
                g.setColor(new Color(215, 205, 230));
                g.drawString(cfg.getSubtitle(), r.x + 18, r.y + 54);

                g.setColor(new Color(175, 165, 195));
                g.drawString(cfg.getDescription(), r.x + 18, r.y + 76);

                // ดาวเวกเตอร์ 3 ดวง (หรือแท็ก Endless)
                int stars = profile.getStageStars(stageNum);
                int starStartX = r.x + r.width - 90;
                int starCenterY = r.y + 30;

                if (cfg.isEndless()) {
                    g.setColor(new Color(45, 30, 65));
                    g.fillRoundRect(starStartX - 28, starCenterY - 14, 105, 24, 8, 8);
                    g.setColor(new Color(255, 179, 0));
                    g.setStroke(new BasicStroke(1.2f));
                    g.drawRoundRect(starStartX - 28, starCenterY - 14, 105, 24, 8, 8);
                    g.setFont(FontHelper.bold(11));
                    g.drawString("โหมดไม่รู้จบ", starStartX - 10, starCenterY + 3);
                } else {
                    for (int s = 0; s < 3; s++) {
                        int sx = starStartX + s * 26;
                        boolean filled = s < stars;
                        drawVectorStar(g, sx, starCenterY, 9, 4,
                                filled ? new Color(255, 204, 0) : new Color(55, 48, 70),
                                filled ? new Color(255, 235, 100) : new Color(85, 75, 105));
                    }
                }
            }
        }

        // ปุ่มเล่นด่านที่เลือก (ขนาดพอดี ไม่ล้นกรอบ และจัดกึ่งกลาง)
        StageConfig currentSel = StageConfig.forStage(selectedStage);
        boolean canPlay = profile.isStageUnlocked(selectedStage);

        g.setColor(canPlay ? new Color(185, 115, 35) : new Color(55, 45, 68));
        g.fillRoundRect(playStageBtn.x, playStageBtn.y, playStageBtn.width, playStageBtn.height, 14, 14);
        g.setColor(canPlay ? Constants.COLOR_ACCENT_GOLD : new Color(85, 75, 100));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(playStageBtn.x, playStageBtn.y, playStageBtn.width, playStageBtn.height, 14, 14);

        g.setFont(FontHelper.bold(16));
        String playText = canPlay ? ("เริ่มเล่น " + currentSel.getShortTitle()) : "ด่านนี้ยังไม่ปลดล็อก";
        FontMetrics btnFm = g.getFontMetrics();
        int textW = btnFm.stringWidth(playText);

        if (canPlay) {
            int iconW = 12;
            int totalContentW = iconW + 10 + textW;
            int contentStartX = playStageBtn.x + (playStageBtn.width - totalContentW) / 2;
            int py = playStageBtn.y + playStageBtn.height / 2;

            int[] triX = {contentStartX, contentStartX + iconW, contentStartX};
            int[] triY = {py - 7, py, py + 7};
            g.setColor(Color.WHITE);
            g.fillPolygon(triX, triY, 3);

            g.setColor(Color.WHITE);
            g.drawString(playText, contentStartX + iconW + 10, playStageBtn.y + 32);
        } else {
            int textX = playStageBtn.x + (playStageBtn.width - textW) / 2;
            g.setColor(new Color(160, 150, 175));
            g.drawString(playText, textX, playStageBtn.y + 32);
        }
    }

    private void drawUpgradeShop(Graphics2D g) {
        ProfileManager profile = ProfileManager.getInstance();

        int shopX = 665;
        int shopY = 75;
        int shopW = 535;
        int shopH = 615;

        // กรอบแผงร้านค้าอัปเกรด
        g.setColor(new Color(24, 18, 38, 200));
        g.fillRoundRect(shopX, shopY, shopW, shopH, 18, 18);
        g.setColor(new Color(65, 52, 90));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(shopX, shopY, shopW, shopH, 18, 18);

        // หัวข้อร้านค้า (แยกบรรทัดชัดเจน ไม่ชนกล่องดาว)
        g.setFont(FontHelper.bold(20));
        g.setColor(Color.WHITE);
        g.drawString("อัปเกรดเวทมนตร์", shopX + 24, shopY + 36);
        g.setFont(FontHelper.bold(11));
        g.setColor(new Color(175, 155, 205));
        g.drawString("MAGIC UPGRADES", shopX + 24, shopY + 52);

        // ป้ายแสดงดาวคงเหลือ (จัดวางมุมขวาบน ห่างจากหัวข้ออย่างเหมาะสม)
        int starBoxW = 160;
        int starBoxH = 34;
        int starBoxX = shopX + shopW - starBoxW - 20;
        int starBoxY = shopY + 22;

        g.setColor(new Color(36, 26, 18, 230));
        g.fillRoundRect(starBoxX, starBoxY, starBoxW, starBoxH, 10, 10);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(starBoxX, starBoxY, starBoxW, starBoxH, 10, 10);

        // ดาวเวกเตอร์ในกล่อง
        drawVectorStar(g, starBoxX + 18, starBoxY + 17, 8, 4, Constants.COLOR_ACCENT_GOLD, Color.WHITE);
        g.setFont(FontHelper.bold(13));
        g.setColor(Color.WHITE);
        g.drawString("ดาวคงเหลือ: " + profile.getStarPoints() + " ดวง", starBoxX + 34, starBoxY + 22);

        // คำอธิบายร้านค้า
        g.setFont(FontHelper.plain(12));
        g.setColor(new Color(175, 165, 195));
        g.drawString("นำดาวสะสมที่ได้จากการผ่านด่านมาอัปเกรดทักษะเวทมนตร์ถาวร", shopX + 24, shopY + 76);

        // รายการอัปเกรด 4 ชนิด
        ProfileManager.UpgradeType[] types = ProfileManager.UpgradeType.values();
        int itemStartY = shopY + 95;
        int itemH = 102;
        int gapY = 14;

        for (int i = 0; i < 4; i++) {
            ProfileManager.UpgradeType type = types[i];
            int itemY = itemStartY + i * (itemH + gapY);
            int itemW = shopW - 44;
            int itemX = shopX + 22;

            g.setColor(new Color(34, 26, 48, 210));
            g.fillRoundRect(itemX, itemY, itemW, itemH, 12, 12);
            g.setColor(new Color(75, 60, 98));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(itemX, itemY, itemW, itemH, 12, 12);

            // ป้ายประเภทแบบเวกเตอร์คลีนๆ (Clean Category Pill)
            String category = switch (type) {
                case TOWER_HP -> "หอคอย";
                case CONVEYOR_SPEED -> "สายพาน";
                case POTION_RADIUS -> "ระเบิดยา";
                case CAULDRON_RECOVERY -> "หม้อต้ม";
            };
            Color catColor = switch (type) {
                case TOWER_HP -> new Color(64, 196, 255);
                case CONVEYOR_SPEED -> new Color(255, 215, 64);
                case POTION_RADIUS -> new Color(255, 110, 64);
                case CAULDRON_RECOVERY -> new Color(105, 240, 174);
            };

            int pillW = 60;
            int pillH = 20;
            g.setColor(new Color(catColor.getRed(), catColor.getGreen(), catColor.getBlue(), 40));
            g.fillRoundRect(itemX + 16, itemY + 14, pillW, pillH, 6, 6);
            g.setColor(catColor);
            g.setStroke(new BasicStroke(1.0f));
            g.drawRoundRect(itemX + 16, itemY + 14, pillW, pillH, 6, 6);
            g.setFont(FontHelper.bold(10));
            FontMetrics pfm = g.getFontMetrics();
            g.drawString(category, itemX + 16 + (pillW - pfm.stringWidth(category)) / 2, itemY + 28);

            // ชื่อสกิล
            g.setFont(FontHelper.bold(15));
            g.setColor(Color.WHITE);
            g.drawString(type.getName(), itemX + 86, itemY + 29);

            // คำอธิบาย
            g.setFont(FontHelper.plain(12));
            g.setColor(new Color(195, 185, 215));
            g.drawString(type.getDescription(), itemX + 16, itemY + 52);

            // หลอดแสดงเลเวล 3 ช่อง (Lv. 0/3)
            int level = profile.getUpgradeLevel(type);
            int barX = itemX + 16;
            int barY = itemY + 68;
            for (int pip = 0; pip < ProfileManager.MAX_UPGRADE_LEVEL; pip++) {
                int px = barX + pip * 36;
                if (pip < level) {
                    g.setColor(new Color(255, 204, 0));
                    g.fillRoundRect(px, barY, 28, 12, 4, 4);
                } else {
                    g.setColor(new Color(50, 42, 65));
                    g.fillRoundRect(px, barY, 28, 12, 4, 4);
                }
                g.setColor(new Color(95, 80, 118));
                g.drawRoundRect(px, barY, 28, 12, 4, 4);
            }

            g.setFont(FontHelper.bold(12));
            boolean isMax = (level >= ProfileManager.MAX_UPGRADE_LEVEL);
            g.setColor(isMax ? Color.YELLOW : new Color(175, 165, 195));
            String levelText = isMax ? "MAX" : ("Lv. " + level + "/3");
            g.drawString(levelText, barX + 118, barY + 11);

            // ปุ่มกดอัปเกรด
            Rectangle btn = upgradeBtns[i];
            boolean canBuy = profile.canUpgrade(type);

            if (isMax) {
                g.setColor(new Color(42, 36, 52));
            } else if (canBuy) {
                g.setColor(new Color(170, 95, 30));
            } else {
                g.setColor(new Color(55, 42, 50));
            }
            g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);

            if (canBuy && !isMax) {
                g.setColor(Constants.COLOR_ACCENT_GOLD);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
            }

            g.setFont(FontHelper.bold(12));
            String btnText;
            if (isMax) {
                btnText = "ระดับสูงสุด";
                g.setColor(new Color(150, 140, 160));
            } else if (canBuy) {
                btnText = "อัปเกรด (1 ดาว)";
                g.setColor(Color.WHITE);
            } else {
                btnText = "ดาวไม่พอ (1 ดาว)";
                g.setColor(new Color(180, 145, 145));
            }

            FontMetrics bfm = g.getFontMetrics();
            g.drawString(btnText, btn.x + (btn.width - bfm.stringWidth(btnText)) / 2, btn.y + 25);
        }
    }

    /**
     * วาดดาว 5 แฉกแบบเวกเตอร์คมชัด สวยงามโดยไม่ต้องพึ่งพา Emoji OS
     */
    public static void drawVectorStar(Graphics2D g, int cx, int cy, int outerR, int innerR, Color fill, Color outline) {
        int points = 5;
        int[] xPts = new int[points * 2];
        int[] yPts = new int[points * 2];
        double step = Math.PI / points;
        double start = -Math.PI / 2.0;

        for (int i = 0; i < points * 2; i++) {
            double r = (i % 2 == 0) ? outerR : innerR;
            double a = start + i * step;
            xPts[i] = (int) Math.round(cx + r * Math.cos(a));
            yPts[i] = (int) Math.round(cy + r * Math.sin(a));
        }

        if (fill != null) {
            g.setColor(fill);
            g.fillPolygon(xPts, yPts, points * 2);
        }
        if (outline != null) {
            g.setColor(outline);
            g.setStroke(new BasicStroke(1.2f));
            g.drawPolygon(xPts, yPts, points * 2);
        }
    }

    /**
     * วาดแม่กุญแจแบบเวกเตอร์สำหรับด่านที่ยังไม่ปลดล็อก
     */
    public static void drawVectorLock(Graphics2D g, int x, int y, int size, Color color) {
        int bodyW = size;
        int bodyH = (int) (size * 0.72);
        int bodyY = y + size - bodyH;

        // ตัวกุญแจ
        g.setColor(color);
        g.fillRoundRect(x, bodyY, bodyW, bodyH, 4, 4);

        // หูกุญแจ
        int shackleW = (int) (size * 0.65);
        int shackleH = (int) (size * 0.65);
        int shackleX = x + (bodyW - shackleW) / 2;
        g.setStroke(new BasicStroke(2.0f));
        g.drawArc(shackleX, y, shackleW, shackleH, 0, 180);

        // รูกุญแจ
        g.setColor(new Color(24, 18, 32));
        g.fillOval(x + bodyW / 2 - 2, bodyY + 4, 4, 4);
        g.fillRect(x + bodyW / 2 - 1, bodyY + 7, 2, 4);
    }
}
