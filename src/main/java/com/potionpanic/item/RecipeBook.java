package com.potionpanic.item;

import com.potionpanic.entity.potion.*;

import java.util.*;

/**
 * สมุดบันทึกสูตรปรุงยาเวทมนตร์ (Recipe Book)
 * ตรวจสอบวัตถุดิบและสร้างออบเจกต์ขวดยาที่ถูกต้องตามสูตร
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
    }

    private static final List<RecipeEntry> RECIPES = new ArrayList<>();

    static {
        RECIPES.add(new RecipeEntry("ยาเพลิงกัลป์ (Fire Potion)", "สมุนไพรเพลิง x2", "ระเบิดไฟหมู่ (AOE) + เผาไหม้ (Burn) ต่อเนื่อง",
                IngredientType.RED_HERB, IngredientType.RED_HERB));

        RECIPES.add(new RecipeEntry("ยาน้ำแข็ง (Ice Potion)", "ผลึกน้ำแข็ง x2", "สร้างดาเมจน้ำแข็ง + สโลว์มอนสเตอร์ 50%",
                IngredientType.BLUE_CRYSTAL, IngredientType.BLUE_CRYSTAL));

        RECIPES.add(new RecipeEntry("ยากรดพิษ (Acid/Poison)", "เห็ดพิษ x2", "กัดกร่อนเกราะ + พิษทำดาเมจต่อเนื่อง",
                IngredientType.TOXIC_MUSHROOM, IngredientType.TOXIC_MUSHROOM));

        RECIPES.add(new RecipeEntry("ยาสายฟ้า (Lightning Potion)", "รากไม้สายฟ้า x2", "ดาเมจสูงเดี่ยว + ชะงัก (Stun)",
                IngredientType.THUNDER_ROOT, IngredientType.THUNDER_ROOT));

        RECIPES.add(new RecipeEntry("ยาระเบิดผสม (Explosive)", "สมุนไพรเพลิง + รากไม้สายฟ้า", "ระเบิดเวทกวาดล้างรุนแรงรัศมีกว้างมาก",
                IngredientType.RED_HERB, IngredientType.THUNDER_ROOT));
    }

    public static List<RecipeEntry> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    /**
     * ผสมวัตถุดิบในหม้อต้ม แล้วคืนค่าขวดยาที่ตรงตามสูตร
     */
    public static Potion craft(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return new FailedPotion();
        }

        // นับจำนวนวัตถุดิบแต่ละประเภท
        Map<IngredientType, Integer> counts = new EnumMap<>(IngredientType.class);
        for (IngredientType ing : ingredients) {
            counts.put(ing, counts.getOrDefault(ing, 0) + 1);
        }

        int red = counts.getOrDefault(IngredientType.RED_HERB, 0);
        int blue = counts.getOrDefault(IngredientType.BLUE_CRYSTAL, 0);
        int poison = counts.getOrDefault(IngredientType.TOXIC_MUSHROOM, 0);
        int lightning = counts.getOrDefault(IngredientType.THUNDER_ROOT, 0);

        if (red >= 2) {
            return new FirePotion();
        } else if (blue >= 2) {
            return new IcePotion();
        } else if (poison >= 2) {
            return new PoisonPotion();
        } else if (lightning >= 2) {
            return new LightningPotion();
        } else if (red >= 1 && lightning >= 1) {
            return new ExplosivePotion();
        } else {
            return new FailedPotion();
        }
    }

    public static boolean isValidRecipe(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.size() < 2) return false;
        Map<IngredientType, Integer> counts = new EnumMap<>(IngredientType.class);
        for (IngredientType ing : ingredients) {
            counts.put(ing, counts.getOrDefault(ing, 0) + 1);
        }
        int red = counts.getOrDefault(IngredientType.RED_HERB, 0);
        int blue = counts.getOrDefault(IngredientType.BLUE_CRYSTAL, 0);
        int poison = counts.getOrDefault(IngredientType.TOXIC_MUSHROOM, 0);
        int lightning = counts.getOrDefault(IngredientType.THUNDER_ROOT, 0);
        return (red >= 2) || (blue >= 2) || (poison >= 2) || (lightning >= 2) || (red >= 1 && lightning >= 1);
    }

    public static Potion previewCraft(List<IngredientType> ingredients) {
        if (ingredients == null || ingredients.size() < 2) return null;
        return craft(ingredients);
    }
}
