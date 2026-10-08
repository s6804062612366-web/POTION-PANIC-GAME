package com.potionpanic.entity.monster;

import com.potionpanic.util.ElementType;

/**
 * มอนสเตอร์ภูตพายุว่องไว (Swift Imp)
 * เคลื่อนที่รวดเร็ว เลือดน้อย จุดอ่อนคือสายฟ้า (LIGHTNING) หรือการแช่แข็งให้อยู่กับที่
 */
public class SwiftImp extends Monster {

    public SwiftImp(double startX, double startY) {
        super("อิมป์ว่องไว", 60, 1.3, 8, ElementType.LIGHTNING, startX, startY);
    }

    @Override
    public String getSpriteName() {
        return "monster_swift_imp";
    }
}
