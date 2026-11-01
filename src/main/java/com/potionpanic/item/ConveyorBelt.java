package com.potionpanic.item;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Renderable;
import com.potionpanic.entity.monster.Monster;
import com.potionpanic.util.Constants;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.FontHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * แท่นวางวัตถุดิบ 5 ช่อง (Ingredient Stations / Conveyor)
 * ระบบสุ่มวัตถุดิบอัจฉริยะตามสถานการณ์ภัยคุกคาม (Threat-Aware Smart Alchemy Spawning)
 * คำนวณความต้องการวัตถุดิบตามจุดอ่อนมอนสเตอร์ตัวที่ใกล้หอคอยที่สุด และของที่ค้างในหม้อต้ม
 * รับประกันว่าสายพานจะมีวัตถุดิบสำหรับต้มยาแก้ทางเสมอ โดยไม่ต้องเสียเวลารีสายพาน
 */
public class ConveyorBelt implements Renderable {
    private final IngredientType[] slots = new IngredientType[Constants.BELT_SLOTS_COUNT];
    private final double[] respawnTimers = new double[Constants.BELT_SLOTS_COUNT];
    private double respawnDuration = 0.6; // เวลาในการสร้างของใหม่มาแทนที่
    private List<IngredientType> allowedIngredients = java.util.Arrays.asList(IngredientType.values());
    private final Random random = new Random();
    private double runeRotation = 0.0;

    // ระบบสุ่มของใหม่ทั้งสายพาน (Reroll)
    private double rerollCooldownTimer = 0.0;
    private final double rerollCooldown = 1.0; // ปรับลดเหลือ 1.0 วินาทีเพื่อความลื่นไหล

    // แคชบริบทสนามรบเพื่อคำนวณการสุ่มอัจฉริยะ
    private List<Monster> cachedMonsters = Collections.emptyList();
    private List<IngredientType> cachedCauldron = Collections.emptyList();
    private List<ElementType> cachedUpcoming = Collections.emptyList();

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

    /**
     * กำหนดบริบทสนามรบสำหรับการคำนวณสุ่มวัตถุดิบอัจฉริยะ
     */
    public void setContext(List<Monster> activeMonsters, List<IngredientType> cauldronIngredients, List<ElementType> upcomingWeaknesses) {
        if (activeMonsters != null) this.cachedMonsters = activeMonsters;
        if (cauldronIngredients != null) this.cachedCauldron = cauldronIngredients;
        if (upcomingWeaknesses != null) this.cachedUpcoming = upcomingWeaknesses;
    }

    private void fillAllSlots() {
        // เติมสายพานด้วยระบบสุ่มอัจฉริยะ
        for (int i = 0; i < slots.length; i++) {
            slots[i] = getSmartIngredient(i);
            respawnTimers[i] = 0.0;
        }
    }

    /**
     * อัปเดตสายพานพร้อมรับบริบทมอนสเตอร์ในสนามและวัตถุดิบในหม้อต้ม
     */
    public void update(double dt, List<Monster> activeMonsters, List<IngredientType> cauldronIngredients, List<ElementType> upcomingWeaknesses) {
        if (activeMonsters != null) this.cachedMonsters = activeMonsters;
        if (cauldronIngredients != null) this.cachedCauldron = cauldronIngredients;
        if (upcomingWeaknesses != null) this.cachedUpcoming = upcomingWeaknesses;

        update(dt);
    }

