package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.engine.GameEngine;
import com.potionpanic.engine.StageConfig;
import com.potionpanic.entity.Projectile;
import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.item.IngredientType;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * หน้าจอการเล่นหลัก (Gameplay Panel)
 * - แสดงภาพพื้นหลัง 3 โซน (bg_gameplay.png)
 * - ป๊อปอัพสไลด์การ์ดสอนเล่นก่อนเริ่มด่าน (StageIntroOverlay)
 * - ระบบตำรา 3 ส่วนผสมแบบภาพไอคอนบนกระดานด่วน
 * - ระบบสั่นหน้าจอ (Screen Shake) และเอฟเฟกต์การเคลื่อนไหวเต็มรูปแบบ
 * - ไร้อีโมจิ 100%
 */
public class GameplayPanel extends JPanel {
    private final GameWindow window;
    private final GameEngine engine;
    private Timer gameLoopTimer;
    private long lastTime = System.nanoTime();

    private int mouseX = 0;
    private int mouseY = 0;

    // ป๊อปอัพสอนสูตรก่อนเริ่มด่าน
    private StageIntroOverlay introOverlay;

    // หน้าต่างสมุดสูตรยาบนโต๊ะปรุงยา (เปิด/ปิดได้ตามสั่ง)
    private boolean tableBookOpen = false;
    private final Rectangle closedBookRect = new Rectangle(1005, Constants.ZONE_BOT_Y + 16, 215, 244);
    private final Rectangle openBookRect = new Rectangle(710, Constants.ZONE_BOT_Y + 10, 545, 254);
    private final Rectangle bookCloseBtnRect = new Rectangle(710 + 545 - 34, Constants.ZONE_BOT_Y + 18, 22, 22);

    // พิกัดปุ่มสุ่มสายพาน [F]
    private final Rectangle rerollBtnRect = new Rectangle(
            Constants.BELT_START_X - 40 + (Constants.BELT_SLOT_SIZE + Constants.BELT_SLOT_GAP) * Constants.BELT_SLOTS_COUNT + 60 + 12,
            Constants.ZONE_MID_Y + 30, 120, 98);

    // ปุ่มเปิด-ปิดเสียง [M] (ไอคอนเวกเตอร์กะทัดรัด)
    private final Rectangle soundBtnRect = new Rectangle(Constants.WINDOW_WIDTH - 54, 12, 38, 38);

    public GameplayPanel(GameWindow window, GameEngine engine) {
        this.window = window;
        this.engine = engine;

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setFocusable(true);
        setBackground(Constants.COLOR_BG_DARK);

        initListeners();
        initGameLoop();
    }

    public void showStageIntro(StageConfig config) {
        this.introOverlay = new StageIntroOverlay(config);
    }

    private void initListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                // ถ้าป๊อปอัพสไลด์สอนเล่นเปิดอยู่ ส่งคีย์ให้ป๊อปอัพจัดการก่อน
                if (introOverlay != null && introOverlay.isActive()) {
                    if (introOverlay.handleKeyPress(code)) {
                        repaint();
                        return;
                    }
                }

