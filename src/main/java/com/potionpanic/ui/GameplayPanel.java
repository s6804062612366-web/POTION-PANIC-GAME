package com.potionpanic.ui;

import com.potionpanic.engine.GameEngine;
import com.potionpanic.entity.Projectile;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.item.IngredientType;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ScaleHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

/**
 * หน้าจอการเล่นหลัก (Gameplay Panel)
 * แสดงผล 3 โซน (มอนสเตอร์บุกด้านบน, สายพานตรงกลาง, โต๊ะปรุงยาด้านล่าง)
 * รองรับคีย์ลัด [Q/W/E/R], [Backspace], Quick Recipe Board และระบบ Combo
 */
public class GameplayPanel extends JPanel {
    private final GameWindow window;
    private final GameEngine engine;
    private Timer gameLoopTimer;
    private long lastTime = System.nanoTime();

    private int mouseX = 0;
    private int mouseY = 0;

    // พิกัดป้ายคัมภีร์สูตรยาด่วน (คลิกเปิดสมุดเต็มจอได้)
    private final Rectangle recipeBoardRect = new Rectangle(740, Constants.ZONE_BOT_Y + 12, 510, 226);

    public GameplayPanel(GameWindow window, GameEngine engine) {
        this.window = window;
        this.engine = engine;

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setFocusable(true);
        setBackground(Constants.COLOR_BG_DARK);

        initListeners();
        initGameLoop();
    }

