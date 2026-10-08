package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.engine.ProfileManager;
import com.potionpanic.engine.StageConfig;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * หน้าจอเมื่อชนะเกม (Victory Panel)
 * แสดงคะแนน, จำนวนดาวที่ได้รับ (1-3 ดาว), และปุ่มไปยังด่านถัดไป / เลือกด่าน / เล่นซ้ำ
 * รองรับ Virtual Scaling 16:9
 */
public class VictoryPanel extends JPanel {
    private final GameWindow window;
    private int finalScore = 0;
    private int earnedStars = 1;
    private int currentStage = 1;

    // ปุ่มตัวเลือก
    private final Rectangle nextStageBtn = new Rectangle(480, 410, 320, 55);
    private final Rectangle replayBtn = new Rectangle(480, 480, 320, 55);
    private final Rectangle stageSelectBtn = new Rectangle(480, 550, 320, 55);

    public VictoryPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG_DARK);

        initMouseListener();
    }

    public void setResults(int score, int stars, int stage) {
        this.finalScore = score;
        this.earnedStars = Math.max(1, Math.min(3, stars));
        this.currentStage = stage;
    }

    public void setFinalScore(int score) {
        setResults(score, 1, 1);
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                int mx = v.x;
                int my = v.y;

                if (hasNextStage() && nextStageBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.startStage(currentStage + 1);
                } else if (replayBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.startStage(currentStage);
                } else if (stageSelectBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.showScene("STAGE_SELECT");
                }
            }
        });
    }

    private boolean hasNextStage() {
        return currentStage < 4;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int screenW = getWidth();
        int screenH = getHeight();

        // พื้นหลัง Letterbox
        g2.setColor(new Color(12, 10, 24));
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

        // พื้นหลังบรรยากาศฉลองชัยชนะสีเขียวมรกตและทอง
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(15, 30, 25), 0, h, new Color(25, 45, 35));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // ภาพหอคอยด้านข้าง
        BufferedImage tower = AssetLoader.getImage("tower");
        if (tower != null) {
            g2.drawImage(tower, w - 280, h - 350, 200, 300, null);
            g2.drawImage(tower, 60, h - 320, 180, 270, null);
        }

        // หัวข้อ "VICTORY" สีทองสุกปลั่ง
        g2.setFont(com.potionpanic.util.FontHelper.bold(44));
        String victoryTitle = "VICTORY - ผ่านด่านสำเร็จ!";
        FontMetrics vfm = g2.getFontMetrics();
        int vx = (w - vfm.stringWidth(victoryTitle)) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(victoryTitle, vx + 3, 133);

        GradientPaint goldGrad = new GradientPaint(0, 90, new Color(255, 235, 59), 0, 150, new Color(255, 179, 0));
        g2.setPaint(goldGrad);
        g2.drawString(victoryTitle, vx, 130);

        // ชื่อด่านที่เพิ่งเคลียร์
        StageConfig stageCfg = StageConfig.forStage(currentStage);
        g2.setFont(com.potionpanic.util.FontHelper.bold(20));
        g2.setColor(new Color(200, 255, 200));
        String stageTitle = stageCfg.getTitle();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(stageTitle, (w - fm.stringWidth(stageTitle)) / 2, 175);

        // วาดดาว 3 ดวงแบบเวกเตอร์
        drawStars(g2, w / 2, 230);

        // คำอธิบายระดับดาว
        String starComment = switch (earnedStars) {
            case 3 -> "สมบูรณ์แบบ! หอคอยไม่ได้รับความเสียหายแม้แต่น้อย (+3 ดาวสะสม)";
            case 2 -> "ยอดเยี่ยม! หอคอยคงเหลือพลังชีวิตมากกว่า 50% (+2 ดาวสะสม)";
            default -> "ผ่านด่านสำเร็จ! ปกป้องหอคอยจนหยดสุดท้าย (+1 ดาวสะสม)";
        };
        g2.setFont(com.potionpanic.util.FontHelper.bold(15));
        g2.setColor(Color.WHITE);
        fm = g2.getFontMetrics();
        g2.drawString(starComment, (w - fm.stringWidth(starComment)) / 2, 290);

        // แสดงคะแนน
        g2.setFont(com.potionpanic.util.FontHelper.bold(17));
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        String scoreText = "คะแนนแห่งชัยชนะ: " + finalScore + " แต้ม";
        fm = g2.getFontMetrics();
        g2.drawString(scoreText, (w - fm.stringWidth(scoreText)) / 2, 330);

        // แต้มดาวคงเหลือสำหรับอัปเกรด
        int points = ProfileManager.getInstance().getStarPoints();
        g2.setFont(com.potionpanic.util.FontHelper.plain(14));
        g2.setColor(new Color(255, 235, 150));
        String pointText = "(คุณมีดาวสำหรับอัปเกรดสะสมอยู่: " + points + " ดวง)";
        fm = g2.getFontMetrics();
        g2.drawString(pointText, (w - fm.stringWidth(pointText)) / 2, 365);

        // วาดปุ่มตัวเลือก
        if (hasNextStage()) {
            drawWoodenButton(g2, nextStageBtn, "ด่านถัดไป", new Color(160, 100, 40));
        }
        drawWoodenButton(g2, replayBtn, "เล่นด่านนี้ซ้ำ", new Color(110, 75, 45));
        drawWoodenButton(g2, stageSelectBtn, "เลือกด่าน & อัปเกรด", new Color(75, 55, 95));

        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    private void drawStars(Graphics2D g, int centerX, int centerY) {
        int starSpacing = 68;
        int startX = centerX - starSpacing;

        for (int i = 0; i < 3; i++) {
            int sx = startX + i * starSpacing;
            boolean filled = i < earnedStars;
            StageSelectPanel.drawVectorStar(g, sx, centerY, 24, 11,
                    filled ? new Color(255, 215, 0) : new Color(42, 50, 46),
                    filled ? Color.WHITE : new Color(75, 88, 80));
        }
    }

    private void drawWoodenButton(Graphics2D g, Rectangle rect, String text, Color baseColor) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(rect.x + 4, rect.y + 6, rect.width, rect.height, 16, 16);

        g.setColor(baseColor);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);

        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int ty = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(text, tx + 1, ty + 1);
        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }
}
