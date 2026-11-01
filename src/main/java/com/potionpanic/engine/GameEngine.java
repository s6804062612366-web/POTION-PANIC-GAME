package com.potionpanic.engine;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Projectile;
import com.potionpanic.entity.Tower;
import com.potionpanic.entity.VisualEffect;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.item.Cauldron;
import com.potionpanic.item.ConveyorBelt;
import com.potionpanic.item.IngredientType;
import com.potionpanic.item.PotionRack;
import com.potionpanic.util.Constants;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * เอนจินหลักควบคุมลอจิกและสถานะทั้งหมดของเกม (Game Engine)
 * จัดการระบบด่าน (Stage Progression), การอัปเกรดเวทมนตร์, Combo Streak, การประเมินดาว (1-3 Stars)
 * และจัดการวิชวลเอฟเฟกต์ (Visual Effects) บนสนามรบ
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
    private final List<VisualEffect> activeEffects = new ArrayList<>();

    private GameState state = GameState.PLAYING;
    private int score = 0;
    private boolean recipeBookOpen = false;

    // ระบบหน้าจอสั่นสะเทือน (Screen Shake)
    private double screenShakeTimer = 0.0;
    private double screenShakeIntensity = 0.0;
    private int lastReportedWave = 0;

    // ระบบ Stage & Progression
    private int currentStage = 1;
    private StageConfig currentStageConfig;
    private int earnedStars = 0;
    private int newStarsGained = 0;
    private int endlessStarsEarned = 0;

    // ระบบ Combo Streak
    private int comboCount = 0;
    private double comboTimer = 0.0;
    private final double maxComboTime = 4.0;

    public GameEngine() {
        this.currentStageConfig = StageConfig.forStage(1);
        this.tower = new Tower(Constants.TOWER_MAX_HP, 40, Constants.MONSTER_LANE_Y - 90);
        this.belt = new ConveyorBelt();
        this.cauldron = new Cauldron(115, Constants.ZONE_BOT_Y + 22);
        this.potionRack = new PotionRack(335, Constants.ZONE_BOT_Y + 25);
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
        activeEffects.clear();

        waveManager.setStage(currentStageConfig);
        belt.rerollAll(activeMonsters, cauldron.getIngredients(), waveManager.getUpcomingWeaknesses());

        score = 0;
        comboCount = 0;
        comboTimer = 0.0;
        earnedStars = 0;
        state = GameState.PLAYING;
        recipeBookOpen = false;
        lastReportedWave = 0;
        screenShakeTimer = 0.0;

        // เริ่มเพลงศึกต่อสู้
        SoundManager.getInstance().playBattleBgm();
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

        // อัปเดต Screen Shake
        if (screenShakeTimer > 0) {
            screenShakeTimer -= dt;
            if (screenShakeTimer <= 0) {
                screenShakeIntensity = 0.0;
            }
        }

        // อัปเดต Combo Timer
        if (comboTimer > 0) {
            comboTimer -= dt;
            if (comboTimer <= 0) {
                comboCount = 0;
            }
        }

        // 1. อัปเดตสายพานและหม้อต้ม (ส่งบริบทมอนสเตอร์ในสนามเพื่อการสุ่มอัจฉริยะ)
        belt.update(dt, activeMonsters, cauldron.getIngredients(), waveManager.getUpcomingWeaknesses());
        cauldron.update(dt);

        // 2. อัปเดตกระสุนขวดยาที่ลอยอยู่ (ส่ง activeEffects เข้าไปด้วย)
        for (Projectile p : activeProjectiles) {
            p.update(dt, activeMonsters, activeEffects);
        }
        activeProjectiles.removeIf(Projectile::isDead);

        // 3. อัปเดตวิชวลเอฟเฟกต์ (วงระเบิด, บ่อน้ำยา, สายฟ้าชิ่ง)
        for (VisualEffect eff : activeEffects) {
            eff.update(dt);
        }
        activeEffects.removeIf(VisualEffect::isExpired);

        // 4. อัปเดตมอนสเตอร์ในฉาก
        int oldTowerHp = tower.getHp();
        for (Monster m : activeMonsters) {
            m.update(dt, tower);
        }
        if (tower.getHp() < oldTowerHp) {
            double currentHpRatio = (double) tower.getHp() / tower.getMaxHp();
            // สั่นเตือนเฉพาะตอนหอคอยกำลังจะพัง (เลือดต่ำกว่า 35%)
            if (currentHpRatio <= 0.35) {
                triggerScreenShake(5.0, 0.25);
            }
        }

        // เรียงลำดับมอนสเตอร์ตามแกน Y (Depth Sorting: ตัวอยู่บนวาดก่อน ตัวอยู่ล่างวาดทับ)
        activeMonsters.sort(Comparator.comparingDouble(Monster::getY));

        // กำจัดมอนสเตอร์ที่ตายและเพิ่มคะแนนตาม Combo
        activeMonsters.removeIf(m -> {
            if (m.isDead()) {
                // เอฟเฟกต์แสงสลายตัวของมอนสเตอร์
                activeEffects.add(VisualEffect.createDeathBurst(m.getX() + m.getWidth() / 2.0, m.getY() + m.getHeight() / 2.0, m.getWeakness().getColor()));

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

        // 5. อัปเดต Wave Manager
        int oldWave = waveManager.getCurrentWave();
        waveManager.update(dt, activeMonsters);
        int newWave = waveManager.getCurrentWave();
        if (newWave != lastReportedWave && newWave > 0) {
            lastReportedWave = newWave;
            String sub = currentStageConfig.isEndless() ? "โหมดเอาชีวิตรอดไม่รู้จบ!" : ("เวฟที่ " + newWave + " จาก " + waveManager.getTotalWaves());
            activeEffects.add(VisualEffect.createWaveBanner("WAVE " + newWave + " START!", sub));
        }

        // 6. ตรวจสอบการเตือนภัยเมื่อเลือดหอคอยต่ำกว่า 35%
        double hpRatio = (double) tower.getHp() / tower.getMaxHp();
        if (state == GameState.PLAYING && hpRatio < 0.35 && !tower.isDead()) {
            SoundManager.getInstance().updateLowHpAlert(true);
        } else {
            SoundManager.getInstance().updateLowHpAlert(false);
        }

        // 7. ตรวจสอบเงื่อนไขแพ้ / ชนะ
        if (tower.isDead()) {
            state = GameState.GAME_OVER;
            triggerScreenShake(7.5, 0.40);
            SoundManager.getInstance().updateLowHpAlert(false);
            SoundManager.getInstance().stopBgm();
            SoundManager.getInstance().playDefeat();
            if (currentStageConfig.isEndless()) {
                int wavesCleared = Math.max(0, waveManager.getCurrentWave() - 1);
                endlessStarsEarned = ProfileManager.getInstance().recordEndlessProgress(wavesCleared);
            }
        } else if (waveManager.isAllCompleted() && activeMonsters.isEmpty()) {
            state = GameState.VICTORY;
            SoundManager.getInstance().updateLowHpAlert(false);
            SoundManager.getInstance().stopBgm();
            earnedStars = calculateStarsEarned();
            newStarsGained = ProfileManager.getInstance().recordStageClear(currentStage, earnedStars);
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

            // เอฟเฟกต์วัตถุดิบลอยโค้งจากสายพานลงหม้อต้ม
            double startX = Constants.BELT_START_X + slotIndex * (Constants.BELT_SLOT_SIZE + Constants.BELT_SLOT_GAP) + 38;
            double startY = Constants.ZONE_MID_Y + 50;
            double targetX = cauldron.getX() + 70;
            double targetY = cauldron.getY() + 45;
            activeEffects.add(VisualEffect.createFlyingItem(item.getImage(), startX, startY, targetX, targetY));
        }
    }

    /**
     * สุ่มวัตถุดิบใหม่ทั้งสายพาน (Reroll กด [F])
     */
    public boolean rerollBelt() {
        if (state != GameState.PLAYING || recipeBookOpen) return false;
        return belt.rerollAll(activeMonsters, cauldron.getIngredients(), waveManager.getUpcomingWeaknesses());
    }

    /**
     * ผู้เล่นกด Spacebar เพื่อต้มผสมยาในหม้อ
     */
    public void brewPotion() {
        if (state != GameState.PLAYING || recipeBookOpen) return;
        Potion potion = cauldron.brew();
        if (potion != null) {
            // ตรวจสอบสภาวะคับขัน (เลือดหอคอย <= 50% หรือ มอนสเตอร์ >= 3 ตัว หรือ มีบอส)
            boolean isClutch = (tower.getHp() <= tower.getMaxHp() * 0.5) ||
                               (activeMonsters.size() >= 3) ||
                               activeMonsters.stream().anyMatch(m -> m instanceof com.potionpanic.entity.monster.ChaosBoss);

            // สุ่มโอกาสเกิดยาระเบิดมหาเวท (Miracle Brew - RARE!)
            if (com.potionpanic.item.RecipeBook.shouldTriggerMiracleExplosive(isClutch)) {
                potion = new com.potionpanic.entity.potion.ExplosivePotion();
                SoundManager.getInstance().playCritical();
                activeEffects.add(VisualEffect.createWaveBanner("MIRACLE BREW!", "แจ็กพอต! สุ่มได้ยาระเบิดมหาเวท (RARE)"));
            }

            potionRack.addPotion(potion);

            // เอฟเฟกต์ขวดยาลอยจากหม้อต้มเข้าสู่ชั้นวางยา
            double startX = cauldron.getX() + 70;
            double startY = cauldron.getY() + 40;
            double targetX = potionRack.getX() + 80;
            double targetY = potionRack.getY() + 45;
            activeEffects.add(VisualEffect.createFlyingItem(potion.getImage(), startX, startY, targetX, targetY));
        }
    }

    public void triggerScreenShake(double intensity, double duration) {
        this.screenShakeIntensity = intensity;
        this.screenShakeTimer = duration;
    }

    public Point getScreenShakeOffset() {
        if (screenShakeTimer <= 0) return new Point(0, 0);
        int ox = (int) ((Math.random() - 0.5) * screenShakeIntensity * 2.0);
        int oy = (int) ((Math.random() - 0.5) * screenShakeIntensity * 2.0);
        return new Point(ox, oy);
    }

    public double getScreenShakeTimer() {
        return screenShakeTimer;
    }

    public double getScreenShakeIntensity() {
        return screenShakeIntensity;
    }

    /**
     * ปาขวดยาที่เลือกไว้ไปยังพิกัดเป้าหมาย (targetX, targetY)
     */
    public boolean throwPotionAt(double targetX, double targetY) {
        if (state != GameState.PLAYING || recipeBookOpen) return false;
        int currentIdx = potionRack.getSelectedIndex();
        Potion selected = potionRack.consumeSelected();
        if (selected != null) {
            int slotIdx = (currentIdx >= 0) ? currentIdx : 0;
            Point center = potionRack.getSlotCenter(slotIdx);
            double startX = center.getX();
            double startY = center.getY();
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
    public List<VisualEffect> getActiveEffects() { return activeEffects; }
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
    public int getNewStarsGained() { return newStarsGained; }
    public int getEndlessStarsEarned() { return endlessStarsEarned; }
}