    private void initListeners() {
        // ดักจับการกดคีย์บอร์ด
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_1 -> engine.grabIngredientToCauldron(0);
                    case KeyEvent.VK_2 -> engine.grabIngredientToCauldron(1);
                    case KeyEvent.VK_3 -> engine.grabIngredientToCauldron(2);
                    case KeyEvent.VK_4 -> engine.grabIngredientToCauldron(3);
                    case KeyEvent.VK_5 -> engine.grabIngredientToCauldron(4);
                    case KeyEvent.VK_SPACE -> engine.brewPotion();
                    case KeyEvent.VK_BACK_SPACE -> engine.getCauldron().removeLastIngredient();
                    case KeyEvent.VK_C -> engine.getCauldron().clear();

                    // คีย์ลัดเลือกขวดยาในช่องพักยา [Q, W, E, R]
                    case KeyEvent.VK_Q -> engine.getPotionRack().selectSlot(0);
                    case KeyEvent.VK_W -> engine.getPotionRack().selectSlot(1);
                    case KeyEvent.VK_E -> engine.getPotionRack().selectSlot(2);
                    case KeyEvent.VK_R -> engine.getPotionRack().selectSlot(3);

                    case KeyEvent.VK_TAB, KeyEvent.VK_H -> engine.toggleRecipeBook();
                    case KeyEvent.VK_ESCAPE -> window.showScene("MENU");
                }
            }
        });

        // ดักจับการเคลื่อนที่และคลิกเมาส์ โดยแปลงพิกัดเข้าสู่ Virtual Coordinate (1280x720)
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

                // ถ้าสมุดสูตรยาเต็มจอเปิดอยู่ คลิกที่ใดก็ได้เพื่อปิด
                if (engine.isRecipeBookOpen()) {
                    engine.setRecipeBookOpen(false);
                    return;
                }

                // คลิกที่คัมภีร์สูตรยาเพื่อเปิดหน้าต่างสูตรยาเต็มจอ
                if (recipeBoardRect.contains(mx, my)) {
                    engine.toggleRecipeBook();
                    return;
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

            engine.update(dt);

            // ตรวจสอบการเปลี่ยนหน้าต่างเมื่อชนะหรือแพ้
            if (engine.getState() == GameEngine.GameState.GAME_OVER) {
                gameLoopTimer.stop();
                window.getGameOverPanel().setFinalScore(engine.getScore(), engine.getCurrentStage());
                window.showScene("GAME_OVER");
            } else if (engine.getState() == GameEngine.GameState.VICTORY) {
                gameLoopTimer.stop();
                window.getVictoryPanel().setResults(engine.getScore(), engine.getEarnedStars(), engine.getCurrentStage());
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
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int screenW = getWidth();
        int screenH = getHeight();

        // ระบายสีพื้นหลังรอบนอก (Pillarbox / Letterbox)
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

        // --- เริ่มต้นการเรนเดอร์ในพิกัดเสมือน 1280 x 720 ---

        // 1. วาดพื้นหลังทั้ง 3 โซน
        drawBackgroundZones(g2);

        // 2. วาดหอคอย
        engine.getTower().render(g2);

        // 3. วาดมอนสเตอร์ในเลนบน
        for (Monster m : engine.getActiveMonsters()) {
            m.render(g2);
        }

        // 4. วาดสายพานลำเลียงในโซนกลาง
        engine.getBelt().render(g2);

        // 5. วาดโต๊ะปรุงยา หม้อต้ม และชั้นวางยาในโซนล่าง
        engine.getCauldron().render(g2);
        engine.getPotionRack().render(g2);

        // 6. วาดคัมภีร์สูตรยาด่วนถาวรบนโต๊ะ (Quick Recipe Board)
        drawQuickRecipeBoard(g2);

        // 7. วาดขวดยาที่ลอยอยู่ในอากาศ
        for (Projectile p : engine.getActiveProjectiles()) {
            p.render(g2);
        }

        // 8. วาด HUD ด้านบน (Wave, Score, Combo Streak)
        drawTopHUD(g2);

        // 9. วาดเป้าเล็งเมาส์ (Aiming Crosshair) เมื่อมียาพร้อมปา
        drawAimingReticle(g2);

        // 10. วาด Popup สมุดสูตรยาเต็มจอเมื่อกดเปิด
        if (engine.isRecipeBookOpen()) {
            RecipeBookDialog.render(g2, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }

        // 11. วาดเอฟเฟกต์อันตรายเรืองแสงรอบขอบจอเมื่อเลือดหอคอยต่ำกว่า 35%
        double hpRatio = (double) engine.getTower().getHp() / engine.getTower().getMaxHp();
        if (hpRatio < 0.35 && !engine.getTower().isDead()) {
            drawLowHpDangerVignette(g2, hpRatio);
        }

        // --- สิ้นสุดการเรนเดอร์ คืนค่า Transform ---
        g2.setTransform(origTx);
        g2.setClip(origClip);
    }

    private void drawBackgroundZones(Graphics2D g) {
        int w = Constants.WINDOW_WIDTH;
        int h = Constants.WINDOW_HEIGHT;

        // โซนบน: ท้องฟ้ากลางคืนและเลนมอนสเตอร์
        GradientPaint skyGradient = new GradientPaint(0, 0, new Color(15, 12, 35), 0, Constants.ZONE_MID_Y, new Color(30, 20, 50));
        g.setPaint(skyGradient);
        g.fillRect(0, 0, w, Constants.ZONE_MID_Y);

        // ทางเดินดิน/หินเวทมนตร์ของมอนสเตอร์
        g.setColor(new Color(45, 38, 55));
        g.fillRect(0, Constants.MONSTER_LANE_Y - 10, w, 90);
        g.setColor(new Color(65, 55, 80));
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, Constants.MONSTER_LANE_Y - 10, w, Constants.MONSTER_LANE_Y - 10);
        g.drawLine(0, Constants.MONSTER_LANE_Y + 80, w, Constants.MONSTER_LANE_Y + 80);

        // ดวงจันทร์เสี้ยว
        g.setColor(new Color(255, 245, 200, 220));
        g.fillOval(w - 180, 25, 60, 60);
        g.setColor(new Color(15, 12, 35));
        g.fillOval(w - 195, 20, 55, 55);

        // โซนกลาง: แท่นวางสายพานลำเลียง
        GradientPaint midGrad = new GradientPaint(0, Constants.ZONE_MID_Y, new Color(25, 20, 30), 0, Constants.ZONE_BOT_Y, new Color(18, 15, 25));
        g.setPaint(midGrad);
        g.fillRect(0, Constants.ZONE_MID_Y, w, Constants.ZONE_MID_HEIGHT);

        // เส้นแบ่งโซนเรืองแสงสีทอง
        g.setColor(new Color(Constants.COLOR_ACCENT_GOLD.getRed(), Constants.COLOR_ACCENT_GOLD.getGreen(), Constants.COLOR_ACCENT_GOLD.getBlue(), 80));
        g.drawLine(0, Constants.ZONE_MID_Y, w, Constants.ZONE_MID_Y);
        g.drawLine(0, Constants.ZONE_BOT_Y, w, Constants.ZONE_BOT_Y);

        // โซนล่าง: โต๊ะห้องแล็บปรุงยาของนักปรุงยา (Alchemist Workbench)
        GradientPaint botGrad = new GradientPaint(0, Constants.ZONE_BOT_Y, new Color(50, 35, 28), 0, h, new Color(28, 18, 14));
        g.setPaint(botGrad);
        g.fillRect(0, Constants.ZONE_BOT_Y, w, Constants.ZONE_BOT_HEIGHT);

        // ลายไม้บนโต๊ะ
        g.setColor(new Color(75, 52, 42));
        for (int y = Constants.ZONE_BOT_Y + 30; y < h; y += 45) {
            g.drawLine(0, y, w, y);
        }
    }

    private void drawTopHUD(Graphics2D g) {
        int w = Constants.WINDOW_WIDTH;

        // แถบข้อมูลด้านบนสุด (HUD Banner)
        g.setColor(new Color(10, 8, 20, 210));
        g.fillRoundRect(w / 2 - 290, 10, 580, 42, 14, 14);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(w / 2 - 290, 10, 580, 42, 14, 14);

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

        g.drawString(waveText, w / 2 - 275, 36);
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.drawString(scoreText, w / 2 - 75, 36);
        g.setColor(new Color(255, 110, 110));
        g.drawString(monstersText, w / 2 + 95, 36);

        // วาด Combo Streak ถ้ามี
        int combo = engine.getComboCount();
        if (combo >= 2) {
            int comboX = w - 210;
            int comboY = 12;
            g.setColor(new Color(255, 140, 0, 220));
            g.fillRoundRect(comboX, comboY, 190, 38, 10, 10);
            g.setColor(Constants.COLOR_ACCENT_GOLD);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(comboX, comboY, 190, 38, 10, 10);

            g.setColor(Color.WHITE);
            g.setFont(com.potionpanic.util.FontHelper.bold(14));
            g.drawString("COMBO x" + combo + "!", comboX + 22, comboY + 24);
        }
    }

    /**
     * วาดคัมภีร์สูตรยาด่วนถาวรบนโต๊ะ (Quick Recipe Board)
     * แสดงเฉพาะสูตรยาที่สามารถผสมได้ในด่านปัจจุบัน ดีไซน์คลีนสบายตา
     */
    private void drawQuickRecipeBoard(Graphics2D g) {
        int bx = recipeBoardRect.x;
        int by = recipeBoardRect.y;
        int bw = recipeBoardRect.width;
        int bh = recipeBoardRect.height;

        // แผ่นคัมภีร์ไม้โบราณ
        g.setColor(new Color(42, 28, 22));
        g.fillRoundRect(bx, by, bw, bh, 14, 14);
        g.setColor(Constants.COLOR_WOOD_LIGHT);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(bx, by, bw, bh, 14, 14);

        // พื้นกระดาษด้านใน
        g.setColor(new Color(245, 235, 215, 240));
        g.fillRoundRect(bx + 8, by + 8, bw - 16, bh - 16, 10, 10);

        // หัวข้อคัมภีร์
        g.setColor(new Color(80, 45, 20));
        g.setFont(com.potionpanic.util.FontHelper.bold(13));
        g.drawString("คัมภีร์สูตรยาเฉพาะด่าน (คลิกดูละเอียด)", bx + 16, by + 28);

        // เส้นคั่น
        g.setColor(new Color(190, 165, 135));
        g.drawLine(bx + 16, by + 34, bx + bw - 16, by + 34);

        // แสดงเฉพาะสูตรยาที่ทำได้ในด่านนี้ (Clean Thai Labels)
        java.util.List<String[]> activeRecipes = new java.util.ArrayList<>();
        activeRecipes.add(new String[]{"[ยาเพลิง]", "สมุนไพร x2", "ชนะทาง อสูรพฤกษา (ดาเมจหมู่ + ไฟไหม้)"});
        activeRecipes.add(new String[]{"[ยาน้ำแข็ง]", "ผลึกน้ำแข็ง x2", "ชนะทาง โกเลมไฟ (หน่วงช้าลง 50%)"});

        java.util.List<IngredientType> allowed = engine.getCurrentStageConfig().getAllowedIngredients();
        if (allowed.contains(IngredientType.TOXIC_MUSHROOM)) {
            activeRecipes.add(new String[]{"[ยากรดพิษ]", "เห็ดพิษ x2", "ชนะทาง สัตว์ร้ายเกราะ (ละลายเกราะหนา)"});
        }
        if (allowed.contains(IngredientType.THUNDER_ROOT)) {
            activeRecipes.add(new String[]{"[ยาสายฟ้า]", "รากสายฟ้า x2", "ชนะทาง อิมป์ว่องไว (ชะงัก Stun)"});
            activeRecipes.add(new String[]{"[ยาระเบิด]", "สมุนไพร + รากสายฟ้า", "ระเบิดกวาดล้างรัศมีกว้าง"});
        }

        int rowY = by + 52;
        for (String[] r : activeRecipes) {
            Color potionColor = switch (r[0]) {
                case "[ยาเพลิง]" -> new Color(210, 50, 35);
                case "[ยาน้ำแข็ง]" -> new Color(25, 118, 210);
                case "[ยากรดพิษ]" -> new Color(56, 142, 60);
                case "[ยาสายฟ้า]" -> new Color(200, 140, 0);
                default -> new Color(190, 70, 20);
            };

            g.setFont(com.potionpanic.util.FontHelper.bold(11));
            g.setColor(potionColor);
            g.drawString(r[0] + ":", bx + 18, rowY);

            g.setFont(com.potionpanic.util.FontHelper.plain(11));
            g.setColor(new Color(110, 60, 25));
            g.drawString(r[1], bx + 95, rowY);

            g.setColor(new Color(30, 95, 40));
            g.drawString("➔ " + r[2], bx + 215, rowY);

            rowY += 27;
        }

        // แถบคำแนะนำคีย์ลัดด้านล่าง
        g.setColor(new Color(230, 215, 185));
        g.fillRect(bx + 8, by + bh - 32, bw - 16, 24);
        g.setColor(new Color(120, 50, 15));
        g.setFont(com.potionpanic.util.FontHelper.bold(11));
        g.drawString("[1-5] หยิบของ | [Space] ผสมยา | [Backspace] ลบของ | [Q-R] เลือกยา | [F11] เต็มจอ", bx + 16, by + bh - 16);
    }

    /**
     * วาดเอฟเฟกต์สีแดงเตือนภัยอันตรายรอบขอบจอเมื่อเลือดหอคอย < 35%
     */
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
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
            g.setStroke(new BasicStroke(2.5f));

            int r = 24;
            g.drawOval(mouseX - r, mouseY - r, r * 2, r * 2);
            g.drawLine(mouseX - r - 8, mouseY, mouseX + r + 8, mouseY);
            g.drawLine(mouseX, mouseY - r - 8, mouseX, mouseY + r + 8);

            g.setColor(Color.WHITE);
            g.setFont(Constants.FONT_SMALL);
            g.drawString("คลิกเพื่อปา " + selected.getName(), mouseX + 16, mouseY - 14);
        }
    }
}
