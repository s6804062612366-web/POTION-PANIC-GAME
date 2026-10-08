package com.potionpanic.engine;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Projectile;
import com.potionpanic.entity.Tower;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.item.Cauldron;
import com.potionpanic.item.ConveyorBelt;
import com.potionpanic.item.IngredientType;
import com.potionpanic.item.PotionRack;
import com.potionpanic.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * เอนจินหลักควบคุมลอจิกและสถานะทั้งหมดของเกม (Game Engine)
 * จัดการระบบด่าน (Stage Progression), การอัปเกรดเวทมนตร์, Combo Streak, และการประเมินดาว (1-3 Stars)
 */
public class GameEngine {

    public enum GameState {
        PLAYING,
        PAUSED,
        VICTORY,
        GAME_OVER
    }

    private final Tower tower;
    private final ConveyorBelt belt;
    private final Cauldron cauldron;
    private final PotionRack potionRack;
    private final WaveManager waveManager;

    private final List<Monster> activeMonsters = new ArrayList<>();
    private final List<Projectile> activeProjectiles = new ArrayList<>();

    private GameState state = GameState.PLAYING;
    private int score = 0;
    private boolean recipeBookOpen = false;

    // ระบบ Stage & Progression
    private int currentStage = 1;
    private StageConfig currentStageConfig;
    private int earnedStars = 0;

    // ระบบ Combo Streak
    private int comboCount = 0;
    private double comboTimer = 0.0;
    private final double maxComboTime = 4.0;

    public GameEngine() {
        this.currentStageConfig = StageConfig.forStage(1);
        this.tower = new Tower(Constants.TOWER_MAX_HP, 40, Constants.MONSTER_LANE_Y - 90);
        this.belt = new ConveyorBelt();
        this.cauldron = new Cauldron(160, Constants.ZONE_BOT_Y + 15);
        this.potionRack = new PotionRack(355, Constants.ZONE_BOT_Y + 25);
        this.waveManager = new WaveManager(currentStageConfig);
        startStage(1);
    }

    /**
     * เริ่มเล่นในด่านที่กำหนด พร้อมดึงค่าความสามารถที่อัปเกรดมาใช้งาน
     */
    public void startStage(int stageNumber) {
        this.currentStage = stageNumber;
        this.currentStageConfig = StageConfig.forStage(stageNumber);

        ProfileManager profile = ProfileManager.getInstance();

        // นำค่าอัปเกรดมาใช้
        tower.setMaxHp(Constants.TOWER_MAX_HP + profile.getBonusTowerHp());
        tower.reset();

        belt.setAllowedIngredients(currentStageConfig.getAllowedIngredients());
        belt.setRespawnDuration(profile.getBeltRespawnDuration());
        belt.reset();

        cauldron.setExplosionDuration(profile.getCauldronExplosionDuration());
        cauldron.clear();

        Potion.setGlobalRadiusMultiplier(profile.getPotionRadiusMultiplier());
        potionRack.clear();

        activeMonsters.clear();
        activeProjectiles.clear();

        waveManager.setStage(currentStageConfig);

        score = 0;
        comboCount = 0;
        comboTimer = 0.0;
        earnedStars = 0;
        state = GameState.PLAYING;
        recipeBookOpen = false;
    }

    /**
     * คำนวณจำนวนดาวที่ได้รับ (1 - 3 ดาว) ตามพลังชีวิตหอคอยที่เหลือ
     */
    public int calculateStarsEarned() {
        if (tower.isDead()) return 0;
        double hpRatio = (double) tower.getHp() / tower.getMaxHp();
        if (hpRatio >= 0.999) return 3; // เลือดเต็ม 100% ได้ 3 ดาวสมบูรณ์แบบ
        if (hpRatio >= 0.50) return 2;  // เลือดเหลือเกิน 50% ได้ 2 ดาว
        return 1;                      // เอาตัวรอดได้ ได้ 1 ดาว
    }