    public void update(double dt) {
        runeRotation += dt * 3.5;

        if (rerollCooldownTimer > 0) {
            rerollCooldownTimer = Math.max(0, rerollCooldownTimer - dt);
        }

        // ตรวจสอบช่องที่ว่างอยู่ และนับเวลาเพื่อเสกของชิ้นใหม่มาแทนที่
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                respawnTimers[i] -= dt;
                if (respawnTimers[i] <= 0.0) {
                    slots[i] = getSmartIngredient(i);
                    respawnTimers[i] = 0.0;
                }
            }
        }
    }

    /**
     * ดึงรายการวัตถุดิบ 3 ชิ้นที่จำเป็นต้องใช้ปรุงยาปราบจุดอ่อนธาตุนั้นๆ
     */
    public List<IngredientType> getRequiredIngredientsForWeakness(ElementType el) {
        if (el == null) return List.of(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB);
        return switch (el) {
            case FIRE -> List.of(IngredientType.RED_HERB, IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL);
            case ICE -> List.of(IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB);
            case POISON -> List.of(IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM, IngredientType.RED_HERB);
            case LIGHTNING -> List.of(IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT, IngredientType.BLUE_CRYSTAL);
            case UNIVERSAL -> List.of(IngredientType.RED_HERB, IngredientType.TOXIC_MUSHROOM, IngredientType.THUNDER_ROOT);
            default -> List.of(IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB);
        };
    }

    /**
     * อัลกอริทึมสุ่มวัตถุดิบอัจฉริยะ (Smart Threat-Aware Alchemy Spawning)
     */
    public IngredientType getSmartIngredient(int slotIndex) {
        if (allowedIngredients == null || allowedIngredients.isEmpty()) {
            return IngredientType.RED_HERB;
        }

        // 1. ตรวจสอบมอนสเตอร์ที่อันตรายที่สุด (อยู่ใกล้หอคอยที่สุด)
        Monster primaryThreat = null;
        double minX = Double.MAX_VALUE;
        for (Monster m : cachedMonsters) {
            if (!m.isDead() && m.getX() < minX) {
                minX = m.getX();
                primaryThreat = m;
            }
        }

        ElementType targetWeakness = null;
        if (primaryThreat != null) {
            targetWeakness = primaryThreat.getWeakness();
        } else if (cachedUpcoming != null && !cachedUpcoming.isEmpty()) {
            targetWeakness = cachedUpcoming.get(0);
        }

        // 2. ถ้ามีจุดอ่อนเป้าหมาย ตรวจสอบว่าสายพาน + หม้อต้ม มีของครบสูตร 3 ชิ้นหรือยัง
        if (targetWeakness != null) {
            List<IngredientType> neededFormula = new ArrayList<>(getRequiredIngredientsForWeakness(targetWeakness));
            // กรองเฉพาะวัตถุดิบที่อนุญาตในด่านนี้
            neededFormula.removeIf(ing -> !allowedIngredients.contains(ing));

            // หักลบวัตถุดิบที่อยู่ในหม้อต้มแล้ว
            List<IngredientType> missingFromFormula = new ArrayList<>(neededFormula);
            for (IngredientType cIng : cachedCauldron) {
                missingFromFormula.remove(cIng);
            }

            // หักลบวัตถุดิบที่มีอยู่แล้วบนสายพานช่องอื่นๆ
            for (int j = 0; j < slots.length; j++) {
                if (j != slotIndex && slots[j] != null) {
                    missingFromFormula.remove(slots[j]);
                }
            }

            // ถ้ายังขาดวัตถุดิบในการประกอบสูตรยาแก้ทางตัวหน้าสุด ให้โอกาส 85% เสกของที่ขาดออกมาทันที!
            if (!missingFromFormula.isEmpty()) {
                if (random.nextDouble() < 0.85) {
                    return missingFromFormula.get(random.nextInt(missingFromFormula.size()));
                }
            }
        }

        // 3. ตรวจสอบของในหม้อต้ม: ถ้าผู้เล่นใส่ของไว้ 2 ชิ้น และมีสูตรที่ปิดได้
        if (cachedCauldron.size() == 2) {
            Map<IngredientType, com.potionpanic.entity.potion.Potion> suggestions = RecipeBook.getSuggestionsForTwo(cachedCauldron);
            List<IngredientType> validFinishers = new ArrayList<>();
            for (IngredientType fin : suggestions.keySet()) {
                if (allowedIngredients.contains(fin)) {
                    validFinishers.add(fin);
                }
            }
            if (!validFinishers.isEmpty() && random.nextDouble() < 0.80) {
                return validFinishers.get(random.nextInt(validFinishers.size()));
            }
        }

        // 4. สุ่มถ่วงน้ำหนักตามมอนสเตอร์ทั้งหมดในสนาม และของในหม้อต้ม
        Map<IngredientType, Double> weights = new HashMap<>();
        for (IngredientType ing : allowedIngredients) {
            weights.put(ing, 1.0); // น้ำหนักพื้นฐาน
        }

        // เพิ่มน้ำหนักให้วัตถุดิบที่เข้าคู่กับของในหม้อต้ม
        for (IngredientType cIng : cachedCauldron) {
            if (weights.containsKey(cIng)) {
                weights.put(cIng, weights.get(cIng) + 1.5);
            }
        }

        for (Monster m : cachedMonsters) {
            if (!m.isDead()) {
                double urgency = Math.max(1.0, (1280.0 - m.getX()) / 350.0);
                List<IngredientType> reqs = getRequiredIngredientsForWeakness(m.getWeakness());
                for (IngredientType rIng : reqs) {
                    if (weights.containsKey(rIng)) {
                        weights.put(rIng, weights.get(rIng) + 2.5 * urgency);
                    }
                }
            }
        }

        // รูเล็ตสุ่มตามน้ำหนัก (Weighted Random Selection)
        double totalWeight = 0.0;
        for (double w : weights.values()) {
            totalWeight += w;
        }

        double roll = random.nextDouble() * totalWeight;
        double accum = 0.0;
        for (Map.Entry<IngredientType, Double> entry : weights.entrySet()) {
            accum += entry.getValue();
            if (roll <= accum) {
                return entry.getKey();
            }
        }

        return allowedIngredients.get(random.nextInt(allowedIngredients.size()));
    }

    /**
     * สุ่มวัตถุดิบใหม่ทั้ง 5 ช่องบนสายพาน (เมื่อกดปุ่ม [F] หรือคลิก)
     * การันตีได้ชุดวัตถุดิบแก้ทางมอนสเตอร์ตัวหน้าสุดครบสูตร 3 ชิ้นทันที
     */
    public boolean rerollAll() {
        return rerollAll(cachedMonsters, cachedCauldron, cachedUpcoming);
    }

    public boolean rerollAll(List<Monster> activeMonsters, List<IngredientType> cauldronIngredients, List<ElementType> upcomingWeaknesses) {
        if (rerollCooldownTimer > 0) {
            return false;
        }
        rerollCooldownTimer = rerollCooldown;

        if (activeMonsters != null) this.cachedMonsters = activeMonsters;
        if (cauldronIngredients != null) this.cachedCauldron = cauldronIngredients;
        if (upcomingWeaknesses != null) this.cachedUpcoming = upcomingWeaknesses;

        // หาจุดอ่อนของมอนสเตอร์ตัวที่อันตรายที่สุด
        Monster primary = null;
        double minX = Double.MAX_VALUE;
        for (Monster m : cachedMonsters) {
            if (!m.isDead() && m.getX() < minX) {
                minX = m.getX();
                primary = m;
            }
        }

        ElementType targetWeakness = null;
        if (primary != null) {
            targetWeakness = primary.getWeakness();
        } else if (cachedUpcoming != null && !cachedUpcoming.isEmpty()) {
            targetWeakness = cachedUpcoming.get(0);
        }

        List<IngredientType> guaranteedSet = new ArrayList<>();
        if (targetWeakness != null) {
            for (IngredientType ing : getRequiredIngredientsForWeakness(targetWeakness)) {
                if (allowedIngredients.contains(ing)) {
                    guaranteedSet.add(ing);
                }
            }
        }

        // เติมช่องที่เหลือให้ครบ 5 ช่อง
        while (guaranteedSet.size() < slots.length) {
            guaranteedSet.add(getSmartIngredient(-1));
        }

        // สับตำแหน่งเพื่อความเป็นธรรมชาติ
        Collections.shuffle(guaranteedSet, random);

        for (int i = 0; i < slots.length; i++) {
            slots[i] = guaranteedSet.get(i);
            respawnTimers[i] = 0.0;
        }

        SoundManager.getInstance().playBoil();
        return true;
    }

    public double getRerollCooldownTimer() {
        return rerollCooldownTimer;
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

    public void reset() {
        rerollCooldownTimer = 0.0;
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

        // วาดปุ่ม Reroll [F] ข้างสายพาน
        drawRerollButton(g, beltX + beltWidth + 12, beltY + 20);

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
            g.setFont(FontHelper.bold(13));
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

            g.setFont(FontHelper.bold(11));
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

    private void drawRerollButton(Graphics2D g, int rx, int ry) {
        int rw = 120;
        int rh = 98;
        boolean canReroll = rerollCooldownTimer <= 0;

        g.setColor(canReroll ? new Color(42, 32, 55, 220) : new Color(28, 22, 36, 180));
        g.fillRoundRect(rx, ry, rw, rh, 12, 12);
        g.setColor(canReroll ? Constants.COLOR_ACCENT_GOLD : new Color(70, 60, 85));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(rx, ry, rw, rh, 12, 12);

        // หัวข้อปุ่ม
        g.setFont(FontHelper.bold(12));
        g.setColor(canReroll ? Color.WHITE : new Color(140, 130, 155));
        g.drawString("สุ่มสายพาน [F]", rx + 16, ry + 32);

        if (canReroll) {
            g.setFont(FontHelper.bold(11));
            g.setColor(new Color(129, 212, 250));
            g.drawString("คลิก / กด [F]", rx + 24, ry + 62);
        } else {
            g.setFont(FontHelper.plain(11));
            g.setColor(new Color(255, 170, 170));
            String cd = String.format("คูลดาวน์ %.1fs", rerollCooldownTimer);
            g.drawString(cd, rx + 12, ry + 62);
        }
    }

    private void drawRuneEmblem(Graphics2D g, int cx, int cy, double angle) {
        g.setColor(new Color(180, 150, 80));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(80, 60, 30));
        g.fillOval(cx - 4, cy - 4, 8, 8);
    }
}
