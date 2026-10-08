package com.potionpanic.item;

import com.potionpanic.entity.Renderable;
import com.potionpanic.entity.potion.Potion;
import com.potionpanic.util.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ชั้นพักขวดยาพร้อมใช้ (Potion Rack) 4 ช่อง
 * รองรับคีย์ลัด [Q], [W], [E], [R] และ Auto-Select ขวดล่าสุด
 * แสดงชื่อยาและปุ่มลัดชัดเจน
 */
public class PotionRack implements Renderable {
    private final Potion[] slots = new Potion[Constants.POTION_RACK_CAPACITY];
    private final String[] hotkeys = {"Q", "W", "E", "R"};
    private int selectedIndex = -1;
    private final int x;
    private final int y;
    private final int slotSize = 72;
    private final int gap = 16;

    public PotionRack(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean addPotion(Potion potion) {
        if (potion == null) return false;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                slots[i] = potion;
                selectedIndex = i; // Auto-Select ขวดยาใบใหม่ทันที
                return true;
            }
        }
        return false; // เต็มทุกช่อง
    }

    public void selectSlot(int index) {
        if (index >= 0 && index < slots.length && slots[index] != null) {
            this.selectedIndex = (this.selectedIndex == index) ? -1 : index;
        }
    }

    public Potion getSelectedPotion() {
        if (selectedIndex >= 0 && selectedIndex < slots.length) {
            return slots[selectedIndex];
        }
        return null;
    }

    public Potion consumeSelected() {
        if (selectedIndex >= 0 && selectedIndex < slots.length && slots[selectedIndex] != null) {
            Potion p = slots[selectedIndex];
            slots[selectedIndex] = null;
            selectedIndex = -1;
            // หาขวดถัดไปที่ยังเหลืออยู่เพื่อเลือกต่อ
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] != null) {
                    selectedIndex = i;
                    break;
                }
            }
            return p;
        }
        return null;
    }

    public int getSlotAt(int mx, int my) {
        for (int i = 0; i < slots.length; i++) {
            int sx = x + i * (slotSize + gap);
            int sy = y + 16;
            if (mx >= sx && mx <= sx + slotSize && my >= sy && my <= sy + slotSize) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }
        selectedIndex = -1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void render(Graphics2D g) {
        int totalWidth = (slotSize + gap) * Constants.POTION_RACK_CAPACITY + 24;
        int totalHeight = slotSize + 76;

        // วาดโต๊ะชั้นวางยา
        g.setColor(new Color(40, 28, 22));
        g.fillRoundRect(x - 12, y - 6, totalWidth, totalHeight, 16, 16);
        g.setColor(Constants.COLOR_WOOD_LIGHT);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x - 12, y - 6, totalWidth, totalHeight, 16, 16);

        // ป้ายหัวข้อชั้นวางยา
        g.setColor(Constants.COLOR_ACCENT_GOLD);
        g.setFont(com.potionpanic.util.FontHelper.bold(12));
        g.drawString("ชั้นพักขวดยาพร้อมใช้ (กด [Q/W/E/R] หรือคลิกเพื่อเลือก)", x, y + totalHeight - 12);

        for (int i = 0; i < slots.length; i++) {
            int sx = x + i * (slotSize + gap);
            int sy = y + 20;

            boolean isSelected = (i == selectedIndex);

            // ป้ายปุ่มลัด [Q], [W], [E], [R] ด้านบนสล็อต
            g.setColor(new Color(45, 40, 60));
            g.fillRoundRect(sx + slotSize / 2 - 14, sy - 14, 28, 18, 5, 5);
            g.setColor(isSelected ? Constants.COLOR_ACCENT_GOLD : Color.WHITE);
            g.setFont(com.potionpanic.util.FontHelper.bold(11));
            FontMetrics fmHk = g.getFontMetrics();
            String hk = "[" + hotkeys[i] + "]";
            g.drawString(hk, sx + slotSize / 2 - fmHk.stringWidth(hk) / 2, sy);

            // สีพื้นสล็อต
            g.setColor(isSelected ? new Color(75, 60, 110) : new Color(25, 20, 30));
            g.fillRoundRect(sx, sy, slotSize, slotSize, 12, 12);

            // ขอบสล็อต (ถ้าเลือกอยู่ จะมีแสงสีทองเรืองรอง)
            if (isSelected) {
                g.setColor(Constants.COLOR_ACCENT_GOLD);
                g.setStroke(new BasicStroke(3.5f));
            } else {
                g.setColor(new Color(100, 90, 80));
                g.setStroke(new BasicStroke(1.5f));
            }
            g.drawRoundRect(sx, sy, slotSize, slotSize, 12, 12);

            // วาดขวดยา
            Potion p = slots[i];
            if (p != null) {
                BufferedImage img = p.getImage();
                if (img != null) {
                    g.drawImage(img, sx + 6, sy + 6, slotSize - 12, slotSize - 12, null);
                }
            }

            // ป้ายชื่อยาใต้ช่อง
            int labelW = slotSize + 10;
            int labelX = sx - 5;
            int labelY = sy + slotSize + 6;

            g.setColor(new Color(15, 12, 25, 220));
            g.fillRoundRect(labelX, labelY, labelW, 18, 6, 6);
            g.setFont(com.potionpanic.util.FontHelper.bold(10));
            FontMetrics fm = g.getFontMetrics();

            if (p != null) {
                g.setColor(p.getFlaskColor());
                // ดึงชื่อย่อ เช่น ยาเพลิง, ยาน้ำแข็ง
                String pName = p.getName().split(" ")[0];
                g.drawString(pName, labelX + (labelW - fm.stringWidth(pName)) / 2, labelY + 13);
            } else {
                g.setColor(new Color(110, 100, 120));
                String empty = "ว่าง";
                g.drawString(empty, labelX + (labelW - fm.stringWidth(empty)) / 2, labelY + 13);
            }
        }
    }
}
