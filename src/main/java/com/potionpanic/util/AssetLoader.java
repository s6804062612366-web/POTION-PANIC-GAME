package com.potionpanic.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * จัดการโหลดทรัพยากรภาพในเกม Potion Panic
 * รองรับโหมด Handcrafted Java2D Vector Art เป็นค่าเริ่มต้น (Unified Classic Fantasy Style)
 * และรองรับการโหลดไฟล์ภาพภายนอกพร้อมระบบ Fallback อัตโนมัติ
 */
public class AssetLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    // ค่าเริ่มต้นใช้ไฟล์รูปภาพ PNG คุณภาพสูงจาก resources/images
    // หากไม่พบไฟล์ จะสลับไปใช้ VectorArtRenderer วาดชดเชยอัตโนมัติ (Fallback)
    public static boolean USE_PROCEDURAL_VECTOR_ART = false;

    public static BufferedImage getImage(String name) {
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }

        BufferedImage img = null;

        // หากเปิดโหมด Procedural Vector Art จะวาดภาพขึ้นสดๆ ด้วย Java2D
        if (USE_PROCEDURAL_VECTOR_ART) {
            img = generateVectorImage(name);
        } else {
            // โหมดโหลดไฟล์รูปภาพภายนอก
            try {
                String path = "/images/" + name + ".png";
                InputStream is = AssetLoader.class.getResourceAsStream(path);
                if (is != null) {
                    img = ImageIO.read(is);
                    if (img != null && !name.startsWith("bg_")) {
                        img = ensureTransparentBackground(img);
                    }
                }
            } catch (Exception ignored) {
            }

            if (img == null) {
                img = generateVectorImage(name);
            }
        }

        imageCache.put(name, img);
        return img;
    }

    /**
     * สร้างภาพเวกเตอร์ด้วย VectorArtRenderer ตามขนาดและอัตราส่วนที่เหมาะสม
     */
    private static BufferedImage generateVectorImage(String name) {
        int width = 128;
        int height = 128;

        if ("cauldron".equals(name)) {
            width = 160;
            height = 160;
        } else if ("tower".equals(name)) {
            width = 180;
            height = 240;
        } else if ("monster_chaos_boss".equals(name)) {
            width = 160;
            height = 160;
        }

        return VectorArtRenderer.createProceduralImage(name, width, height);
    }

    /**
     * ตรวจสอบว่าภาพไอเทม/ตัวละครมีพื้นหลังสีขาวทึบหรือไม่ หากมีจะทำการตัดสีขาวรอบนอกออกเป็นโปร่งใส
     */
    private static BufferedImage ensureTransparentBackground(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();

        // ตรวจสอบมุมทั้ง 4
        int[] corners = {
                src.getRGB(0, 0),
                src.getRGB(w - 1, 0),
                src.getRGB(0, h - 1),
                src.getRGB(w - 1, h - 1)
        };

        boolean hasSolidWhiteCorner = false;
        for (int c : corners) {
            int a = (c >> 24) & 0xff;
            int r = (c >> 16) & 0xff;
            int g = (c >> 8) & 0xff;
            int b = c & 0xff;
            if (a > 200 && r > 240 && g > 240 && b > 240) {
                hasSolidWhiteCorner = true;
                break;
            }
        }

        if (!hasSolidWhiteCorner) {
            return src; // ภาพมีขอบโปร่งใสอยู่แล้ว
        }

        // แปลงภาพเป็น TYPE_INT_ARGB และทำ Flood Fill ลบสีขาวรอบนอกออก
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = result.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();

        boolean[][] visited = new boolean[w][h];
        java.util.ArrayDeque<java.awt.Point> queue = new java.util.ArrayDeque<>();

        int[][] cornerCoords = {{0, 0}, {w - 1, 0}, {0, h - 1}, {w - 1, h - 1}};
        for (int[] pt : cornerCoords) {
            int rgb = result.getRGB(pt[0], pt[1]);
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            if (r > 235 && g > 235 && b > 235) {
                queue.add(new java.awt.Point(pt[0], pt[1]));
                visited[pt[0]][pt[1]] = true;
            }
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            java.awt.Point p = queue.poll();
            result.setRGB(p.x, p.y, 0x00000000); // โปร่งใส 100%

            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];

                if (nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    int rgb = result.getRGB(nx, ny);
                    int a = (rgb >> 24) & 0xff;
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    // ถ้าสีเกือบขาว
                    if (a > 30 && r > 230 && g > 230 && b > 230) {
                        queue.add(new java.awt.Point(nx, ny));
                    }
                }
            }
        }

        return result;
    }

    /**
     * ล้าง Cache รูปภาพเพื่อเรนเดอร์ใหม่ (เช่น เมื่อสลับโหมดภาพ)
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
