package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * หน้าจอเริ่มต้นของเกม (Main Menu)
 * รองรับการย่อ/ขยายเต็มจอ (Resolution-Independent Scaling 16:9)
 */
public class MainMenuPanel extends JPanel {
    private final GameWindow window;
    private boolean showingHowToPlay = false;

    // ปุ่มไม้เมนู (พิกัดเสมือน 1280x720)
    private final Rectangle startBtn = new Rectangle(500, 310, 280, 65);
    private final Rectangle howToBtn = new Rectangle(500, 395, 280, 65);

    // ละอองเวทมนตร์ลอยในพื้นหลัง
    private final List<MagicParticle> particles = new ArrayList<>();
    private final Random rand = new Random();
    private Timer animTimer;

    public MainMenuPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG_DARK);

        // สร้างอนุภาคประกายเวทมนตร์ 40 จุด
        for (int i = 0; i < 40; i++) {
            particles.add(new MagicParticle(rand.nextInt(Constants.WINDOW_WIDTH), rand.nextInt(Constants.WINDOW_HEIGHT)));
        }

        initMouseListener();

        // แอนิเมชันละอองเวทมนตร์ 60 FPS
        animTimer = new Timer(16, e -> {
            for (MagicParticle p : particles) {
                p.update();
            }
            repaint();
        });
        animTimer.start();
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                int mx = v.x;
                int my = v.y;

                if (showingHowToPlay) {
                    showingHowToPlay = false; // คลิกที่ใดก็ได้เพื่อปิดหน้าต่างวิธีเล่น
                    repaint();
                    return;
                }

                if (startBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    window.startNewGame();
                } else if (howToBtn.contains(mx, my)) {
                    SoundManager.getInstance().playGrab();
                    showingHowToPlay = true;
                    repaint();
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

        // พื้นหลัง Letterbox สีมืด
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

        // พื้นหลังท้องฟ้าค่ำคืนและดวงดาว
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(12, 10, 28), 0, h, new Color(35, 20, 48));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // วาดดวงจันทร์และหอคอยด้านข้าง
        g2.setColor(new Color(255, 245, 200, 200));
        g2.fillOval(w - 240, 40, 80, 80);
        g2.setColor(new Color(15, 12, 32));
        g2.fillOval(w - 260, 35, 75, 75);

        // วาดหอคอย
        BufferedImage tower = AssetLoader.getImage("tower");
        if (tower != null) {
            g2.drawImage(tower, w - 280, h - 350, 200, 300, null);
            g2.drawImage(tower, 60, h - 320, 180, 270, null);
        }

        // วาดอนุภาคประกายเวทมนตร์
        for (MagicParticle p : particles) {
            p.render(g2);
        }

        // หัวข้อหลักเกม "POTION PANIC"
        g2.setFont(com.potionpanic.util.FontHelper.bold(52));
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString("POTION PANIC", w / 2 - 200, 172);

        GradientPaint titleGrad = new GradientPaint(0, 120, new Color(255, 112, 67), 0, 180, Constants.COLOR_ACCENT_GOLD);
        g2.setPaint(titleGrad);
        g2.drawString("POTION PANIC", w / 2 - 204, 170);

        // ชื่อไทย "อลวนคนปรุงยา"
        g2.setFont(com.potionpanic.util.FontHelper.bold(24));
        g2.setColor(new Color(129, 212, 250));
        String sub = "( อลวนคนปรุงยา )";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, 215);

        // วาดปุ่มไม้ "เริ่มเกม" และ "วิธีเล่น"
        drawWoodenButton(g2, startBtn, "เริ่มเกม", new Color(110, 75, 45));
        drawWoodenButton(g2, howToBtn, "วิธีเล่น", new Color(90, 60, 35));

        // วาดหม้อต้มยาตรงกลางล่าง
        BufferedImage cImg = AssetLoader.getImage("cauldron");
        if (cImg != null) {
            g2.drawImage(cImg, w / 2 - 70, h - 180, 140, 140, null);
        }

        // วาดขวดยาประดับ 4 ขวด
        String[] flasks = {"potion_fire", "potion_ice", "potion_poison", "potion_lightning"};
        for (int i = 0; i < flasks.length; i++) {
            BufferedImage pImg = AssetLoader.getImage(flasks[i]);
            if (pImg != null) {
                g2.drawImage(pImg, w / 2 - 170 + i * 90, h - 90, 50, 50, null);
            }
        }

        // แสดงหน้าต่างวิธีเล่น
        if (showingHowToPlay) {
            RecipeBookDialog.render(g2, w, h);
        }

        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    private void drawWoodenButton(Graphics2D g, Rectangle rect, String text, Color baseColor) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(rect.x + 4, rect.y + 6, rect.width, rect.height, 18, 18);

        g.setColor(baseColor);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 18, 18);

        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 18, 18);

        g.setColor(new Color(230, 200, 120));
        g.fillOval(rect.x + 8, rect.y + 8, 8, 8);
        g.fillOval(rect.x + rect.width - 16, rect.y + 8, 8, 8);
        g.fillOval(rect.x + 8, rect.y + rect.height - 16, 8, 8);
        g.fillOval(rect.x + rect.width - 16, rect.y + rect.height - 16, 8, 8);

        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int ty = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g.setColor(new Color(40, 20, 10));
        g.drawString(text, tx + 1, ty + 2);
        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }

    private static class MagicParticle {
        double x, y;
        double vy;
        float alpha;
        Color color;

        public MagicParticle(double x, double y) {
            this.x = x;
            this.y = y;
            this.vy = -0.4 - Math.random() * 0.8;
            this.alpha = (float) (0.2 + Math.random() * 0.7);
            Color[] colors = {new Color(255, 193, 7), new Color(76, 175, 80), new Color(33, 150, 243), new Color(244, 67, 54)};
            this.color = colors[(int) (Math.random() * colors.length)];
        }

        public void update() {
            y += vy;
            if (y < -10) {
                y = Constants.WINDOW_HEIGHT + 10;
                x = Math.random() * Constants.WINDOW_WIDTH;
            }
        }

        public void render(Graphics2D g) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
            g.fillOval((int) x, (int) y, 4, 4);
        }
    }
}
