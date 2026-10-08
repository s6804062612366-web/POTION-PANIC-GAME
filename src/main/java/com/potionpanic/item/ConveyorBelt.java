package com.potionpanic.item;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Renderable;
import com.potionpanic.util.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * แท่นวางวัตถุดิบ 5 ช่อง (Ingredient Stations / Conveyor)
 * แสดงปุ่มกด 1-5 และป้ายชื่อวัตถุดิบภาษาไทยชัดเจน
 */
public class ConveyorBelt implements Renderable {
    private final IngredientType[] slots = new IngredientType[Constants.BELT_SLOTS_COUNT];
    private final double[] respawnTimers = new double[Constants.BELT_SLOTS_COUNT];
    private double respawnDuration = 0.6; // เวลาในการสร้างของใหม่มาแทนที่
    private List<IngredientType> allowedIngredients = java.util.Arrays.asList(IngredientType.values());
    private final Random random = new Random();
    private double runeRotation = 0.0;

    public ConveyorBelt() {
        fillAllSlots();
    }

    public void setAllowedIngredients(List<IngredientType> allowed) {
        if (allowed != null && !allowed.isEmpty()) {
            this.allowedIngredients = new java.util.ArrayList<>(allowed);
            fillAllSlots();
        }
    }

    public void setRespawnDuration(double duration) {
        this.respawnDuration = Math.max(0.15, duration);
    }