    /**
     * อัปเดตลอจิกเกมทุกเฟรม (Game Loop Tick)
     */
    public void update(double dt) {
        if (state != GameState.PLAYING || recipeBookOpen) {
            return;
        }

        // อัปเดต Combo Timer
        if (comboTimer > 0) {
            comboTimer -= dt;
            if (comboTimer <= 0) {
                comboCount = 0;
            }
        }

        // 1. อัปเดตสายพานและหม้อต้ม
        belt.update(dt);
        cauldron.update(dt);

        // 2. อัปเดตกระสุนขวดยาที่ลอยอยู่
        for (Projectile p : activeProjectiles) {
            p.update(dt, activeMonsters);
        }
        activeProjectiles.removeIf(Projectile::isDead);

        // 3. อัปเดตมอนสเตอร์ในฉาก
        for (Monster m : activeMonsters) {
            m.update(dt, tower);
        }

        // กำจัดมอนสเตอร์ที่ตายและเพิ่มคะแนนตาม Combo
        activeMonsters.removeIf(m -> {
            if (m.isDead()) {
                comboCount++;
                comboTimer = maxComboTime;
                int bonusMultiplier = Math.min(5, comboCount);
                score += 100 * bonusMultiplier;
                if (comboCount >= 2) {
                    SoundManager.getInstance().playCombo(comboCount);
                }
                return true;
            }
            return false;
        });

        // 4. อัปเดต Wave Manager
        waveManager.update(dt, activeMonsters);

        // 5. ตรวจสอบการเตือนภัยเมื่อเลือดหอคอยต่ำกว่า 35% (Pulsing Heartbeat)
        double hpRatio = (double) tower.getHp() / tower.getMaxHp();
        if (state == GameState.PLAYING && hpRatio < 0.35 && !tower.isDead()) {
            SoundManager.getInstance().updateLowHpAlert(true);
        } else {
            SoundManager.getInstance().updateLowHpAlert(false);
        }

        // 6. ตรวจสอบเงื่อนไขแพ้ / ชนะ
        if (tower.isDead()) {
            state = GameState.GAME_OVER;
            SoundManager.getInstance().updateLowHpAlert(false);
            SoundManager.getInstance().playDefeat();
        } else if (waveManager.isAllCompleted() && activeMonsters.isEmpty()) {
            state = GameState.VICTORY;
            SoundManager.getInstance().updateLowHpAlert(false);
            earnedStars = calculateStarsEarned();
            ProfileManager.getInstance().recordStageClear(currentStage, earnedStars);
            SoundManager.getInstance().playVictory();
        }
    }

    /**
     * ผู้เล่นกดปุ่มตัวเลข 1 - 5 ดึงวัตถุดิบจากสายพานลงหม้อต้ม
     */
    public void grabIngredientToCauldron(int slotIndex) {
        if (state != GameState.PLAYING || recipeBookOpen) return;
        IngredientType item = belt.grab(slotIndex);
        if (item != null) {
            cauldron.addIngredient(item);
        }
    }

    /**
     * ผู้เล่นกด Spacebar เพื่อต้มผสมยาในหม้อ
     */
    public void brewPotion() {
        if (state != GameState.PLAYING || recipeBookOpen) return;
        Potion potion = cauldron.brew();
        if (potion != null) {
            potionRack.addPotion(potion);
        }
    }

    /**
     * ปาขวดยาที่เลือกไว้ไปยังพิกัดเป้าหมาย (targetX, targetY)
     */
    public boolean throwPotionAt(double targetX, double targetY) {
        if (state != GameState.PLAYING || recipeBookOpen) return false;
        Potion selected = potionRack.consumeSelected();
        if (selected != null) {
            double startX = potionRack.getX() + 100;
            double startY = potionRack.getY();
            activeProjectiles.add(new Projectile(selected, startX, startY, targetX, targetY));
            SoundManager.getInstance().playThrow();
            return true;
        }
        return false;
    }

    /**
     * เริ่มเล่นใหม่ในด่านเดิม (Restart Current Stage)
     */
    public void resetGame() {
        startStage(currentStage);
    }

    // Getters and Setters
    public Tower getTower() { return tower; }
    public ConveyorBelt getBelt() { return belt; }
    public Cauldron getCauldron() { return cauldron; }
    public PotionRack getPotionRack() { return potionRack; }
    public WaveManager getWaveManager() { return waveManager; }
    public List<Monster> getActiveMonsters() { return activeMonsters; }
    public List<Projectile> getActiveProjectiles() { return activeProjectiles; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getScore() { return score; }
    public int getComboCount() { return comboCount; }
    public double getComboTimer() { return comboTimer; }
    public boolean isRecipeBookOpen() { return recipeBookOpen; }
    public void setRecipeBookOpen(boolean open) { this.recipeBookOpen = open; }
    public void toggleRecipeBook() { this.recipeBookOpen = !this.recipeBookOpen; }

    public int getCurrentStage() { return currentStage; }
    public StageConfig getCurrentStageConfig() { return currentStageConfig; }
    public int getEarnedStars() { return earnedStars; }
}
