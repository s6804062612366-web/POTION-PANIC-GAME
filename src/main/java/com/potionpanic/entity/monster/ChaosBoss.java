package com.potionpanic.entity.monster;

import com.potionpanic.audio.SoundManager;
import com.potionpanic.entity.Tower;
import com.potionpanic.util.ElementType;
import com.potionpanic.util.FontHelper;

import java.awt.*;

/**
 * บอสใหญ่: ราชันย์อสูรเวท (Chaos Overlord)
 * บอสประจำด่านที่ 3 และปรากฏทุกๆ 3 เวฟในโหมด Endless
 * กลไกพิเศษ: สลับเกราะธาตุตลอดการต่อสู้ และแพ้ดาเมจยาระเบิดมหาเวท (Universal) ทะลุเกราะ 3 เท่า!
 */
public class ChaosBoss extends Monster {
    private double shiftTimer = 0.0;
    private final double shiftInterval = 7.5; // สลับธาตุทุก 7.5 วินาที
    private int critHitsTaken = 0;
    private double auraRotation = 0.0;

    private static final ElementType[] SHIFT_ELEMENTS = {
            ElementType.FIRE, ElementType.ICE, ElementType.POISON, ElementType.LIGHTNING
    };
    private int elementIndex = 0;

    public ChaosBoss(double startX, double startY) {
        super("ราชันย์อสูรเวท", 420, 0.28, 25, ElementType.FIRE, startX, startY);
        this.width = 96;
        this.height = 96;
    }

    @Override
    public void update(double dt, Tower tower) {
        super.update(dt, tower);
        if (isDead()) return;

        auraRotation += dt * 2.5;
        shiftTimer += dt;
        if (shiftTimer >= shiftInterval) {
            shiftTimer = 0.0;
            shiftWeakness();
        }
    }

    public void shiftWeakness() {
        elementIndex = (elementIndex + 1) % SHIFT_ELEMENTS.length;
        this.weakness = SHIFT_ELEMENTS[elementIndex];
        critHitsTaken = 0;
        SoundManager.getInstance().playHit();

        String elName = switch (weakness) {
            case FIRE -> "ไฟ";
            case ICE -> "น้ำแข็ง";
            case POISON -> "กรดพิษ";
            case LIGHTNING -> "สายฟ้า";
            default -> "สากล";
        };
        addFloatingText("เกราะสลับธาตุ -> " + elName, weakness.getColor());
    }

    @Override
    public void takeDamage(int amount, ElementType element) {
        if (element == ElementType.UNIVERSAL) {
            // ยาระเบิดมหาเวททำลายเกราะเคออส รุนแรง 3 เท่า!
            hitFlashTimer = 0.20;
            int finalDamage = amount * 3;
            this.hp = Math.max(0, this.hp - finalDamage);
            SoundManager.getInstance().playCritical();
            addFloatingText("SHATTER! -" + finalDamage, new Color(255, 214, 0));
            // ฉีกเกราะแล้วสลับธาตุทันที
            shiftWeakness();
            return;
        }

        // ถ้าโจมตีตรงจุดอ่อนครบ 2 ครั้ง บอสจะสลับธาตุทันทีเพื่อความท้าทาย
        if (element == this.weakness) {
            critHitsTaken++;
            if (critHitsTaken >= 2) {
                super.takeDamage(amount, element);
                shiftWeakness();
                return;
            }
        }

        super.takeDamage(amount, element);
    }

    @Override
    public String getSpriteName() {
        return "monster_chaos_boss";
    }

    @Override
    public void render(Graphics2D g) {
        if (isDead()) return;

        // วาดวงแหวนเวทมนตร์เคออสเรืองแสงหมุนวนรอบบอส
        int cx = (int) (x + width / 2);
        int cy = (int) (y + height / 2);
        int auraR = (int) (width * 0.65);

        g.setColor(new Color(weakness.getColor().getRed(), weakness.getColor().getGreen(), weakness.getColor().getBlue(), 70));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{10, 8}, (float) (auraRotation * 10)));
        g.drawOval(cx - auraR, cy - auraR, auraR * 2, auraR * 2);

        // เรียกเรนเดอร์มาตรฐาน
        super.render(g);

        // ป้ายสัญลักษณ์ [BOSS] สีทองเหนือชื่อ
        g.setFont(FontHelper.bold(10));
        g.setColor(new Color(255, 215, 0));
        g.drawString("[BOSS]", (int) (x + width / 2 - 18), (int) y - 44);
    }
}

