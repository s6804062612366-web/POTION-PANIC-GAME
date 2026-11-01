package com.potionpanic.engine;

import com.potionpanic.entity.monster.*;
import com.potionpanic.util.Constants;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * จัดการรอบการบุกของมอนสเตอร์ (Wave Manager)
 * ปรับตาม StageConfig: รองรับ Stage 1, Stage 2, Stage 3 (พร้อมบอสใหญ่) และ Endless Mode
 * มีระบบกระจายพิกัดแกน Y ป้องกันมอนสเตอร์เดินซ้อนทับกัน
 */
public class WaveManager {
    private StageConfig stageConfig;
    private int currentWave = 1;
    private int totalWaves = 3;
    private final Queue<Monster> spawnQueue = new ArrayDeque<>();
    private double spawnTimer = 0.0;
    private double spawnInterval = 3.0;
    private double waveTransitionTimer = 0.0;
    private boolean waveInProgress = false;
    private boolean allCompleted = false;
    private final Random rand = new Random();

    public WaveManager() {
        this(StageConfig.forStage(1));
    }

    public WaveManager(StageConfig stageConfig) {
        setStage(stageConfig);
    }

    public void setStage(StageConfig stageConfig) {
        this.stageConfig = stageConfig;
        this.totalWaves = stageConfig.getTotalWaves();
        reset();
    }

    public void startWave(int waveNumber) {
        this.currentWave = waveNumber;
        this.spawnQueue.clear();
        this.waveInProgress = true;
        this.spawnTimer = 1.0;

        double baseLaneY = Constants.MONSTER_LANE_Y - 20;
        double startX = Constants.WINDOW_WIDTH + 20;

        if (stageConfig.isEndless()) {
            buildEndlessWave(waveNumber, startX, baseLaneY);
        } else {
            switch (stageConfig.getStageNumber()) {
                case 1 -> buildStage1Wave(waveNumber, startX, baseLaneY);
                case 2 -> buildStage2Wave(waveNumber, startX, baseLaneY);
                case 3 -> buildStage3Wave(waveNumber, startX, baseLaneY);
                default -> buildStage1Wave(waveNumber, startX, baseLaneY);
            }
        }
    }

    private double staggerY(double baseLaneY) {
        // สุ่มเหลื่อมพิกัด Y บน-ล่าง เล็กน้อย เพื่อไม่ให้มอนสเตอร์เดินซ้อนทับกัน
        return baseLaneY + (rand.nextDouble() - 0.5) * 26.0;
    }

    private void buildStage1Wave(int wave, double startX, double laneY) {
        if (wave == 1) {
            spawnInterval = 3.5;
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
        } else {
            spawnInterval = 2.8;
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
            spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
        }
    }

    private void buildStage2Wave(int wave, double startX, double laneY) {
        switch (wave) {
            case 1 -> {
                spawnInterval = 3.0;
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
            }
            case 2 -> {
                spawnInterval = 2.6;
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
            }
            case 3 -> {
                spawnInterval = 2.2;
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
            }
        }
    }

    private void buildStage3Wave(int wave, double startX, double laneY) {
        switch (wave) {
            case 1 -> {
                spawnInterval = 2.7;
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
            }
            case 2 -> {
                spawnInterval = 2.2;
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
            }
            case 3 -> {
                // Wave สุดท้ายของด่าน 3: กองทัพมอนสเตอร์ + บอสใหญ่ ราชันย์อสูรเวท (Chaos Overlord)
                spawnInterval = 2.0;
                spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
                spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                // บอสใหญ่ประจำด่านที่ 3!
                spawnQueue.add(new ChaosBoss(startX + 40, laneY - 10));
            }
        }
    }

    private void buildEndlessWave(int wave, double startX, double laneY) {
        int count = 6 + (wave * 2);
        spawnInterval = Math.max(1.1, 3.0 - (wave * 0.12));

        for (int i = 0; i < count; i++) {
            int roll = rand.nextInt(4);
            switch (roll) {
                case 0 -> spawnQueue.add(new PlantCreeper(startX, staggerY(laneY)));
                case 1 -> spawnQueue.add(new FireGolem(startX, staggerY(laneY)));
                case 2 -> spawnQueue.add(new ArmoredBeast(startX, staggerY(laneY)));
                case 3 -> spawnQueue.add(new SwiftImp(startX, staggerY(laneY)));
            }
        }

        // ในโหมด Endless ทุกๆ 3 เวฟ จะมี Chaos Boss ปรากฏตัว
        if (wave % 3 == 0) {
            spawnQueue.add(new ChaosBoss(startX + 50, laneY - 10));
        }
    }

    public void update(double dt, List<Monster> activeMonsters) {
        if (allCompleted) return;

        // สปอว์นมอนสเตอร์ตามคิว
        if (!spawnQueue.isEmpty()) {
            spawnTimer += dt;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                Monster m = spawnQueue.poll();
                if (m != null) {
                    activeMonsters.add(m);
                }
            }
        } else {
            // เมื่อคิวปล่อยหมดแล้ว รอให้มอนสเตอร์ในฉากถูกกำจัดทั้งหมด
            if (activeMonsters.isEmpty() && waveInProgress) {
                waveInProgress = false;
                waveTransitionTimer = 3.0; // พัก 3 วินาทีก่อนขึ้น Wave ใหม่
            }
        }

        // จัดการขึ้น Wave ถัดไป
        if (!waveInProgress) {
            waveTransitionTimer -= dt;
            if (waveTransitionTimer <= 0) {
                if (stageConfig.isEndless()) {
                    startWave(currentWave + 1);
                } else {
                    if (currentWave < totalWaves) {
                        startWave(currentWave + 1);
                    } else {
                        allCompleted = true; // เคลียร์ครบทุก Wave ชนะเกม!
                    }
                }
            }
        }
    }

    public void reset() {
        currentWave = 1;
        allCompleted = false;
        waveInProgress = false;
        waveTransitionTimer = 0;
        startWave(1);
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    /**
     * ดึงรายการจุดอ่อนของมอนสเตอร์ที่กำลังรอคิวเกิดในเวฟนี้ (สูงสุด 3 ตัวแรก)
     * เพื่อให้ระบบสายพานเตรียมวัตถุดิบแก้ทางไว้ล่วงหน้า
     */
    public List<com.potionpanic.util.ElementType> getUpcomingWeaknesses() {
        List<com.potionpanic.util.ElementType> list = new java.util.ArrayList<>();
        for (Monster m : spawnQueue) {
            com.potionpanic.util.ElementType el = m.getWeakness();
            if (el != null && !list.contains(el)) {
                list.add(el);
            }
            if (list.size() >= 3) break;
        }
        return list;
    }

    public boolean isAllCompleted() {
        return allCompleted;
    }

    public int getRemainingInQueue() {
        return spawnQueue.size();
    }

    public StageConfig getStageConfig() {
        return stageConfig;
    }
}
