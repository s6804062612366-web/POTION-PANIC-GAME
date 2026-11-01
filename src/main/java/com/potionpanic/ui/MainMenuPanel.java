package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * หน้าจอเริ่มต้นของเกม (Main Menu)
 * ภาพปกแฟนตาซีเต็มจอ (bg_main_menu) คุณภาพสูง
 * พร้อมระบบเสียงเวทมนตร์ลึกลับ (Mystic Alchemist Chamber BGM) และไร้อีโมจิ 100%
 */
public class MainMenuPanel extends JPanel {
    private final GameWindow window;
    private boolean showingHowToPlay = false;

    // ปุ่มไม้เมนู (พิกัดเสมือน 1280x720)
    private final Rectangle startBtn = new Rectangle(490, 375, 300, 68);
    private final Rectangle howToBtn = new Rectangle(490, 465, 300, 68);
    private final Rectangle soundBtn = new Rectangle(Constants.WINDOW_WIDTH - 66, 22, 42, 42);

    private int mouseX = -1;
    private int mouseY = -1;
    private double animTime = 0.0;

    // ละอองเวทมนตร์ลอยในพื้นหลัง
    private final List<MagicParticle> particles = new ArrayList<>();
    private final Random rand = new Random();
    private final Timer animTimer;

    public MainMenuPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG_DARK);

        for (int i = 0; i < 45; i++) {
            particles.add(new MagicParticle(rand.nextInt(Constants.WINDOW_WIDTH), rand.nextInt(Constants.WINDOW_HEIGHT)));
        }

        initMouseListener();

        animTimer = new Timer(16, e -> {
            animTime += 0.016;
            for (MagicParticle p : particles) {
                p.update();
            }
            repaint();
        });
        animTimer.start();
    }

    public void startAnim() {
        if (!animTimer.isRunning()) animTimer.start();
        SoundManager.getInstance().playMenuBgm();
    }

    public void stopAnim() {
        if (animTimer.isRunning()) animTimer.stop();
    }

    private void initMouseListener() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private void updateCoords(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                mouseX = v.x;
                mouseY = v.y;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateCoords(e);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                updateCoords(e);
                int mx = mouseX;
                int my = mouseY;

                if (showingHowToPlay) {
                    showingHowToPlay = false;
                    SoundManager.getInstance().playGrab();
                    repaint();
                    return;
                }

                if (soundBtn.contains(mx, my)) {
                    SoundManager.getInstance().toggleSound();
                    SoundManager.getInstance().playGrab();
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
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
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

        // พื้นหลัง Letterbox สีมืด
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

        // 1. วาดภาพพื้นหลังห้องปรุงยาเวทมนตร์เต็มจอ (bg_main_menu)
        BufferedImage bg = AssetLoader.getImage("bg_main_menu");
        if (bg != null) {
            g2.drawImage(bg, 0, 0, w, h, null);
            // เพิ่มความลึกด้วยม่านเงาดำบางๆ
            g2.setColor(new Color(10, 8, 20, 85));
            g2.fillRect(0, 0, w, h);
        } else {
            GradientPaint bgGrad = new GradientPaint(0, 0, new Color(12, 10, 28), 0, h, new Color(35, 20, 48));
            g2.setPaint(bgGrad);
            g2.fillRect(0, 0, w, h);
        }

        // 2. วาดละอองเวทมนตร์ลอย
        for (MagicParticle p : particles) {
            p.render(g2);
        }

        // 3. วาดปุ่มปิด/เปิดเสียง (Sound Mute Button) ที่มุมขวาบน
        drawSoundButton(g2);

        // 4. ชื่อเกม "POTION PANIC" แบบตัวอักษรลอย 3D ไม่มีกรอบ พร้อมลูกเล่นเปลี่ยนสีเรียงตัวอักษรตามสียา
        g2.setFont(FontHelper.bold(68));
        FontMetrics mfm = g2.getFontMetrics();
        String mainTitle = "POTION PANIC";
        int totalTitleW = mfm.stringWidth(mainTitle);
        int curX = (w - totalTitleW) / 2;
        int titleY = 175;

        // วาดแต่ละตัวอักษรด้วยเงา 3D และสีขวดยาที่ไหลเวียนนุ่มนวล
        double colorShift = animTime * 1.8;
        int paletteLen = POTION_RAINBOW.length;

        for (int i = 0; i < mainTitle.length(); i++) {
            char ch = mainTitle.charAt(i);
            String chStr = String.valueOf(ch);
            int charW = mfm.charWidth(ch);

            if (ch != ' ') {
                // คำนวณสีประจำตัวอักษรแบบค่อยๆ เหลื่อมสีตามลำดับ
                double phase = (colorShift + i * 0.6) % paletteLen;
                if (phase < 0) phase += paletteLen;
                int idx1 = (int) phase;
                int idx2 = (idx1 + 1) % paletteLen;
                float blend = (float) (phase - idx1);

                Color c1 = POTION_RAINBOW[idx1];
                Color c2 = POTION_RAINBOW[idx2];
                int redVal = Math.min(255, Math.max(0, (int) (c1.getRed() * (1 - blend) + c2.getRed() * blend)));
                int greenVal = Math.min(255, Math.max(0, (int) (c1.getGreen() * (1 - blend) + c2.getGreen() * blend)));
                int blueVal = Math.min(255, Math.max(0, (int) (c1.getBlue() * (1 - blend) + c2.getBlue() * blend)));
                Color faceColor = new Color(redVal, greenVal, blueVal);
                Color depthColor = new Color(Math.max(10, redVal / 3), Math.max(10, greenVal / 3), Math.max(10, blueVal / 3));

                // ชั้นที่ 1: เงาลึกสีดำ (Deep Shadow) ให้ตัวหนังสือลอยเด่นออกมาจากพื้นหลัง
                g2.setColor(new Color(0, 0, 0, 230));
                g2.drawString(chStr, curX + 4, titleY + 6);

                // ชั้นที่ 2: ความหนา 3D (Extrusion depth)
                g2.setColor(depthColor);
                g2.drawString(chStr, curX + 2, titleY + 3);

                // ชั้นที่ 3: ออร่าเรืองแสงสีประจำยา (Magical Glow)
                g2.setColor(new Color(redVal, greenVal, blueVal, 70));
                g2.drawString(chStr, curX - 1, titleY - 1);
                g2.drawString(chStr, curX + 1, titleY - 1);

                // ชั้นที่ 4: หน้าตัดตัวอักษรสีสดใส (Floating Face)
                g2.setColor(faceColor);
                g2.drawString(chStr, curX, titleY);
            }
            curX += charW;
        }

        // ชื่อภาษาไทย "( อลวนคนปรุงยา )" สีทองนุ่มนวล
        g2.setFont(FontHelper.bold(22));
        String sub = "( อลวนคนปรุงยา )";
        FontMetrics sfm = g2.getFontMetrics();
        int sx = (w - sfm.stringWidth(sub)) / 2;
        int subY = 215;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(sub, sx + 2, subY + 2);
        g2.setColor(new Color(255, 225, 140));
        g2.drawString(sub, sx, subY);

        // 5. วาดปุ่มไม้ "เริ่มเกม" และ "ตำราวิธีเล่น" พร้อม Hover Animation
        boolean startHover = startBtn.contains(mouseX, mouseY);
        boolean howToHover = howToBtn.contains(mouseX, mouseY);

        drawHoverButton(g2, startBtn, "เริ่มเกม", new Color(175, 85, 25), startHover);
        drawHoverButton(g2, howToBtn, "ตำราวิธีเล่น & สารานุกรม", new Color(75, 50, 95), howToHover);

        // 6. แสดงหน้าต่างวิธีเล่นเต็มจอ
        if (showingHowToPlay) {
            RecipeBookDialog.render(g2, w, h);
        }

        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    // สีประจำขวดยาแต่ละประเภทสำหรับลูกเล่นเปลี่ยนสีตัวอักษร
    private static final Color[] POTION_RAINBOW = {
        new Color(255, 85, 85),    // ยาเพลิง (Fire Red)
        new Color(75, 195, 255),   // ยาน้ำแข็ง (Ice Blue)
        new Color(105, 240, 140),  // ยาพิษ (Poison Green)
        new Color(30, 245, 225),   // ยาเยือกแข็งพิษ (Frost-Venom Cyan)
        new Color(255, 225, 65),   // ยาสายฟ้า (Lightning Yellow)
        new Color(225, 110, 255)   // ยาระเบิดเวทมนตร์ (Explosive Purple)
    };

    private void drawSoundButton(Graphics2D g) {
        boolean soundOn = SoundManager.getInstance().isSoundEnabled();
        boolean hover = soundBtn.contains(mouseX, mouseY);

        // พื้นหลังปุ่มกลมมนเรียบหรู
        g.setColor(hover ? new Color(55, 40, 75, 240) : new Color(25, 18, 35, 210));
        g.fillRoundRect(soundBtn.x, soundBtn.y, soundBtn.width, soundBtn.height, 12, 12);
        g.setColor(hover ? Constants.COLOR_ACCENT_GOLD : new Color(130, 105, 165));
        g.setStroke(new BasicStroke(hover ? 2.0f : 1.4f));
        g.drawRoundRect(soundBtn.x, soundBtn.y, soundBtn.width, soundBtn.height, 12, 12);

        // ไอคอนลำโพงเวกเตอร์ (Vector Speaker Icon)
        int cx = soundBtn.x + soundBtn.width / 2;
        int cy = soundBtn.y + soundBtn.height / 2;
        Color iconColor = soundOn ? (hover ? Color.WHITE : new Color(255, 225, 120)) : new Color(170, 150, 180);
        g.setColor(iconColor);

        // ตัวลำโพง (โคน + กรวย)
        int spkLeft = cx - 10;
        int spkTop = cy - 4;
        g.fillRect(spkLeft, spkTop, 5, 8);

        Polygon cone = new Polygon();
        cone.addPoint(spkLeft + 5, spkTop);
        cone.addPoint(spkLeft + 11, cy - 8);
        cone.addPoint(spkLeft + 11, cy + 8);
        cone.addPoint(spkLeft + 5, cy + 4);
        g.fillPolygon(cone);

        if (soundOn) {
            // คลื่นเสียง 2 ระลอก
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(cx - 3, cy - 6, 12, 12, -45, 90);
            g.drawArc(cx - 1, cy - 10, 18, 20, -45, 90);
        } else {
            // เส้นขีดฆ่าสีแดง (Mute Slash)
            g.setColor(new Color(255, 90, 90));
            g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(cx - 9, cy - 9, cx + 9, cy + 9);
        }
    }

    private void drawHoverButton(Graphics2D g, Rectangle rect, String text, Color baseColor, boolean hover) {
        int expand = hover ? 3 : 0;
        int rx = rect.x - expand;
        int ry = rect.y - expand;
        int rw = rect.width + expand * 2;
        int rh = rect.height + expand * 2;

        // เงาตกกระทบ
        g.setColor(new Color(0, 0, 0, hover ? 190 : 140));
        g.fillRoundRect(rx + 4, ry + 6, rw, rh, 18, 18);

        // ตัวปุ่ม
        g.setColor(hover ? baseColor.brighter() : baseColor);
        g.fillRoundRect(rx, ry, rw, rh, 18, 18);

        g.setColor(hover ? Color.WHITE : Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(hover ? 3.0f : 2.2f));
        g.drawRoundRect(rx, ry, rw, rh, 18, 18);

        g.setFont(FontHelper.bold(22));
        FontMetrics fm = g.getFontMetrics();
        int tx = rx + (rw - fm.stringWidth(text)) / 2;
        int ty = ry + ((rh - fm.getHeight()) / 2) + fm.getAscent();

        g.setColor(new Color(0, 0, 0, 180));
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
            Color[] colors = {new Color(255, 193, 7), new Color(76, 175, 80), new Color(33, 150, 243), new Color(244, 67, 54), new Color(171, 71, 188)};
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
