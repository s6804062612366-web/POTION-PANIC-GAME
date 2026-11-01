package com.potionpanic.item;

import com.potionpanic.entity.potion.*;

import java.util.*;

/**
 * สมุดบันทึกตำราปรุงยาเวทมนตร์ (Recipe Book - 3-Ingredient Magic School Alchemy)
 * ตรวจสอบวัตถุดิบ 3 ชิ้น (Order-Independent) และสร้างอ็อบเจกต์ขวดยาที่ถูกต้องตามตำรา
 * พร้อมระบบ Smart Suggestion คำนวณวัตถุดิบชิ้นที่ 3 เพื่อช่วยใบ้ผู้เล่น
 */
public class RecipeBook {

    public static class RecipeEntry {
        private final String potionName;
        private final String ingredientsDesc;
        private final String effectDesc;
        private final List<IngredientType> required;

        public RecipeEntry(String potionName, String ingredientsDesc, String effectDesc, IngredientType... ingredients) {
            this.potionName = potionName;
            this.ingredientsDesc = ingredientsDesc;
            this.effectDesc = effectDesc;
            this.required = Arrays.asList(ingredients);
        }

        public String getPotionName() {
            return potionName;
        }

        public String getIngredientsDesc() {
            return ingredientsDesc;
        }

        public String getEffectDesc() {
            return effectDesc;
        }

        public List<IngredientType> getRequired() {
            return required;
        }

        /**
         * ตรวจสอบว่าวัตถุดิบที่ให้มา ตรงกับสูตรนี้หรือไม่ (Order-Independent)
         */
        public boolean matches(List<IngredientType> ingredients) {
            if (ingredients == null || ingredients.size() != required.size()) return false;
            Map<IngredientType, Integer> inCounts = countIngredients(ingredients);
            Map<IngredientType, Integer> reqCounts = countIngredients(required);
            return inCounts.equals(reqCounts);
        }
    }

    private static final List<RecipeEntry> RECIPES = new ArrayList<>();

    static {
        // สูตรที่ 1: ยาเพลิงกัลป์ (Fire Potion)
        RECIPES.add(new RecipeEntry("ยาเพลิงกัลป์ (Fire Potion)",
                "สมุนไพรเพลิง x2 + ผลึกน้ำแข็ง x1",
                "ระเบิดไฟหมู่ (AOE) + เผาไหม้ (Burn) ต่อเนื่อง [ชนะทาง: อสูรพฤกษา]",
                IngredientType.RED_HERB, IngredientType.RED_HERB, IngredientType.BLUE_CRYSTAL));

        // สูตรที่ 2: ยาน้ำแข็ง (Ice Potion)
        RECIPES.add(new RecipeEntry("ยาน้ำแข็ง (Ice Potion)",
                "ผลึกน้ำแข็ง x2 + สมุนไพรเพลิง x1",
                "ดาเมจน้ำแข็ง + สโลว์แช่แข็ง 50% [ชนะทาง: โกเลมไฟ]",
                IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB));

        // สูตรที่ 3: ยากรดพิษ (Acid/Poison)
        RECIPES.add(new RecipeEntry("ยากรดพิษ (Acid/Poison)",
                "เห็ดพิษ x2 + สมุนไพรเพลิง x1",
                "กัดกร่อนละลายเกราะหนา + ทิ้งบ่อพิษกัดกร่อน [ชนะทาง: สัตว์ร้ายเกราะ]",
                IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM, IngredientType.RED_HERB));

        // สูตรที่ 4: ยาสายฟ้า (Lightning Potion)
        RECIPES.add(new RecipeEntry("ยาสายฟ้า (Lightning Potion)",
                "รากสายฟ้า x2 + ผลึกน้ำแข็ง x1",
                "ดาเมจสูง + สายฟ้าชิ่ง + ช็อตชะงัก Stun [ชนะทาง: อิมป์ว่องไว]",
                IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT, IngredientType.BLUE_CRYSTAL));

        // สูตรที่ 5: ยาหมอกพายุพิษ (Frost-Venom)
        RECIPES.add(new RecipeEntry("ยาหมอกพายุพิษ (Frost-Venom)",
                "เห็ดพิษ + ผลึกน้ำแข็ง + สมุนไพรเพลิง",
                "หมอกพิษไอเย็นกว้าง สโลว์และติดพิษคู่ [ชนะทาง: โกเลมไฟ & สัตว์ร้ายเกราะ]",
                IngredientType.TOXIC_MUSHROOM, IngredientType.BLUE_CRYSTAL, IngredientType.RED_HERB));

        // ของแรร์พิเศษ: ยาระเบิดมหาเวท (Explosive Potion - Miracle Brew)
        RECIPES.add(new RecipeEntry("ยาระเบิดมหาเวท (Miracle Brew - RARE)",
                "สุ่มออก 15-25% จากการปรุงยาทุกชนิดสำเร็จ",
                "ระเบิดทำลายล้างมหาเวท เจาะเกราะทุกธาตุ และทำลายบอสใหญ่ 3 เท่า!"));
    }

