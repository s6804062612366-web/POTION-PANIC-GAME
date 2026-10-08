package com.potionpanic.engine;

import com.potionpanic.item.IngredientType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * กำหนดค่าเฉพาะของแต่ละด่าน (Stage Configuration)
 * ควบคุมชนิดวัตถุดิบที่ใช้ได้, จำนวนเวฟ, และคำแนะนำของด่านนั้นๆ
 */
public class StageConfig {
    private final int stageNumber;
    private final String title;
    private final String subtitle;
    private final String description;
    private final List<IngredientType> allowedIngredients;
    private final int totalWaves;
    private final boolean endless;

    public StageConfig(int stageNumber, String title, String subtitle, String description,
                       List<IngredientType> allowedIngredients, int totalWaves, boolean endless) {
        this.stageNumber = stageNumber;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.allowedIngredients = Collections.unmodifiableList(allowedIngredients);
        this.totalWaves = totalWaves;
        this.endless = endless;
    }

    public static StageConfig forStage(int stageNumber) {
        return switch (stageNumber) {
            case 1 -> new StageConfig(
                    1,
                    "ด่านที่ 1: มือใหม่หัดปรุง (Apprentice)",
                    "ธาตุไฟ & น้ำแข็ง",
                    "เรียนรู้ความแตกต่างระหว่าง ยาเพลิง (ปราบอสูรพฤกษา) และ ยาน้ำแข็ง (ปราบโกเลมไฟ)",
                    Arrays.asList(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL),
                    2,
                    false
            );
            case 2 -> new StageConfig(
                    2,
                    "ด่านที่ 2: หมอกพิษและเกราะหิน (Acid & Armor)",
                    "เพิ่มเห็ดพิษ & สัตว์ร้ายเกราะหนา",
                    "เผชิญหน้ากับสัตว์ร้ายหุ้มเกราะที่ทนทาน ต้องใช้ ยากรดพิษ กัดกร่อนเกราะให้สลาย",
                    Arrays.asList(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, IngredientType.TOXIC_MUSHROOM),
                    3,
                    false
            );
            case 3 -> new StageConfig(
                    3,
                    "ด่านที่ 3: พายุคลั่งและหายนะ (Storm & Chaos)",
                    "ครบ 4 วัตถุดิบ & อิมป์ความเร็วสูง & บอสใหญ่",
                    "ทดสอบทักษะขั้นสูงสุด ปรุงยาสายฟ้าหยุดยั้งอิมป์ และผสมยาระเบิดทำลายล้างบอส!",
                    Arrays.asList(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, IngredientType.TOXIC_MUSHROOM, IngredientType.THUNDER_ROOT),
                    3,
                    false
            );
            case 4 -> new StageConfig(
                    4,
                    "โหมดท้าทาย: ไร้ขีดจำกัด (Endless Mode)",
                    "เอาชีวิตรอดจากคลื่นมอนสเตอร์ไม่รู้จบ",
                    "ใช้วัตถุดิบและยาทั้งหมดเพื่อสร้างสถิติคะแนนสูงสุด ท้าทายขีดจำกัดของคุณ!",
                    Arrays.asList(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, IngredientType.TOXIC_MUSHROOM, IngredientType.THUNDER_ROOT),
                    999,
                    true
            );
            default -> forStage(1);
        };
    }

    public int getStageNumber() { return stageNumber; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDescription() { return description; }
    public List<IngredientType> getAllowedIngredients() { return allowedIngredients; }
    public int getTotalWaves() { return totalWaves; }
    public boolean isEndless() { return endless; }

    public String getShortTitle() {
        return switch (stageNumber) {
            case 1 -> "ด่านที่ 1: มือใหม่หัดปรุง";
            case 2 -> "ด่านที่ 2: หมอกพิษและเกราะหิน";
            case 3 -> "ด่านที่ 3: พายุคลั่งและหายนะ";
            case 4 -> "โหมดท้าทาย: ไร้ขีดจำกัด";
            default -> "ด่านที่ " + stageNumber;
        };
    }
}
