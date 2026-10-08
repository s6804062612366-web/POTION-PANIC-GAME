package com.potionpanic.item;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Renderable;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * หม้อต้มยาเวทมนตร์ (Cauldron)
 * ใส่ได้สูงสุด 3 วัตถุดิบ และกดปุ่ม Spacebar เพื่อต้มผสมยา
 * มีระบบ Live Preview แสดงผลยาที่จะได้ก่อนต้ม, ปุ่ม Backspace เพื่อลบของ,
 * และระบบหม้อระเบิดติดคูลดาวน์ 2 วินาทีหากต้มสูตรผิด
 */
public class Cauldron implements Renderable {
    private final List<IngredientType> ingredients = new ArrayList<>();
    private final int x;
    private final int y;
    private final int size = 150;
    private double bubbleTime = 0.0;

    private double explosionTimer = 0.0;
    private double explosionDuration = 2.0;

    public Cauldron(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setExplosionDuration(double duration) {
        this.explosionDuration = Math.max(0.4, duration);
    }

    public boolean addIngredient(IngredientType item) {
        if (item == null || isExploded()) return false;
        if (ingredients.size() < Constants.CAULDRON_MAX_ITEMS) {
            ingredients.add(item);
            return true;
        }
        return false;
    }

    /**
     * ดึงวัตถุดิบชิ้นล่าสุดออกจากหม้อต้ม (เมื่อผู้เล่นกด Backspace)
     */
    public boolean removeLastIngredient() {
        if (isExploded() || ingredients.isEmpty()) return false;
        ingredients.remove(ingredients.size() - 1);
        SoundManager.getInstance().playGrab();
        return true;
    }

    public Potion brew() {
        if (isExploded() || ingredients.isEmpty()) return null;

        // ถ้ามีวัตถุดิบไม่ถึง 2 ชิ้น หรือสูตรไม่ถูกต้อง หม้อจะระเบิดติดคูลดาวน์!
        if (ingredients.size() < 2 || !RecipeBook.isValidRecipe(ingredients)) {
            explosionTimer = explosionDuration;
            ingredients.clear();
            SoundManager.getInstance().playExplosion();
            return null;
        }

        // สูตรถูกต้อง ปรุงสำเร็จ!
        SoundManager.getInstance().playBoil();
        Potion potion = RecipeBook.craft(ingredients);
        ingredients.clear();
        return potion;
    }

    public void clear() {
        ingredients.clear();
    }

    public void update(double dt) {
        bubbleTime += dt * 4;
        if (explosionTimer > 0) {
            explosionTimer = Math.max(0, explosionTimer - dt);
        }
    }

    public boolean isExploded() {
        return explosionTimer > 0;
    }

    public double getExplosionTimer() {
        return explosionTimer;
    }

    public List<IngredientType> getIngredients() {
        return ingredients;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void render(Graphics2D g) {
        // วาดหม้อต้มยา
        BufferedImage cauldronImg = AssetLoader.getImage("cauldron");
        if (cauldronImg != null) {
            g.drawImage(cauldronImg, x, y, size, size, null);
        }

        // วาดสีน้ำยาที่เปลี่ยนไปตามวัตถุดิบข้างใน
        if (!ingredients.isEmpty() && !isExploded()) {
            IngredientType last = ingredients.get(ingredients.size() - 1);
            Color tintColor = switch (last.getElement()) {
                case FIRE -> new Color(244, 67, 54, 90);
                case ICE -> new Color(33, 150, 243, 90);
                case POISON -> new Color(139, 195, 74, 90);
                case LIGHTNING -> new Color(255, 235, 59, 90);
                default -> new Color(76, 175, 80, 80);
            };
            int lx = x + 24;
            int ly = y + 26;
            int lw = size - 48;
            int lh = 30;
            g.setColor(tintColor);
            g.fillOval(lx, ly, lw, lh);
        }

        // แสดงผลเมื่อหม้อระเบิด (Explosion Smoke & Warning)
        if (isExploded()) {
            g.setColor(new Color(40, 40, 40, 220));
            g.fillOval(x + 15, y - 25, size - 30, 60);

            g.setColor(new Color(244, 67, 54));
            g.setFont(com.potionpanic.util.FontHelper.bold(14));
            String expMsg = "หม้อระเบิด! (" + String.format("%.1f", explosionTimer) + "s)";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(expMsg, x + (size - fm.stringWidth(expMsg)) / 2, y + 10);
        } else {
            // Live Recipe Preview เหนือหม้อต้ม
            drawRecipePreview(g);
        }

        // วาดช่องแสดงวัตถุดิบที่ใส่ลงไปแล้ว (ปรับเหลือ 2 ช่องสมดุลกึ่งกลางหม้อ)
        int slotW = 34;
        int slotGap = 10;
        int totalSlotsW = (Constants.CAULDRON_MAX_ITEMS * slotW) + ((Constants.CAULDRON_MAX_ITEMS - 1) * slotGap);
        int badgeStartX = x + (size - totalSlotsW) / 2;
        int badgeY = y + size - 16;

        for (int i = 0; i < Constants.CAULDRON_MAX_ITEMS; i++) {
            int bx = badgeStartX + i * (slotW + slotGap);
            g.setColor(new Color(20, 20, 30, 230));
            g.fillRoundRect(bx, badgeY, slotW, slotW, 8, 8);
            g.setColor(new Color(Constants.COLOR_ACCENT_GOLD.getRed(), Constants.COLOR_ACCENT_GOLD.getGreen(), Constants.COLOR_ACCENT_GOLD.getBlue(), 140));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(bx, badgeY, slotW, slotW, 8, 8);

            if (i < ingredients.size()) {
                BufferedImage icon = ingredients.get(i).getImage();
                if (icon != null) {
                    g.drawImage(icon, bx + 3, badgeY + 3, slotW - 6, slotW - 6, null);
                }
            } else {
                // ขีดประบางๆ แสดงช่องว่าง
                g.setColor(new Color(80, 75, 95));
                g.drawString(String.valueOf(i + 1), bx + slotW / 2 - 4, badgeY + slotW / 2 + 5);
            }
        }

        // ป้ายคำแนะนำด้านล่างหม้อต้ม
        g.setColor(Color.WHITE);
        g.setFont(Constants.FONT_SMALL);
        String label;
        if (isExploded()) {
            label = "รอหม้อฟื้นสภาพ...";
            g.setColor(new Color(255, 120, 120));
        } else if (ingredients.isEmpty()) {
            label = "กด 1-5 ใส่ของ";
        } else if (ingredients.size() == 1) {
            label = "[Backspace] ลบของ";
        } else {
            label = "[Space] ผสม / [Backspace] ลบ";
            g.setColor(Constants.COLOR_ACCENT_GOLD);
        }
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (size - fm.stringWidth(label)) / 2, y + size + 34);
    }

    private void drawRecipePreview(Graphics2D g) {
        if (ingredients.isEmpty()) return;

        int bubbleW = 220;
        int bubbleH = 34;
        int bubbleX = x + (size - bubbleW) / 2;
        int bubbleY = y - 36;

        if (ingredients.size() >= 2) {
            boolean valid = RecipeBook.isValidRecipe(ingredients);
            if (valid) {
                Potion preview = RecipeBook.previewCraft(ingredients);
                g.setColor(new Color(20, 35, 25, 230));
                g.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);
                g.setColor(new Color(76, 175, 80));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);

                // ภาพขวดยาตัวอย่าง
                if (preview != null && preview.getImage() != null) {
                    g.drawImage(preview.getImage(), bubbleX + 6, bubbleY + 3, 28, 28, null);
                }

                g.setColor(Color.WHITE);
                g.setFont(com.potionpanic.util.FontHelper.bold(12));
                String name = preview != null ? preview.getName() : "ยาเวทมนตร์";
                g.drawString(name, bubbleX + 38, bubbleY + 22);
            } else {
                // สูตรผิดพลาด แจ้งเตือนข้อความเตือนภัย
                g.setColor(new Color(50, 15, 15, 230));
                g.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);
                g.setColor(new Color(244, 67, 54));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);

                g.setColor(new Color(255, 180, 180));
                g.setFont(com.potionpanic.util.FontHelper.bold(12));
                String errMsg = "สูตรไม่ถูกต้อง (กด Backspace)";
                g.drawString(errMsg, bubbleX + 18, bubbleY + 22);
            }
        } else {
            // มี 1 ชิ้น
            g.setColor(new Color(20, 20, 35, 210));
            g.fillRoundRect(bubbleX + 20, bubbleY, bubbleW - 40, bubbleH, 8, 8);
            g.setColor(new Color(150, 140, 180));
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            String msg = "ใส่เพิ่มอีก 1 ชิ้น...";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, bubbleX + (bubbleW - fm.stringWidth(msg)) / 2, bubbleY + 21);
        }
    }
}
