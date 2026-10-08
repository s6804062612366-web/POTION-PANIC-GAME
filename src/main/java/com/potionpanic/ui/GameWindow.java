package com.potionpanic.ui;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.engine.GameEngine;
import com.potionpanic.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * หน้าต่างหลักของเกม (JFrame) จัดการสลับฉากด้วย CardLayout
 * รองรับการย่อ/ขยายหน้าจอ และโหมดเต็มจอ (Fullscreen / Maximize)
 */
public class GameWindow extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);

    private final GameEngine engine;
    private final MainMenuPanel mainMenuPanel;
    private final StageSelectPanel stageSelectPanel;
    private final GameplayPanel gameplayPanel;
    private final GameOverPanel gameOverPanel;
    private final VictoryPanel victoryPanel;

    private boolean isFullScreen = false;
    private Rectangle previousBounds = new Rectangle(100, 100, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

    public GameWindow() {
        super(Constants.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // อนุญาตให้ขยายหน้าจอได้ และรองรับ Fullscreen ของ macOS
        setResizable(true);
        setMinimumSize(new Dimension(960, 540));
        getRootPane().putClientProperty("apple.awt.fullScreenable", true);

        this.engine = new GameEngine();

        this.mainMenuPanel = new MainMenuPanel(this);
        this.stageSelectPanel = new StageSelectPanel(this);
        this.gameplayPanel = new GameplayPanel(this, engine);
        this.gameOverPanel = new GameOverPanel(this);
        this.victoryPanel = new VictoryPanel(this);

        rootPanel.add(mainMenuPanel, "MENU");
        rootPanel.add(stageSelectPanel, "STAGE_SELECT");
        rootPanel.add(gameplayPanel, "GAME");
        rootPanel.add(gameOverPanel, "GAME_OVER");
        rootPanel.add(victoryPanel, "VICTORY");

        add(rootPanel);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        pack();
        setLocationRelativeTo(null); // แสดงผลกึ่งกลางหน้าจอ

        initGlobalKeyShortcuts();

        // เริ่มเล่น BGM สังเคราะห์เบาๆ คลอในเกม
        SoundManager.getInstance().startBgm();

        showScene("MENU");
    }

    private void initGlobalKeyShortcuts() {
        // ดักปุ่ม F11 เพื่อสลับโหมดเต็มจอ (Fullscreen Toggle)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F11) {
                toggleFullScreen();
                return true;
            }
            return false;
        });
    }

    /**
     * สลับระหว่างโหมดหน้าต่างปกติ กับโหมดเต็มจอ (Fullscreen Toggle)
     */
    public void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (!isFullScreen) {
            previousBounds = getBounds();
            dispose();
            setUndecorated(true);
            try {
                device.setFullScreenWindow(this);
            } catch (Exception e) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            setVisible(true);
            isFullScreen = true;
        } else {
            dispose();
            setUndecorated(false);
            device.setFullScreenWindow(null);
            setBounds(previousBounds);
            setVisible(true);
            isFullScreen = false;
        }
        revalidate();
        repaint();
    }

    public void showScene(String sceneName) {
        cardLayout.show(rootPanel, sceneName);
        if ("GAME".equals(sceneName)) {
            gameplayPanel.startLoop();
            gameplayPanel.requestFocusInWindow();
        } else {
            gameplayPanel.stopLoop();
            if ("STAGE_SELECT".equals(sceneName)) {
                stageSelectPanel.repaint();
            }
        }
    }

    public void startNewGame() {
        showScene("STAGE_SELECT");
    }

    public void startStage(int stageNumber) {
        engine.startStage(stageNumber);
        showScene("GAME");
    }

    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }

    public VictoryPanel getVictoryPanel() {
        return victoryPanel;
    }

    public StageSelectPanel getStageSelectPanel() {
        return stageSelectPanel;
    }

    public GameEngine getEngine() {
        return engine;
    }
}
