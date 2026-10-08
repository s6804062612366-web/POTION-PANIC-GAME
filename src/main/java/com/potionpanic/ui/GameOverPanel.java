package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
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
 * หน้าจอเมื่อแพ้เกม (Game Over)
 * แสดงคะแนน, ด่านที่แพ้, และปุ่มลองใหม่ / เลือกด่านเพื่ออัปเกรดสกิล / กลับเมนู
 * รองรับ Virtual Scaling 16:9 เมื่อขยายเต็มจอ
 */
public class GameOverPanel extends JPanel {
    private final GameWindow window;
    private int finalScore = 0;
    private int currentStage = 1;

    private final Rectangle retryBtn = new Rectangle(480, 390, 320, 55);
    private final Rectangle stageSelectBtn = new Rectangle(480, 460, 320, 55);
    private final Rectangle menuBtn = new Rectangle(480, 530, 320, 55);

    public GameOverPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG_DARK);

        initMouseListener();
    }

    public void setFinalScore(int score, int stage) {
        this.finalScore = score;
        this.currentStage = stage;
    }

    public void setFinalScore(int score) {
        setFinalScore(score, 1);
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                int mx = v.x;
                int my = v.y;

                if (retryBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.startStage(currentStage);
                } else if (stageSelectBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.showScene("STAGE_SELECT");
                } else if (menuBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.showScene("MENU");
                }
            }
        });
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

        // พื้นหลังบรรยากาศหม่นหมองหลังพ่ายแพ้
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(25, 10, 15), 0, h, new Color(45, 15, 20));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // ภาพหอคอยด้านข้าง
        BufferedImage tower = AssetLoader.getImage("tower");
        if (tower != null) {
            g2.drawImage(tower, w - 280, h - 350, 200, 300, null);
            g2.drawImage(tower, 60, h - 320, 180, 270, null);
        }

        // หัวข้อ "GAME OVER" สีแดง
        g2.setFont(com.potionpanic.util.FontHelper.bold(50));
        String goTitle = "GAME OVER";
        FontMetrics gofm = g2.getFontMetrics();
        int goX = (w - gofm.stringWidth(goTitle)) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(goTitle, goX + 3, 172);

        g2.setColor(new Color(244, 67, 54));
        g2.drawString(goTitle, goX, 170);

        // คำอธิบาย
        StageConfig stageCfg = StageConfig.forStage(currentStage);
        g2.setFont(com.potionpanic.util.FontHelper.bold(20));
        g2.setColor(new Color(255, 200, 200));
        String desc = "หอคอยเวทมนตร์ถูกทำลายใน " + stageCfg.getTitle();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(desc, (w - fm.stringWidth(desc)) / 2, 220);

        // คำแนะนำสำหรับผู้เล่น
        g2.setFont(com.potionpanic.util.FontHelper.plain(14));
        g2.setColor(new Color(255, 220, 180));
        String tip = "ข้อแนะนำ: อัปเกรดพลังชีวิตหอคอย หรือความเร็วสายพานเพื่อรับมือกับฝูงมอนสเตอร์ได้ดีขึ้น";
        fm = g2.getFontMetrics();
        g2.drawString(tip, (w - fm.stringWidth(tip)) / 2, 260);

        // คะแนนรวม
        g2.setFont(com.potionpanic.util.FontHelper.bold(17));
        g2.setColor(Constants.COLOR_ACCENT_GOLD);
        String scoreText = "คะแนนที่ทำได้: " + finalScore + " แต้ม";
        fm = g2.getFontMetrics();
        g2.drawString(scoreText, (w - fm.stringWidth(scoreText)) / 2, 310);

        // วาดปุ่มไม้ "ลองใหม่อีกครั้ง", "เลือกด่าน & อัปเกรด", "กลับเมนูหลัก"
        drawWoodenButton(g2, retryBtn, "ลองใหม่อีกครั้ง", new Color(140, 60, 45));
        drawWoodenButton(g2, stageSelectBtn, "เลือกด่าน & อัปเกรด", new Color(75, 55, 95));
        drawWoodenButton(g2, menuBtn, "กลับหน้าหลัก", new Color(90, 60, 35));

        g2.setTransform(origTx);
        g2.setClip(origClip);
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
