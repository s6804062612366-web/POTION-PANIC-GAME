package com.potionpanic.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * จัดการข้อมูลความคืบหน้าของผู้เล่น (Profile & Save State)
 * บันทึกการปลดล็อกด่าน, จำนวนดาวที่ได้, แต้มดาวที่ใช้ได้, และระดับการอัปเกรดสกิล
 */
public class ProfileManager {
    private static ProfileManager instance;

    public enum UpgradeType {
        TOWER_HP("หอคอยทนทาน", "เพิ่มพลังชีวิตสูงสุดของหอคอย +30 HP ต่อขั้น"),
        CONVEYOR_SPEED("สายพานรวดเร็ว", "เร่งความเร็วในการเสกวัตถุดิบชิ้นใหม่มาแทนที่"),
        POTION_RADIUS("ระเบิดวงกว้าง", "เพิ่มรัศมีการระเบิดของยาทุกชนิด +15% ต่อขั้น"),
        CAULDRON_RECOVERY("ฟื้นฟูหม้อไว", "ลดระยะเวลาคูลดาวน์เมื่อหม้อต้มระเบิดจากการใส่สูตรผิด");

        private final String name;
        private final String description;

        UpgradeType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    private int unlockedStage = 1; // 1, 2, 3 หรือ 4 (Endless Mode)
    private final Map<Integer, Integer> stageStars = new HashMap<>(); // ด่าน 1-3 -> ดาว 0-3
    private int starPoints = 0; // ดาวที่มีให้ใช้ซื้ออัปเกรด
    private int totalStarsEarned = 0;

    // ระดับการอัปเกรดสกิล (0 ถึง 3)
    private int towerHpLevel = 0;
    private int conveyorSpeedLevel = 0;
    private int potionRadiusLevel = 0;
    private int cauldronRecoveryLevel = 0;

    public static final int MAX_UPGRADE_LEVEL = 3;
    public static final int UPGRADE_COST = 1; // ใช้ 1 ดาวต่อ 1 ขั้น

    private ProfileManager() {
        // เริ่มต้นมีด่าน 1 ปลดล็อก ดาวเริ่มต้น 0
        stageStars.put(1, 0);
        stageStars.put(2, 0);
        stageStars.put(3, 0);
    }

    public static synchronized ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    /**
     * บันทึกผลเมื่อผ่านด่าน และคำนวณดาวใหม่ที่ได้รับ
     * @return จำนวนดาวใหม่ที่ได้รับเพิ่มในรอบนี้
     */
    public int recordStageClear(int stage, int stars) {
        int oldStars = stageStars.getOrDefault(stage, 0);
        int newStarsGained = 0;

        if (stars > oldStars) {
            newStarsGained = stars - oldStars;
            stageStars.put(stage, stars);
            starPoints += newStarsGained;
            totalStarsEarned += newStarsGained;
        }

        // ปลดล็อกด่านถัดไป
        if (stage >= unlockedStage && stage < 4) {
            unlockedStage = stage + 1;
        }

        return newStarsGained;
    }

    public boolean canUpgrade(UpgradeType type) {
        if (starPoints < UPGRADE_COST) return false;
        return getUpgradeLevel(type) < MAX_UPGRADE_LEVEL;
    }

    public boolean buyUpgrade(UpgradeType type) {
        if (!canUpgrade(type)) return false;

        starPoints -= UPGRADE_COST;
        switch (type) {
            case TOWER_HP -> towerHpLevel++;
            case CONVEYOR_SPEED -> conveyorSpeedLevel++;
            case POTION_RADIUS -> potionRadiusLevel++;
            case CAULDRON_RECOVERY -> cauldronRecoveryLevel++;
        }
        return true;
    }

    public int getUpgradeLevel(UpgradeType type) {
        return switch (type) {
            case TOWER_HP -> towerHpLevel;
            case CONVEYOR_SPEED -> conveyorSpeedLevel;
            case POTION_RADIUS -> potionRadiusLevel;
            case CAULDRON_RECOVERY -> cauldronRecoveryLevel;
        };
    }

    // --- ค่าตัวคูณและสเตตัสที่ได้จากอัปเกรด ---

    /** เลือดเพิ่มจากระดับอัปเกรด (+30 ต่อเลเวล) */
    public int getBonusTowerHp() {
        return towerHpLevel * 30;
    }

    /** เวลาเสกวัตถุดิบทดแทน (ปกติ 0.60 วิ -> 0.48 -> 0.36 -> 0.24 วิ) */
    public double getBeltRespawnDuration() {
        return Math.max(0.20, 0.60 - (conveyorSpeedLevel * 0.12));
    }

    /** ตัวคูณรัศมีระเบิดของยา (1.0 -> 1.15 -> 1.30 -> 1.45 เท่า) */
    public double getPotionRadiusMultiplier() {
        return 1.0 + (potionRadiusLevel * 0.15);
    }

    /** เวลาคูลดาวน์หม้อต้มเมื่อระเบิด (ปกติ 2.0 วิ -> 1.5 -> 1.0 -> 0.6 วิ) */
    public double getCauldronExplosionDuration() {
        return Math.max(0.5, 2.0 - (cauldronRecoveryLevel * 0.48));
    }

    // Getters
    public int getUnlockedStage() { return unlockedStage; }
    public boolean isStageUnlocked(int stage) { return stage <= unlockedStage; }
    public int getStageStars(int stage) { return stageStars.getOrDefault(stage, 0); }
    public int getStarPoints() { return starPoints; }
    public int getTotalStarsEarned() { return totalStarsEarned; }
}
