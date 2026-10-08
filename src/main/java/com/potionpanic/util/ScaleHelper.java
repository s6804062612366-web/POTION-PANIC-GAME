package com.potionpanic.util;

import java.awt.Point;

/**
 * ตัวช่วยคำนวณมาตราส่วนหน้าจอ (Screen Scale Helper)
 * รองรับการขยายหน้าจอเต็มจอ (Fullscreen / Maximize) โดยคงสัดส่วน 16:9 ไว้อย่างสมบูรณ์แบบ
 */
public class ScaleHelper {

    public static double getScale(int screenW, int screenH) {
        return Math.min((double) screenW / Constants.WINDOW_WIDTH, (double) screenH / Constants.WINDOW_HEIGHT);
    }

    public static int getOffsetX(int screenW, int screenH, double scale) {
        return (int) Math.round((screenW - Constants.WINDOW_WIDTH * scale) / 2.0);
    }

    public static int getOffsetY(int screenW, int screenH, double scale) {
        return (int) Math.round((screenH - Constants.WINDOW_HEIGHT * scale) / 2.0);
    }

    public static Point toVirtual(int screenX, int screenY, int screenW, int screenH) {
        double scale = getScale(screenW, screenH);
        if (scale <= 0) scale = 1.0;
        int ox = getOffsetX(screenW, screenH, scale);
        int oy = getOffsetY(screenW, screenH, scale);
        int vx = (int) Math.round((screenX - ox) / scale);
        int vy = (int) Math.round((screenY - oy) / scale);
        return new Point(vx, vy);
    }
}