    private void fillAllSlots() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = getRandomIngredient();
            respawnTimers[i] = 0.0;
        }
    }

    public void update(double dt) {
        runeRotation += dt * 3.5;

        // ตรวจสอบช่องที่ว่างอยู่ และนับเวลาเพื่อเสกของชิ้นใหม่มาแทนที่
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                respawnTimers[i] -= dt;
                if (respawnTimers[i] <= 0.0) {
                    slots[i] = getRandomIngredient();
                    respawnTimers[i] = 0.0;
                }
            }
        }
    }

    /**
     * ดึงวัตถุดิบจากช่องที่ระบุ และเริ่มนับเวลาเสกของชิ้นใหม่มาแทนที่ในช่องเดิม
     */
    public IngredientType grab(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.length) return null;
        IngredientType item = slots[slotIndex];
        if (item != null) {
            slots[slotIndex] = null;
            respawnTimers[slotIndex] = respawnDuration; // เริ่มนับเวลาแทนที่
            SoundManager.getInstance().playGrab();
        }
        return item;
    }

    private IngredientType getRandomIngredient() {
        if (allowedIngredients == null || allowedIngredients.isEmpty()) {
            return IngredientType.RED_HERB;
        }
        return allowedIngredients.get(random.nextInt(allowedIngredients.size()));
    }

    public void reset() {
        fillAllSlots();
    }

    @Override
    public void render(Graphics2D g) {
        int beltX = Constants.BELT_START_X - 40;
        int beltY = Constants.ZONE_MID_Y + 10;
        int beltWidth = (Constants.BELT_SLOT_SIZE + Constants.BELT_SLOT_GAP) * Constants.BELT_SLOTS_COUNT + 60;
        int beltHeight = 138;

        // วาดฐานแท่นวางไม้และเหล็กเวทมนตร์
        g.setColor(new Color(45, 30, 25));
        g.fillRoundRect(beltX, beltY, beltWidth, beltHeight, 20, 20);

        // แผ่นแท่นวางโลหะเวทมนตร์
        g.setColor(new Color(30, 32, 40));
        g.fillRoundRect(beltX + 10, beltY + 12, beltWidth - 20, beltHeight - 24, 12, 12);

        // ลายเส้นประดับแท่นวาง
        g.setColor(new Color(55, 60, 75));
        for (int x = beltX + 25; x < beltX + beltWidth - 20; x += 35) {
            g.drawLine(x, beltY + 16, x, beltY + beltHeight - 16);
        }

        // วาดหัวท้ายตราสัญลักษณ์เวทมนตร์
        drawRuneEmblem(g, beltX + 18, beltY + beltHeight / 2, runeRotation);
        drawRuneEmblem(g, beltX + beltWidth - 18, beltY + beltHeight / 2, runeRotation);

        // วาดสล็อต 5 ช่อง
        int slotSize = Constants.BELT_SLOT_SIZE;
        for (int i = 0; i < Constants.BELT_SLOTS_COUNT; i++) {
            int sx = Constants.BELT_START_X + i * (slotSize + Constants.BELT_SLOT_GAP);
            int sy = beltY + 14;

            // จานรองสล็อตโลหะ
            g.setColor(new Color(20, 20, 30, 220));
            g.fillRoundRect(sx, sy, slotSize, slotSize, 14, 14);

            g.setColor(new Color(Constants.COLOR_ACCENT_GOLD.getRed(), Constants.COLOR_ACCENT_GOLD.getGreen(), Constants.COLOR_ACCENT_GOLD.getBlue(), 120));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(sx, sy, slotSize, slotSize, 14, 14);

            // ป้ายบอกปุ่มกด [1] ถึง [5]
            g.setColor(new Color(40, 40, 55, 230));
            g.fillRoundRect(sx + slotSize / 2 - 16, sy - 10, 32, 20, 6, 6);
            g.setColor(Constants.COLOR_ACCENT_GOLD);
            g.setFont(com.potionpanic.util.FontHelper.bold(13));
            g.drawString("[" + (i + 1) + "]", sx + slotSize / 2 - 9, sy + 5);

            // วาดไอเทม หรือแสดงแอนิเมชันกำลังเสกของชิ้นใหม่มาแทนที่
            IngredientType item = slots[i];
            if (item != null) {
                BufferedImage img = item.getImage();
                if (img != null) {
                    int pad = 10;
                    g.drawImage(img, sx + pad, sy + pad, slotSize - pad * 2, slotSize - pad * 2, null);
                }
            } else {
                // กำลังเสกวัตถุดิบใหม่มาแทนที่ (Loading Arc)
                double progress = 1.0 - Math.max(0.0, respawnTimers[i] / respawnDuration);
                g.setColor(new Color(255, 204, 51, 180));
                g.setStroke(new BasicStroke(3));
                int arcSize = slotSize - 36;
                int arcX = sx + 18;
                int arcY = sy + 18;
                g.drawArc(arcX, arcY, arcSize, arcSize, 90, (int) (progress * 360));

                // แสงวิบวับตรงกลาง
                g.setColor(new Color(255, 255, 255, 120));
                g.fillOval(sx + slotSize / 2 - 4, sy + slotSize / 2 - 4, 8, 8);
            }

            // ป้ายชื่อวัตถุดิบภาษาไทยใต้ช่อง
            int labelW = slotSize + 22;
            int labelX = sx - 11;
            int labelY = sy + slotSize + 5;
            g.setColor(new Color(15, 12, 25, 230));
            g.fillRoundRect(labelX, labelY, labelW, 20, 6, 6);
            g.setColor(new Color(90, 80, 110));
            g.setStroke(new BasicStroke(1));
            g.drawRoundRect(labelX, labelY, labelW, 20, 6, 6);

            g.setFont(com.potionpanic.util.FontHelper.bold(11));
            FontMetrics fm = g.getFontMetrics();
            if (item != null) {
                g.setColor(item.getElement().getColor());
                String name = item.getShortName();
                g.drawString(name, labelX + (labelW - fm.stringWidth(name)) / 2, labelY + 14);
            } else {
                g.setColor(new Color(160, 150, 170));
                String loading = "กำลังเสก...";
                g.drawString(loading, labelX + (labelW - fm.stringWidth(loading)) / 2, labelY + 14);
            }
        }
    }

    private void drawRuneEmblem(Graphics2D g, int cx, int cy, double angle) {
        g.setColor(new Color(180, 150, 80));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(80, 60, 30));
        g.fillOval(cx - 4, cy - 4, 8, 8);
    }
}
