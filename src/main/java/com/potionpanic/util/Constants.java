package com.potionpanic.util;

import java.awt.Color;
import java.awt.Font;

/**
 * รวมค่าคงที่และการตั้งค่าหลักของเกม Potion Panic
 */
public final class Constants {
    private Constants() {} // ป้องกันการสร้าง Instance

    // มิติหน้าจอ
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String GAME_TITLE = "Potion Panic - อลวนคนปรุงยา";
    public static final int TARGET_FPS = 60;

    // การแบ่งโซนหน้าจอ Gameplay (3 โซน)
    // โซน 1: มอนสเตอร์บุกและหอคอย (ด้านบน)
    public static final int ZONE_TOP_Y = 0;
    public static final int ZONE_TOP_HEIGHT = 280;
    public static final int MONSTER_LANE_Y = 190;
    public static final int TOWER_X = 140;
    public static final int TOWER_MAX_HP = 100;

    // โซน 2: สายพานลำเลียงวัตถุดิบ (ตรงกลาง)
    public static final int ZONE_MID_Y = 280;
    public static final int ZONE_MID_HEIGHT = 160;
    public static final int BELT_SLOTS_COUNT = 5;
    public static final int BELT_START_X = 260;
    public static final int BELT_SLOT_SIZE = 90;
    public static final int BELT_SLOT_GAP = 40;

    // โซน 3: โต๊ะปรุงยา, หม้อต้ม และชั้นวางยา (ด้านล่าง)
    public static final int ZONE_BOT_Y = 440;
    public static final int ZONE_BOT_HEIGHT = 280;
    public static final int CAULDRON_MAX_ITEMS = 2; // ปรับเหลือ 2 ช่องตามคำขอของผู้ใช้ (สูตรยาใช้ 2 วัตถุดิบ)
    public static final int POTION_RACK_CAPACITY = 4;

    // สีธีมเกม
    public static final Color COLOR_BG_DARK = new Color(20, 18, 38);
    public static final Color COLOR_ACCENT_GOLD = new Color(255, 204, 51);
    public static final Color COLOR_WOOD_DARK = new Color(60, 42, 33);
    public static final Color COLOR_WOOD_LIGHT = new Color(133, 90, 61);
    public static final Color COLOR_PANEL_OVERLAY = new Color(15, 12, 30, 220);

    // แบบอักษรมาตรฐานระดับพรีเมียม (Bai Jamjuree / Sukhumvit Set)
    public static final Font FONT_TITLE = FontHelper.bold(34);
    public static final Font FONT_SUBTITLE = FontHelper.bold(20);
    public static final Font FONT_REGULAR = FontHelper.plain(15);
    public static final Font FONT_BOLD = FontHelper.bold(15);
    public static final Font FONT_SMALL = FontHelper.bold(12);
}
