package com.potionpanic.item;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Renderable;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.util.AssetLoader;
import com.potionpanic.util.Constants;
import com.potionpanic.util.FontHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * หม้อต้มยาเวทมนตร์ (Cauldron) - ระบบตำรา 3 ส่วนผสม
 * ใส่ได้สูงสุด 3 วัตถุดิบ และกดปุ่ม Spacebar เพื่อต้มผสมยา
 * มีระบบ Smart Recipe Helper ใบ้ส่วนผสมชิ้นที่ 3 อัตโนมัติเมื่อใส่ของครบ 2 ชิ้น
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

        // ถ้ามีวัตถุดิบไม่ถึง 3 ชิ้น หรือสูตรไม่ถูกต้อง หม้อจะระเบิดติดคูลดาวน์!
        if (ingredients.size() < 3 || !RecipeBook.isValidRecipe(ingredients)) {
            explosionTimer = explosionDuration;
            ingredients.clear();
            SoundManager.getInstance().playExplosion();
            return null;
        }

        // สูตรถูกต้อง 3 ส่วนผสม ปรุงสำเร็จ!
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

        // วาดสีน้ำยาที่เปลี่ยนไปตามวัตถุดิบชิ้นล่าสุดข้างใน
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
            g.setFont(FontHelper.bold(14));
            String expMsg = "หม้อระเบิด! (" + String.format("%.1f", explosionTimer) + "s)";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(expMsg, x + (size - fm.stringWidth(expMsg)) / 2, y + 10);
        } else {
            // Live Recipe Preview & Smart Helper เหนือหม้อต้ม
            drawRecipePreview(g);
        }

        // วาดช่องแสดงวัตถุดิบ 3 ช่อง (3 Slots)
        int slotW = 34;
        int slotGap = 8;
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
                g.setColor(new Color(80, 75, 95));
                g.drawString(String.valueOf(i + 1), bx + slotW / 2 - 4, badgeY + slotW / 2 + 5);
            }
        }

        // ป้ายแจ้งเตือนเมื่อหม้อต้มระเบิด (เมื่อปกติจะไม่แสดงตัวหนังสือรกตา)
        if (isExploded()) {
            g.setColor(new Color(255, 120, 120));
            g.setFont(Constants.FONT_SMALL);
            String label = "รอหม้อฟื้นสภาพ...";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, x + (size - fm.stringWidth(label)) / 2, y + size + 34);
        }
    }

    private void drawRecipePreview(Graphics2D g) {
        if (ingredients.isEmpty()) return;

        int bubbleW = 210;
        int bubbleH = 32;
        int bubbleX = x + (size - bubbleW) / 2;
        int bubbleY = y - 28;

        if (ingredients.size() == 3) {
            boolean valid = RecipeBook.isValidRecipe(ingredients);
            if (valid) {
                Potion preview = RecipeBook.previewCraft(ingredients);
                g.setColor(new Color(20, 35, 25, 230));
                g.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);
                g.setColor(new Color(76, 175, 80));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);

                if (preview != null && preview.getImage() != null) {
                    g.drawImage(preview.getImage(), bubbleX + 6, bubbleY + 3, 26, 26, null);
                }

                g.setColor(Color.WHITE);
                g.setFont(FontHelper.bold(11));
                String name = preview != null ? preview.getName().split(" ")[0] : "ยาเวทมนตร์";
                g.drawString("ปรุง: " + name + " (ลุ้นระเบิด)", bubbleX + 34, bubbleY + 20);
            } else {
                g.setColor(new Color(50, 15, 15, 230));
                g.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);
                g.setColor(new Color(244, 67, 54));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);

                g.setColor(new Color(255, 180, 180));
                g.setFont(FontHelper.bold(11));
                String errMsg = "สูตรไม่ตรงตำรา";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(errMsg, bubbleX + (bubbleW - fm.stringWidth(errMsg)) / 2, bubbleY + 20);
            }
        } else if (ingredients.size() == 2) {
            // ระบบ Smart Recipe Helper: ใบ้ชิ้นที่ 3 อัตโนมัติ!
            Map<IngredientType, Potion> suggestions = RecipeBook.getSuggestionsForTwo(ingredients);
            g.setColor(new Color(24, 28, 42, 230));
            g.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);
            g.setColor(new Color(100, 140, 220));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 10, 10);

            if (!suggestions.isEmpty()) {
                Map.Entry<IngredientType, Potion> first = suggestions.entrySet().iterator().next();
                IngredientType needed = first.getKey();
                Potion resulting = first.getValue();

                // วาดไอคอนของชิ้นที่ต้องการ
                BufferedImage ingIcon = needed.getImage();
                if (ingIcon != null) {
                    g.drawImage(ingIcon, bubbleX + 6, bubbleY + 3, 26, 26, null);
                }

                g.setFont(FontHelper.bold(13));
                g.setColor(Constants.COLOR_ACCENT_GOLD);
                g.drawString("->", bubbleX + 36, bubbleY + 21);

                // วาดไอคอนของขวดยาที่จะได้
                BufferedImage potIcon = resulting.getImage();
                if (potIcon != null) {
                    g.drawImage(potIcon, bubbleX + 54, bubbleY + 3, 26, 26, null);
                }

                g.setFont(FontHelper.bold(12));
                g.setColor(Color.WHITE);
                String hintText = "ได้ " + resulting.getName().split(" ")[0];
                g.drawString(hintText, bubbleX + 86, bubbleY + 21);
            } else {
                g.setFont(FontHelper.plain(11));
                g.setColor(new Color(255, 170, 170));
                String noRecipe = "สองชิ้นนี้ไม่มีในตำรา (กด Backspace)";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(noRecipe, bubbleX + (bubbleW - fm.stringWidth(noRecipe)) / 2, bubbleY + 20);
            }
        } else {
            // มี 1 ชิ้น
            int b1W = 190;
            int b1X = x + (size - b1W) / 2;
            g.setColor(new Color(20, 20, 35, 210));
            g.fillRoundRect(b1X, bubbleY, b1W, bubbleH, 8, 8);
            g.setColor(new Color(175, 165, 195));
            g.setFont(FontHelper.plain(11));
            String msg = "ใส่ส่วนผสมเพิ่มอีก 2 ชิ้น...";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, b1X + (b1W - fm.stringWidth(msg)) / 2, bubbleY + 20);
        }
    }
}