    private static final Random RANDOM = new Random();

    /**
     * คำนวณการสุ่มออกของยาระเบิดมหาเวท (Miracle Brew)
     * มีโอกาสสุ่มติด 15% ในสถานการณ์ปกติ และเพิ่มเป็น 25% ในสถานการณ์คับขัน
     */
    public static boolean shouldTriggerMiracleExplosive(boolean isClutch) {
        double chance = isClutch ? 0.25 : 0.15;
        return RANDOM.nextDouble() < chance;
    }

    public static List<RecipeEntry> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Potion craft(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.size() < 3) {
            return new FailedPotion();
        }

        Map<IngredientType, Integer> counts = countIngredients(ingredients);
        int red = counts.getOrDefault(IngredientType.RED_HERB, 0);
        int blue = counts.getOrDefault(IngredientType.BLUE_CRYSTAL, 0);
        int poison = counts.getOrDefault(IngredientType.TOXIC_MUSHROOM, 0);
        int thunder = counts.getOrDefault(IngredientType.THUNDER_ROOT, 0);

        if (red == 2 && blue == 1) {
            return new FirePotion();
        } else if (blue == 2 && red == 1) {
            return new IcePotion();
        } else if (poison == 2 && red == 1) {
            return new PoisonPotion();
        } else if (thunder == 2 && blue == 1) {
            return new LightningPotion();
        } else if (poison == 1 && blue == 1 && red == 1) {
            return new FrostVenomPotion();
        } else {
            return new FailedPotion();
        }
    }

    public static boolean isValidRecipe(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.size() != 3) return false;
        Potion p = craft(ingredients);
        return !(p instanceof FailedPotion);
    }

    public static Potion previewCraft(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.size() != 3) return null;
        return craft(ingredients);
    }

    /**
     * คำนวณคำแนะนำว่า ถ้ามีของในหม้อ 2 ชิ้น ชิ้นที่ 3 ชนิดใดบ้างที่จะทำให้ได้ยาสำเร็จ
     * @return แผนที่: ชนิดวัตถุดิบชิ้นที่ 3 -> ผลลัพธ์ขวดยาที่จะได้
     */
    public static Map<IngredientType, Potion> getSuggestionsForTwo(List<IngredientType> currentTwo) {
        Map<IngredientType, Potion> suggestions = new EnumMap<>(IngredientType.class);
        if (currentTwo == null || currentTwo.size() != 2) return suggestions;

        for (IngredientType candidate : IngredientType.values()) {
            List<IngredientType> testList = new ArrayList<>(currentTwo);
            testList.add(candidate);
            if (isValidRecipe(testList)) {
                suggestions.put(candidate, craft(testList));
            }
        }
        return suggestions;
    }

    private static Map<IngredientType, Integer> countIngredients(List<IngredientType> list) {
        Map<IngredientType, Integer> counts = new EnumMap<>(IngredientType.class);
        for (IngredientType ing : list) {
            counts.put(ing, counts.getOrDefault(ing, 0) + 1);
        }
        return counts;
    }
}