                switch (code) {
                    case KeyEvent.VK_1 -> engine.grabIngredientToCauldron(0);
                    case KeyEvent.VK_2 -> engine.grabIngredientToCauldron(1);
                    case KeyEvent.VK_3 -> engine.grabIngredientToCauldron(2);
                    case KeyEvent.VK_4 -> engine.grabIngredientToCauldron(3);
                    case KeyEvent.VK_5 -> engine.grabIngredientToCauldron(4);
                    case KeyEvent.VK_SPACE -> engine.brewPotion();
                    case KeyEvent.VK_BACK_SPACE -> engine.getCauldron().removeLastIngredient();
                    case KeyEvent.VK_C -> engine.getCauldron().clear();
                    case KeyEvent.VK_F -> engine.rerollBelt(); // สุ่มสายพานใหม่

                    // คีย์ลัดเลือกขวดยาในช่องพักยา [Q, W, E, R]
                    case KeyEvent.VK_Q -> engine.getPotionRack().selectSlot(0);
                    case KeyEvent.VK_W -> engine.getPotionRack().selectSlot(1);
                    case KeyEvent.VK_E -> engine.getPotionRack().selectSlot(2);
                    case KeyEvent.VK_R -> engine.getPotionRack().selectSlot(3);

                    case KeyEvent.VK_TAB, KeyEvent.VK_H -> engine.toggleRecipeBook();
                    case KeyEvent.VK_ESCAPE -> {
                        SoundManager.getInstance().stopBgm();
                        window.showScene("MENU");
                    }
                }
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private void updateMouseCoords(MouseEvent e) {
                Point v = ScaleHelper.toVirtual(e.getX(), e.getY(), getWidth(), getHeight());
                mouseX = v.x;
                mouseY = v.y;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMouseCoords(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateMouseCoords(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                updateMouseCoords(e);
                int mx = mouseX;
                int my = mouseY;

                // ถ้าป๊อปอัพสอนเล่นเปิดอยู่
                if (introOverlay != null && introOverlay.isActive()) {
                    if (introOverlay.handleMouseClick(mx, my)) {
                        repaint();
                        return;
                    }
                }

                // ปุ่มสลับเสียง [M]
                if (soundBtnRect.contains(mx, my)) {
                    SoundManager.getInstance().toggleSound();
                    SoundManager.getInstance().playGrab();
                    repaint();
                    return;
                }

                // ถ้าสมุดสูตรยาเต็มจอเปิดอยู่ คลิกที่ใดก็ได้เพื่อปิด
                if (engine.isRecipeBookOpen()) {
                    engine.setRecipeBookOpen(false);
                    return;
                }

                // คลิกที่ปุ่มสุ่มสายพานใหม่
                if (rerollBtnRect.contains(mx, my)) {
                    engine.rerollBelt();
                    return;
                }

                // คลิกเปิด / ปิดสมุดสูตรยาบนโต๊ะปรุงยา
                if (tableBookOpen) {
                    if (bookCloseBtnRect.contains(mx, my) || (openBookRect.contains(mx, my) && my < openBookRect.y + 36)) {
                        tableBookOpen = false;
                        SoundManager.getInstance().playGrab();
                        repaint();
                        return;
                    }
                } else {
                    if (closedBookRect.contains(mx, my)) {
                        tableBookOpen = true;
                        SoundManager.getInstance().playGrab();
                        repaint();
                        return;
                    }
                }

                // คลิกที่ชั้นวางยาเพื่อเลือกขวด
                int clickedSlot = engine.getPotionRack().getSlotAt(mx, my);
                if (clickedSlot != -1) {
                    engine.getPotionRack().selectSlot(clickedSlot);
                    return;
                }

                // คลิกที่โซนบน (เลนมอนสเตอร์) เพื่อปาขวดยา
                if (my < Constants.ZONE_MID_Y && my >= 0 && mx >= 0 && mx <= Constants.WINDOW_WIDTH) {
                    engine.throwPotionAt(mx, my);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void initGameLoop() {
        gameLoopTimer = new Timer(16, e -> {
            long now = System.nanoTime();
            double dt = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            if (dt > 0.05) dt = 0.05;

            // ถ้าป๊อปอัพสอนเล่นเปิดอยู่ ให้หยุดเวลาเกมไว้
            if (introOverlay != null && introOverlay.isActive()) {
                introOverlay.update(dt);
            } else {
                engine.update(dt);
            }

            // ตรวจสอบการเปลี่ยนหน้าต่างเมื่อชนะหรือแพ้
            if (engine.getState() == GameEngine.GameState.GAME_OVER) {
                gameLoopTimer.stop();
                window.getGameOverPanel().setFinalScore(engine.getScore(), engine.getCurrentStage(),
                        Math.max(0, engine.getWaveManager().getCurrentWave() - 1), engine.getEndlessStarsEarned());
                window.showScene("GAME_OVER");
            } else if (engine.getState() == GameEngine.GameState.VICTORY) {
                gameLoopTimer.stop();
                window.getVictoryPanel().setResults(engine.getScore(), engine.getEarnedStars(), engine.getCurrentStage(),
                        engine.getNewStarsGained());
                window.showScene("VICTORY");
            }

            repaint();
        });
    }

    public void startLoop() {
        lastTime = System.nanoTime();
        if (gameLoopTimer != null && !gameLoopTimer.isRunning()) {
            gameLoopTimer.start();
        }
    }

    public void stopLoop() {
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
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

        // ระบายสีพื้นหลังรอบนอก (Letterbox)
        g2.setColor(new Color(10, 8, 20));
        g2.fillRect(0, 0, screenW, screenH);

        double scale = ScaleHelper.getScale(screenW, screenH);
        int ox = ScaleHelper.getOffsetX(screenW, screenH, scale);
        int oy = ScaleHelper.getOffsetY(screenW, screenH, scale);

        Shape origClip = g2.getClip();
        g2.clipRect(ox, oy, (int) Math.round(Constants.WINDOW_WIDTH * scale), (int) Math.round(Constants.WINDOW_HEIGHT * scale));

        AffineTransform origTx = g2.getTransform();

        // เพิ่มการสั่นสะเทือนหน้าจอ (Screen Shake)
        Point shake = engine.getScreenShakeOffset();
        g2.translate(ox + shake.x * scale, oy + shake.y * scale);
        g2.scale(scale, scale);

        // --- เริ่มต้นการเรนเดอร์ในพิกัดเสมือน 1280 x 720 ---

        // 1. วาดภาพพื้นหลัง 3 โซน (bg_gameplay.png หรือกราฟิกเสริม)
        drawBackgroundZones(g2);

        // 2. วาดวิชวลเอฟเฟกต์บนพื้นผิวถนน (บ่อน้ำยา, คลื่นกระแทก)
        for (VisualEffect eff : engine.getActiveEffects()) {
            eff.render(g2);
        }

        // 3. วาดหอคอย
        engine.getTower().render(g2);

        // 4. วาดมอนสเตอร์ในเลนบน (เรียงตามลำดับแกน Y)
        for (Monster m : engine.getActiveMonsters()) {
            m.render(g2);
        }

        // 5. วาดสายพานลำเลียงในโซนกลาง
        engine.getBelt().render(g2);

        // 6. วาดโต๊ะปรุงยา หม้อต้ม และชั้นวางยาในโซนล่าง
        engine.getCauldron().render(g2);
        engine.getPotionRack().render(g2);

        // 7. วาดคัมภีร์สูตรยาด่วนถาวรบนโต๊ะ (Quick Recipe Board) ด้วยภาพไอคอน
        drawQuickRecipeBoard(g2);

        // 8. วาดขวดยาที่ลอยอยู่ในอากาศ
        for (Projectile p : engine.getActiveProjectiles()) {
            p.render(g2);
        }

        // 9. วาด HUD ด้านบน (Wave, Score, Combo Streak, Mute Button)
        drawTopHUD(g2);

        // 10. วาดเป้าเล็งเมาส์ (Aiming Crosshair) พร้อมวงพรีวิวรัศมีระเบิด
        drawAimingReticle(g2);

        // 11. วาด Popup สมุดสูตรยาเต็มจอเมื่อกดเปิด
        if (engine.isRecipeBookOpen()) {
            RecipeBookDialog.render(g2, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }

        // 12. วาดเอฟเฟกต์อันตรายเรืองแสงรอบขอบจอเมื่อเลือดหอคอยต่ำกว่า 35%
        double hpRatio = (double) engine.getTower().getHp() / engine.getTower().getMaxHp();
        if (hpRatio < 0.35 && !engine.getTower().isDead()) {
            drawLowHpDangerVignette(g2, hpRatio);
        }

        // 13. วาดป๊อปอัพสอนเล่นก่อนเริ่มด่าน (StageIntroOverlay)
        if (introOverlay != null && introOverlay.isActive()) {
            introOverlay.render(g2, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }

        // คืนค่า Transform
        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    private void drawBackgroundZones(Graphics2D g) {
        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;

        BufferedImage bg = AssetLoader.getImage("bg_gameplay");
        if (bg != null) {
            g.drawImage(bg, 0, 0, w, h, null);
            // เส้นคั่นเรืองแสงสีทองบางๆ เพื่อช่วยนำสายตา
            g.setColor(new Color(255, 215, 0, 45));
            g.drawLine(0, Constants.ZONE_MID_Y, w, Constants.ZONE_MID_Y);
            g.drawLine(0, Constants.ZONE_BOT_Y, w, Constants.ZONE_BOT_Y);
            return;
        }

        // กรณี Fallback หากไม่มีภาพพื้นหลัง
        GradientPaint skyGradient = new GradientPaint(0, 0, new Color(15, 12, 35), 0, Constants.ZONE_MID_Y, new Color(30, 20, 50));
        g.setPaint(skyGradient);
        g.fillRect(0, 0, w, Constants.ZONE_MID_Y);

        g.setColor(new Color(45, 38, 55));
        g.fillRect(0, Constants.MONSTER_LANE_Y - 14, w, 94);

        GradientPaint midGrad = new GradientPaint(0, Constants.ZONE_MID_Y, new Color(25, 20, 30), 0, Constants.ZONE_BOT_Y, new Color(18, 15, 25));
        g.setPaint(midGrad);
        g.fillRect(0, Constants.ZONE_MID_Y, w, Constants.ZONE_MID_HEIGHT);

        GradientPaint botGrad = new GradientPaint(0, Constants.ZONE_BOT_Y, new Color(50, 35, 28), 0, h, new Color(28, 18, 14));
        g.setPaint(botGrad);
        g.fillRect(0, Constants.ZONE_BOT_Y, w, Constants.ZONE_BOT_HEIGHT);
    }

    private void drawTopHUD(Graphics2D g) {
        int w = Constants.WINDOW_WIDTH;

        // แถบข้อมูลด้านบนสุด (HUD Banner)
        g.setColor(new Color(10, 8, 20, 220));
        g.fillRoundRect(w / 2 - 310, 10, 620, 42, 14, 14);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(w / 2 - 310, 10, 620, 42, 14, 14);

        g.setFont(Constants.FONT_BOLD);
        g.setColor(Color.WHITE);
        String stageName = engine.getCurrentStageConfig().isEndless()
                ? "โหมด Endless"
                : "ด่าน " + engine.getCurrentStage();
        String waveText = stageName + " | WAVE: " + engine.getWaveManager().getCurrentWave() +
                (engine.getCurrentStageConfig().isEndless() ? "" : (" / " + engine.getWaveManager().getTotalWaves()));
        String scoreText = "คะแนน: " + engine.getScore();
        int remaining = engine.getActiveMonsters().size() + engine.getWaveManager().getRemainingInQueue();
        String monstersText = "มอนสเตอร์: " + remaining;

        g.drawString(waveText, w / 2 - 295, 36);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.drawString(scoreText, w / 2 - 80, 36);
        g.setColor(new Color(255, 120, 120));
        g.drawString(monstersText, w / 2 + 105, 36);

        // วาด Combo Streak ถ้ามี
        int combo = engine.getComboCount();
        if (combo >= 2) {
            int comboX = w - 245;
            int comboY = 12;
            g.setColor(new Color(255, 140, 0, 230));
            g.fillRoundRect(comboX, comboY, 125, 38, 10, 10);
            g.setColor(Constants.COLOR_ACCENT_GOLD);
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(comboX, comboY, 125, 38, 10, 10);

            g.setColor(Color.WHITE);
            g.setFont(FontHelper.bold(14));
            g.drawString("COMBO x" + combo + "!", comboX + 16, comboY + 24);
        }

        // ปุ่ม Mute เสียง [M] (ไอคอนลำโพงเวกเตอร์กะทัดรัด)
        drawSoundButton(g);
    }

    private void drawSoundButton(Graphics2D g) {
        boolean soundOn = SoundManager.getInstance().isSoundEnabled();
        boolean hover = soundBtnRect.contains(mouseX, mouseY);

        g.setColor(hover ? new Color(55, 42, 75, 230) : new Color(25, 18, 35, 220));
        g.fillRoundRect(soundBtnRect.x, soundBtnRect.y, soundBtnRect.width, soundBtnRect.height, 10, 10);
        g.setColor(hover ? Constants.COLOR_ACCENT_GOLD : new Color(110, 95, 140));
        g.setStroke(new BasicStroke(hover ? 2.0f : 1.4f));
        g.drawRoundRect(soundBtnRect.x, soundBtnRect.y, soundBtnRect.width, soundBtnRect.height, 10, 10);

        int cx = soundBtnRect.x + soundBtnRect.width / 2;
        int cy = soundBtnRect.y + soundBtnRect.height / 2;
        Color iconColor = soundOn ? (hover ? Color.WHITE : new Color(255, 225, 120)) : new Color(170, 150, 180);
        g.setColor(iconColor);

        // ตัวลำโพงเวกเตอร์
        int spkLeft = cx - 9;
        int spkTop = cy - 4;
        g.fillRect(spkLeft, spkTop, 5, 8);

        Polygon cone = new Polygon();
        cone.addPoint(spkLeft + 5, spkTop);
        cone.addPoint(spkLeft + 10, cy - 7);
        cone.addPoint(spkLeft + 10, cy + 7);
        cone.addPoint(spkLeft + 5, cy + 4);
        g.fillPolygon(cone);

        if (soundOn) {
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(cx - 3, cy - 6, 12, 12, -45, 90);
            g.drawArc(cx - 1, cy - 9, 17, 18, -45, 90);
        } else {
            g.setColor(new Color(255, 90, 90));
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(cx - 8, cy - 8, cx + 8, cy + 8);
        }
    }

    /**
     * วาดตำราปรุงยาบนโต๊ะ (Grimoire on Tabletop)
     * สามารถคลิกสลับเปิด/ปิดได้ตามต้องการ
     */
    private void drawQuickRecipeBoard(Graphics2D g) {
        if (!tableBookOpen) {
            drawClosedGrimoire(g);
        } else {
            drawOpenGrimoire(g);
        }
    }

    /**
     * วาดหน้าปกหนังสือตำราสูตรยาสุดคลาสสิก (Closed Leather Grimoire)
     */
    private void drawClosedGrimoire(Graphics2D g) {
        int bx = closedBookRect.x;
        int by = closedBookRect.y;
        int bw = closedBookRect.width;
        int bh = closedBookRect.height;
        boolean hover = closedBookRect.contains(mouseX, mouseY);

        // เงาตกกระทบของสมุดบนโต๊ะไม้
        g.setColor(new Color(0, 0, 0, hover ? 180 : 130));
        g.fillRoundRect(bx + 4, by + 6, bw, bh, 18, 18);

        // ปกหนังสมุดสีน้ำตาลเข้มหรูหรา (Fantasy Leather Cover)
        GradientPaint leather = new GradientPaint(bx, by, new Color(56, 32, 22), bx + bw, by + bh, new Color(28, 16, 12));
        g.setPaint(leather);
        g.fillRoundRect(bx, by, bw, bh, 16, 16);

        // ขอบทองประดับรอบเล่ม
        g.setColor(hover ? Constants.COLOR_ACCENT_GOLD : new Color(185, 140, 65));
        g.setStroke(new BasicStroke(hover ? 2.5f : 1.8f));
        g.drawRoundRect(bx, by, bw, bh, 16, 16);

        // ลายเส้นประดับขอบใน
        g.setColor(new Color(130, 95, 45, 150));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(bx + 6, by + 6, bw - 12, bh - 12, 12, 12);

        // สันสมุดหนังด้านซ้ายพร้อมแถบเย็บทอง
        int spineW = 20;
        GradientPaint spineGrad = new GradientPaint(bx, by, new Color(38, 20, 14), bx + spineW, by, new Color(60, 36, 26));
        g.setPaint(spineGrad);
        g.fillRoundRect(bx, by, spineW, bh, 16, 16);
        g.fillRect(bx + 8, by, spineW - 8, bh);
        g.setColor(new Color(195, 155, 75));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(bx + spineW, by + 4, bx + spineW, by + bh - 4);
        for (int i = 1; i <= 3; i++) {
            int ry = by + (bh * i / 4);
            g.setColor(Constants.COLOR_ACCENT_GOLD);
            g.drawLine(bx + 2, ry, bx + spineW - 2, ry);
        }

        // ริบบิ้นคั่นหน้าสมุดสีแดงห้อยลงมาด้านล่าง (Crimson Ribbon)
        int ribX = bx + bw / 2 - 8;
        int ribY = by + bh - 4;
        g.setColor(new Color(185, 30, 30));
        Polygon ribbon = new Polygon();
        ribbon.addPoint(ribX, ribY);
        ribbon.addPoint(ribX + 16, ribY);
        ribbon.addPoint(ribX + 16, ribY + 18);
        ribbon.addPoint(ribX + 8, ribY + 12);
        ribbon.addPoint(ribX, ribY + 18);
        g.fillPolygon(ribbon);

        // มุมทองเหลือง 4 มุม (Brass Corner Protectors)
        drawCornerOrnament(g, bx + 4, by + 4, 1, 1);
        drawCornerOrnament(g, bx + bw - 4, by + 4, -1, 1);
        drawCornerOrnament(g, bx + 4, by + bh - 4, 1, -1);
        drawCornerOrnament(g, bx + bw - 4, by + bh - 4, -1, -1);

        // ตราสัญลักษณ์เวทมนตร์สีทองกึ่งกลางหน้าปก
        int cx = bx + spineW + (bw - spineW) / 2;
        int cy = by + 90;
        int sealR = 34;

        g.setColor(new Color(25, 14, 10));
        g.fillOval(cx - sealR, cy - sealR, sealR * 2, sealR * 2);
        g.setColor(hover ? Constants.COLOR_ACCENT_GOLD : new Color(205, 160, 70));
        g.setStroke(new BasicStroke(hover ? 2.2f : 1.6f));
        g.drawOval(cx - sealR, cy - sealR, sealR * 2, sealR * 2);
        g.drawOval(cx - sealR + 4, cy - sealR + 4, (sealR - 4) * 2, (sealR - 4) * 2);

        // วาดไอคอนขวดยาเรืองแสงกลางตรา
        BufferedImage flaskImg = AssetLoader.getImage("potion_explosive");
        if (flaskImg != null) {
            g.drawImage(flaskImg, cx - 18, cy - 18, 36, 36, null);
        }

        // ชื่อหน้าปก "ตำราสูตรยา"
        g.setFont(FontHelper.bold(17));
        FontMetrics fm = g.getFontMetrics();
        String title = "ตำราสูตรยา";
        int tx = cx - fm.stringWidth(title) / 2;
        int ty = cy + 54;
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(title, tx + 1, ty + 1);
        g.setColor(hover ? Color.WHITE : Constants.COLOR_ACCENT_GOLD);
        g.drawString(title, tx, ty);

        // คำแนะนำคลิกเปิด
        g.setFont(FontHelper.bold(11));
        FontMetrics subFm = g.getFontMetrics();
        String sub = hover ? "คลิกเพื่อเปิดอ่าน!" : "(คลิกเพื่อเปิด)";
        int sx = cx - subFm.stringWidth(sub) / 2;
        g.setColor(hover ? new Color(255, 235, 160) : new Color(185, 160, 130));
        g.drawString(sub, sx, ty + 24);
    }

    private void drawCornerOrnament(Graphics2D g, int x, int y, int dirX, int dirY) {
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x + dirX * 16, y);
        p.addPoint(x, y + dirY * 16);
        g.fillPolygon(p);
    }

    /**
     * วาดตำราที่กางเปิดออกเสมือนหนังสือจริง 2 หน้า (Open Two-Page Grimoire)
     * แสดงเฉพาะสมการรูปภาพกระชับ ไม่มีตัวหนังสือยิบย่อยรกตา
     */
    private void drawOpenGrimoire(Graphics2D g) {
        int bx = openBookRect.x;
        int by = openBookRect.y;
        int bw = openBookRect.width;
        int bh = openBookRect.height;

        // เงาตกกระทบของสมุดเปิด
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(bx + 4, by + 6, bw, bh, 18, 18);

        // แผ่นรองปกหนังด้านล่าง
        g.setColor(new Color(45, 26, 18));
        g.fillRoundRect(bx, by, bw, bh, 16, 16);
        g.setColor(new Color(160, 120, 60));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, by, bw, bh, 16, 16);

        // สองหน้ากระดาษพับกางคู่ (Left & Right Parchment Pages)
        int pageW = (bw - 20) / 2;
        int pageH = bh - 12;
        int leftX = bx + 6;
        int rightX = bx + 14 + pageW;
        int pageY = by + 6;

        drawMiniParchment(g, leftX, pageY, pageW, pageH);
        drawMiniParchment(g, rightX, pageY, pageW, pageH);

        // สันหนังสือตรงกลาง
        GradientPaint spineGrad = new GradientPaint(leftX + pageW, pageY, new Color(110, 75, 45, 180), rightX, pageY, new Color(75, 45, 25, 180));
        g.setPaint(spineGrad);
        g.fillRect(leftX + pageW - 4, pageY, 16, pageH);

        // หัวข้อหน้ากระดาษ
        g.setFont(FontHelper.bold(12));
        g.setColor(new Color(75, 38, 16));
        g.drawString("ตำราปรุงยาประจำด่าน", leftX + 14, pageY + 20);

        // ปุ่มปิด [X] ที่มุมขวาบน
        boolean closeHover = bookCloseBtnRect.contains(mouseX, mouseY);
        g.setColor(closeHover ? new Color(220, 50, 50) : new Color(160, 45, 45));
        g.fillRoundRect(bookCloseBtnRect.x, bookCloseBtnRect.y, bookCloseBtnRect.width, bookCloseBtnRect.height, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(FontHelper.bold(13));
        g.drawString("X", bookCloseBtnRect.x + 6, bookCloseBtnRect.y + 16);

        // รวบรวมสูตรยาประจำด่านปัจจุบัน
        record VisualEq(IngredientType i1, IngredientType i2, IngredientType i3, String potSprite, String monSprite, boolean isRare) {}
        List<VisualEq> eqList = new ArrayList<>();
        eqList.add(new VisualEq(IngredientType.RED_HERB, IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, "potion_fire", "monster_plant_creeper", false));
        eqList.add(new VisualEq(IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB, "potion_ice", "monster_fire_golem", false));

        List<IngredientType> allowed = engine.getCurrentStageConfig().getAllowedIngredients();
        if (allowed.contains(IngredientType.TOXIC_MUSHROOM)) {
            eqList.add(new VisualEq(IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM, IngredientType.RED_HERB, "potion_poison", "monster_armored_beast", false));
            eqList.add(new VisualEq(IngredientType.TOXIC_MUSHROOM, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB, "potion_frost_venom", "monster_armored_beast", false));
        }
        if (allowed.contains(IngredientType.THUNDER_ROOT)) {
            eqList.add(new VisualEq(IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT, IngredientType.BLUE_CRYSTAL, "potion_lightning", "monster_swift_imp", false));
            eqList.add(new VisualEq(null, null, null, "potion_explosive", "monster_chaos_boss", true));
        }

        // วาดสูตรสมการภาพ: หน้าซ้าย (สูตรที่ 1-3), หน้าขวา (สูตรที่ 4-6)
        int rowH = 64;
        int iconS = 24;
        int potSize = 28;

        for (int i = 0; i < eqList.size(); i++) {
            VisualEq eq = eqList.get(i);
            boolean onLeft = (i < 3);
            int pageStartX = onLeft ? leftX : rightX;
            int rowIndex = onLeft ? i : (i - 3);
            int ry = pageY + 30 + rowIndex * rowH;
            int rx = pageStartX + 8;
            int rw = pageW - 16;

            // พื้นหลังแถวสมการ
            g.setColor(new Color(230, 215, 190, 110));
            g.fillRoundRect(rx, ry, rw, rowH - 6, 8, 8);

            int cx = rx + 8;
            int cy = ry + (rowH - 6 - iconS) / 2;

            if (eq.isRare) {
                // ป้ายของแรร์สุ่ม
                g.setColor(new Color(215, 200, 175));
                g.fillRoundRect(cx, cy, 64, iconS, 5, 5);
                g.setColor(new Color(140, 60, 20));
                g.setStroke(new BasicStroke(1.0f));
                g.drawRoundRect(cx, cy, 64, iconS, 5, 5);
                g.setFont(FontHelper.bold(10));
                g.setColor(new Color(70, 25, 10));
                g.drawString("สุ่ม RARE", cx + 7, cy + 16);
                cx += 68;

                drawMathSign(g, cx, cy + iconS / 2, "->");
                cx += 14;

                // ขวดยาระเบิด
                BufferedImage potImg = AssetLoader.getImage(eq.potSprite);
                drawImageBox(g, cx, cy - 2, potSize, potImg, new Color(225, 110, 255));
                cx += potSize + 6;

                drawMathSign(g, cx, cy + iconS / 2, "->");
                cx += 14;

                // บอส
                BufferedImage monImg = AssetLoader.getImage(eq.monSprite);
                drawImageBox(g, cx, cy - 2, potSize, monImg, new Color(255, 180, 50));
            } else {
                // [Ing1] + [Ing2] + [Ing3] = [Potion] -> [Monster]
                drawImageBox(g, cx, cy, iconS, eq.i1.getImage(), new Color(175, 145, 110));
                cx += iconS + 2;

                drawMathSign(g, cx, cy + iconS / 2, "+");
                cx += 8;

                drawImageBox(g, cx, cy, iconS, eq.i2.getImage(), new Color(175, 145, 110));
                cx += iconS + 2;

                drawMathSign(g, cx, cy + iconS / 2, "+");
                cx += 8;

                drawImageBox(g, cx, cy, iconS, eq.i3.getImage(), new Color(175, 145, 110));
                cx += iconS + 2;

                drawMathSign(g, cx, cy + iconS / 2, "=");
                cx += 10;

                // ขวดยาผลลัพธ์
                BufferedImage potImg = AssetLoader.getImage(eq.potSprite);
                drawImageBox(g, cx, cy - 2, potSize, potImg, Constants.COLOR_ACCENT_GOLD);
                cx += potSize + 4;

                drawMathSign(g, cx, cy + iconS / 2, "->");
                cx += 12;

                // มอนสเตอร์เป้าหมาย
                BufferedImage monImg = AssetLoader.getImage(eq.monSprite);
                drawImageBox(g, cx, cy - 2, potSize, monImg, new Color(220, 100, 100));
            }
        }
    }

    private void drawMiniParchment(Graphics2D g, int x, int y, int w, int h) {
        GradientPaint grad = new GradientPaint(x, y, new Color(248, 240, 224), x + w, y + h, new Color(236, 222, 198));
        g.setPaint(grad);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(new Color(175, 145, 110));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setColor(new Color(210, 185, 155, 90));
        g.drawRoundRect(x + 5, y + 5, w - 10, h - 10, 8, 8);
    }

    private void drawImageBox(Graphics2D g, int x, int y, int size, BufferedImage img, Color borderColor) {
        g.setColor(new Color(245, 235, 215));
        g.fillRoundRect(x, y, size, size, 5, 5);
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, size, size, 5, 5);
        if (img != null) {
            g.drawImage(img, x + 2, y + 2, size - 4, size - 4, null);
        }
    }

    private void drawMathSign(Graphics2D g, int cx, int cy, String sign) {
        g.setFont(FontHelper.bold(12));
        g.setColor(new Color(110, 65, 30));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(sign, cx - fm.stringWidth(sign) / 2, cy + 4);
    }

    private void drawImageMini(Graphics2D g, int x, int y, int size, BufferedImage img) {
        g.setColor(new Color(245, 235, 215));
        g.fillRoundRect(x, y, size, size, 4, 4);
        g.setColor(new Color(180, 155, 120));
        g.drawRoundRect(x, y, size, size, 4, 4);
        if (img != null) {
            g.drawImage(img, x + 1, y + 1, size - 2, size - 2, null);
        }
    }

    private void drawLowHpDangerVignette(Graphics2D g, double hpRatio) {
        long time = System.currentTimeMillis();
        double pulse = (Math.sin(time / 200.0) + 1.0) / 2.0;
        int alpha = 40 + (int) (pulse * 70);

        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;

        g.setColor(new Color(220, 30, 30, alpha));
        g.setStroke(new BasicStroke(8.0f));
        g.drawRect(4, 4, w - 8, h - 8);

        g.setColor(new Color(180, 20, 20, alpha / 2));
        g.fillRect(0, 0, w, 8);
        g.fillRect(0, h - 8, w, 8);
        g.fillRect(0, 0, 8, h);
        g.fillRect(w - 8, 0, 8, h);
    }

    private void drawAimingReticle(Graphics2D g) {
        Potion selected = engine.getPotionRack().getSelectedPotion();
        if (selected != null && mouseY < Constants.ZONE_MID_Y && mouseY >= 0) {
            Color c = selected.getFlaskColor();
            int blastRadius = (int) selected.getRadius();

            // 1. วาดวงพรีวิวรัศมีระเบิดจริง (Real Splash Blast Area)
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
            g.fillOval(mouseX - blastRadius, mouseY - blastRadius / 2, blastRadius * 2, blastRadius);

            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 90));
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{6, 6}, 0));
            g.drawOval(mouseX - blastRadius, mouseY - blastRadius / 2, blastRadius * 2, blastRadius);

            // 2. วาดเป้าเล็งตรงกลาง
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 220));
            g.setStroke(new BasicStroke(2.2f));
            int r = 18;
            g.drawOval(mouseX - r, mouseY - r, r * 2, r * 2);
            g.drawLine(mouseX - r - 8, mouseY, mouseX + r + 8, mouseY);
            g.drawLine(mouseX, mouseY - r - 8, mouseX, mouseY + r + 8);

            // 3. ป้ายชื่อยา
            g.setColor(Color.WHITE);
            g.setFont(Constants.FONT_SMALL);
            g.drawString("คลิกเพื่อปา " + selected.getName().split(" ")[0], mouseX + 16, mouseY - 14);
        }
    }
}
