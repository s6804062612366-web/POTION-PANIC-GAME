package com.potionpanic.engine;

import com.potionpanic.entity.monster.*;
import com.potionpanic.util.Constants;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * จัดการรอบการบุกของมอนสเตอร์ (Wave Manager)
 * ปรับตาม StageConfig: รองรับ Stage 1, Stage 2, Stage 3 และ Endless Mode
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

        double laneY = Constants.MONSTER_LANE_Y - 20;
        double startX = Constants.WINDOW_WIDTH + 20;

        if (stageConfig.isEndless()) {
            buildEndlessWave(waveNumber, startX, laneY);
        } else {
            switch (stageConfig.getStageNumber()) {
                case 1 -> buildStage1Wave(waveNumber, startX, laneY);
                case 2 -> buildStage2Wave(waveNumber, startX, laneY);
                case 3 -> buildStage3Wave(waveNumber, startX, laneY);
                default -> buildStage1Wave(waveNumber, startX, laneY);
            }
        }
    }

    private void buildStage1Wave(int wave, double startX, double laneY) {
        // ด่าน 1: มีแค่อสูรพฤกษา (แพ้ไฟ) และ โกเลมไฟ (แพ้น้ำแข็ง) เพื่อให้ผู้เล่นใหม่จับทางได้ง่าย
        if (wave == 1) {
            spawnInterval = 3.5;
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new FireGolem(startX, laneY));
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new FireGolem(startX, laneY));
        } else {
            spawnInterval = 2.8;
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new FireGolem(startX, laneY));
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new FireGolem(startX, laneY));
            spawnQueue.add(new PlantCreeper(startX, laneY));
            spawnQueue.add(new FireGolem(startX, laneY));
        }
    }

    private void buildStage2Wave(int wave, double startX, double laneY) {
        // ด่าน 2: เพิ่มสัตว์ร้ายหุ้มเกราะ (แพ้กรดพิษ)
        switch (wave) {
            case 1 -> {
                spawnInterval = 3.0;
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
            }
            case 2 -> {
                spawnInterval = 2.6;
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
            }
            case 3 -> {
                spawnInterval = 2.2;
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
            }
        }
    }

    private void buildStage3Wave(int wave, double startX, double laneY) {
        // ด่าน 3: ครบทุกตัว + อิมป์ว่องไว + บอส
        switch (wave) {
            case 1 -> {
                spawnInterval = 2.7;
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
            }
            case 2 -> {
                spawnInterval = 2.2;
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
            }
            case 3 -> {
                spawnInterval = 1.8;
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new PlantCreeper(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
                spawnQueue.add(new SwiftImp(startX, laneY));
                spawnQueue.add(new ArmoredBeast(startX, laneY));
                spawnQueue.add(new FireGolem(startX, laneY));
            }
        }
    }

    private void buildEndlessWave(int wave, double startX, double laneY) {
        int count = 6 + (wave * 2);
        spawnInterval = Math.max(1.1, 3.0 - (wave * 0.12));

        for (int i = 0; i < count; i++) {
            int roll = rand.nextInt(4);
            switch (roll) {
                case 0 -> spawnQueue.add(new PlantCreeper(startX, laneY));
                case 1 -> spawnQueue.add(new FireGolem(startX, laneY));
                case 2 -> spawnQueue.add(new ArmoredBeast(startX, laneY));
                case 3 -> spawnQueue.add(new SwiftImp(startX, laneY));
            }
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
