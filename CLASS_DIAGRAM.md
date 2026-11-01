# POTION PANIC - Class Diagram & OOP Architecture

เอกสารโครงสร้างสถาปัตยกรรมเชิงวัตถุ (Object-Oriented Programming) สำหรับเกม **POTION PANIC (อลวนคนปรุงยา)**
ระบบปรุงยา 3 วัตถุดิบ (3-Ingredient Alchemy System) พร้อมระบบภาพกราฟิกแฟนตาซี 2 มิติความละเอียดสูง (High-Res Digital Fantasy Art), การ์ดสอนสูตรก่อนเริ่มด่าน (Interactive Stage Briefing), ระบบสมการไอคอนภาพ, เอนจินอนิเมชันและเอฟเฟกต์ฟิสิกส์ (Visual Juice & Screen Shake), และระบบเสียงสังเคราะห์ดนตรีประกอบหลายแทร็ก (Multi-Track 16-bit Polyphonic Synthesizer)

---

## 1. รหัส Mermaid Class Diagram ฉบับสมบูรณ์ (Full Architecture)

คุณสามารถนำโค้ดบล็อกนี้ไปแสดงผลใน **GitHub Markdown**, **Notion**, หรือเว็บ [Mermaid Live Editor](https://mermaid.live) ได้ทันที

```mermaid
classDiagram
    direction TB

    %% ==========================================
    %% 1. INTERFACES (การแยกส่วนนามธรรม)
    %% ==========================================
    class Renderable {
        <<interface>>
        +render(Graphics2D g) void
    }

    class Damageable {
        <<interface>>
        +takeDamage(int amount) void
        +isDead() boolean
        +getHp() int
        +getMaxHp() int
    }

    %% ==========================================
    %% 2. ENTITIES (หอคอย มอนสเตอร์ และ เอฟเฟกต์)
    %% ==========================================
    class Tower {
        -int hp
        -int maxHp
        -double x
        -double y
        +takeDamage(int amount) void
        +repair(int amount) void
        +isDead() boolean
        +render(Graphics2D g) void
    }
    Renderable <|.. Tower
    Damageable <|.. Tower

    class Monster {
        <<abstract>>
        #String name
        #double x
        #double y
        #double speed
        #int hp
        #int maxHp
        #int attackPower
        #ElementType weakness
        #Map~StatusEffect, Double~ statusDurations
        #double hitFlashTimer
        #double walkAnimTimer
        +update(double dt, Tower tower) void
        +render(Graphics2D g) void
        +takeDamage(int amount, ElementType el) void
        +applyStatus(StatusEffect effect, double duration) void
        +isDead() boolean
        +getHp() int
        +getMaxHp() int
        +getWeakness() ElementType
    }
    Renderable <|.. Monster
    Damageable <|.. Monster

    class FireGolem {
        +FireGolem(double x, double y)
    }
    class PlantCreeper {
        +PlantCreeper(double x, double y)
    }
    class ArmoredBeast {
        +ArmoredBeast(double x, double y)
    }
    class SwiftImp {
        +SwiftImp(double x, double y)
    }
    class ChaosBoss {
        -double shiftTimer
        -double shiftInterval
        -ElementType currentShield
        +ChaosBoss(double x, double y)
        +shiftWeakness() void
        +takeDamage(int amount, ElementType el) void
    }
    Monster <|-- FireGolem
    Monster <|-- PlantCreeper
    Monster <|-- ArmoredBeast
    Monster <|-- SwiftImp
    Monster <|-- ChaosBoss

    class VisualEffect {
        <<enumeration EffectType>>
        -EffectType type
        -double x, y, radius, maxRadius
        -double duration, maxDuration
        -Color color
        +createExplosion(double x, double y, double radius, Color c)$ VisualEffect
        +createAcidPuddle(double x, double y, double radius)$ VisualEffect
        +createChainLightning(double x1, double y1, double x2, double y2)$ VisualEffect
        +createFlyingItem(IngredientType ing, Potion pot, double sx, double sy, double tx, double ty)$ VisualEffect
        +createDeathBurst(double x, double y, Color c)$ VisualEffect
        +createWaveBanner(int wave, int total)$ VisualEffect
        +update(double dt) void
        +render(Graphics2D g) void
        +isFinished() boolean
    }
    Renderable <|.. VisualEffect

    %% ==========================================
    %% 3. POTIONS & PROJECTILES (ระบบขวดยาและการขว้าง)
    %% ==========================================
    class Potion {
        <<abstract>>
        #String name
        #ElementType element
        #int baseDamage
        #double splashRadius
        #Color flaskColor
        +getName() String
        +getElement() ElementType
        +getBaseDamage() int
        +getRadius() double
        +getFlaskColor() Color
        +render(Graphics2D g) void
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects)* void
    }
    Renderable <|.. Potion

    class FirePotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class IcePotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class PoisonPotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class LightningPotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class FrostVenomPotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class ExplosivePotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    class FailedPotion {
        +onImpact(double tx, double ty, List~Monster~ targets, List~VisualEffect~ effects) void
    }
    Potion <|-- FirePotion
    Potion <|-- IcePotion
    Potion <|-- PoisonPotion
    Potion <|-- LightningPotion
    Potion <|-- FrostVenomPotion
    Potion <|-- ExplosivePotion
    Potion <|-- FailedPotion

    class Projectile {
        -Potion potion
        -double currentX
        -double currentY
        -double targetX
        -double targetY
        -double progress
        +update(double dt) void
        +render(Graphics2D g) void
        +isArrived() boolean
        +explode(List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    Renderable <|.. Projectile
    Projectile o-- Potion

    %% ==========================================
    %% 4. ITEMS & ALCHEMY (ระบบการปรุงยาและโต๊ะทำงาน 3 วัตถุดิบ)
    %% ==========================================
    class IngredientType {
        <<enumeration>>
        RED_HERB
        BLUE_CRYSTAL
        TOXIC_MUSHROOM
        THUNDER_ROOT
        +getName() String
        +getElement() ElementType
        +getColor() Color
    }

    class ElementType {
        <<enumeration>>
        FIRE
        ICE
        POISON
        LIGHTNING
        UNIVERSAL
        +getName() String
        +getColor() Color
    }

    class Cauldron {
        -List~IngredientType~ slots
        -double explosionTimer
        +addIngredient(IngredientType ing) boolean
        +removeLastIngredient() IngredientType
        +brewPotion() Potion
        +clear() void
        +isExploded() boolean
        +update(double dt) void
        +render(Graphics2D g) void
    }
    Renderable <|.. Cauldron
    Cauldron o-- IngredientType
    Cauldron ..> RecipeBook : queries suggestions & crafts

    class ConveyorBelt {
        -IngredientType[] slots
        -double[] respawnTimers
        -double rerollCooldownTimer
        +grab(int index) IngredientType
        +getSmartIngredient(int slotIndex) IngredientType
        +getRequiredIngredientsForWeakness(ElementType el) List~IngredientType~
        +rerollAll() boolean
        +rerollAll(List~Monster~ monsters, List~IngredientType~ cauldron, List~ElementType~ upcoming) boolean
        +update(double dt) void
        +update(double dt, List~Monster~ monsters, List~IngredientType~ cauldron, List~ElementType~ upcoming) void
        +render(Graphics2D g) void
    }
    Renderable <|.. ConveyorBelt
    ConveyorBelt o-- IngredientType

    class PotionRack {
        -Potion[] slots
        -int selectedIndex
        +addPotion(Potion p) boolean
        +useSelectedPotion() Potion
        +selectSlot(int index) void
        +render(Graphics2D g) void
    }
    Renderable <|.. PotionRack
    PotionRack o-- Potion

    class RecipeBook {
        +isValidRecipe(List~IngredientType~ ingredients)$ boolean
        +previewCraft(List~IngredientType~ ingredients)$ Potion
        +craft(List~IngredientType~ ingredients)$ Potion
        +getSuggestionsForTwo(List~IngredientType~ two)$ Map~IngredientType, Potion~
        +shouldTriggerMiracleExplosive(boolean isClutch)$ boolean
    }
    RecipeBook ..> Potion : creates

    %% ==========================================
    %% 5. CORE ENGINE & MANAGERS
    %% ==========================================
    class GameEngine {
        -Tower tower
        -Cauldron cauldron
        -ConveyorBelt belt
        -PotionRack rack
        -WaveManager waveManager
        -StageConfig currentStage
        -List~Monster~ monsters
        -List~Projectile~ projectiles
        -List~VisualEffect~ activeEffects
        -int score
        -int comboStreak
        -double screenShakeIntensity
        -double screenShakeDuration
        +update(double dt) void
        +throwPotion(double targetX, double targetY) void
        +pickIngredient(int index) void
        +rerollBelt() void
        +brew() void
        +checkCollisions() void
        +triggerScreenShake(double intensity, double duration) void
        +getScreenShakeOffset() Point
    }
    GameEngine *-- Tower
    GameEngine *-- Cauldron
    GameEngine *-- ConveyorBelt
    GameEngine *-- PotionRack
    GameEngine *-- WaveManager
    GameEngine o-- Monster
    GameEngine o-- Projectile
    GameEngine o-- VisualEffect

    class WaveManager {
        -int currentWave
        -int totalWaves
        -double spawnCooldown
        +update(double dt, List~Monster~ monsters, StageConfig cfg) void
        +isWaveCleared() boolean
        +isAllWavesComplete() boolean
        +getUpcomingWeaknesses() List~ElementType~
    }

    class StageConfig {
        -int stageNumber
        -String title
        -String subtitle
        -List~IngredientType~ allowedIngredients
        -int totalWaves
        -boolean endless
        +forStage(int num)$ StageConfig
        +getShortTitle() String
    }

    class ProfileManager {
        -int starPoints
        -int totalStarsEarned
        -Map~Integer, Integer~ stageStars
        -int towerHpLevel
        -int conveyorSpeedLevel
        -int potionRadiusLevel
        -int cauldronRecoveryLevel
        +getInstance()$ ProfileManager
        +recordStageClear(int stage, int stars) int
        +recordEndlessProgress(int waves) int
        +resetUpgrades() int
        +buyUpgrade(UpgradeType type) boolean
        +canUpgrade(UpgradeType type) boolean
        +getMaxLevel(UpgradeType type) int
        +getBonusTowerHp() int
        +getBeltRespawnDuration() double
        +getPotionRadiusMultiplier() double
        +getCauldronExplosionDuration() double
    }

    class SoundManager {
        -boolean soundMuted
        +getInstance()$ SoundManager
        +playMenuBgm() void
        +playBattleBgm() void
        +toggleSound() void
        +isMuted() boolean
        +playMagicChime(double freq, double dur) void
        +playBubbleTone() void
        +playCombo(int combo) void
        +updateLowHpAlert(boolean active) void
    }

    class AssetLoader {
        +getImage(String key)$ BufferedImage
        +ensureTransparentBackground(BufferedImage img)$ BufferedImage
    }

    class VectorArtRenderer {
        +createProceduralImage(String name, int w, int h)$ BufferedImage
        +drawPotionBottle(Graphics2D g, int w, int h, Color main, Color glow, String label, ElementType el)$ void
        +drawVectorElementSymbol(Graphics2D g, int cx, int cy, int r, ElementType el)$ void
    }

    %% ==========================================
    %% 6. UI LAYER (หน้าจอ Swing & โอเวอร์เลย์สอนสูตร)
    %% ==========================================
    class GameWindow {
        -CardLayout cardLayout
        -JPanel mainContainer
        +showScene(String name) void
        +startStage(int stageNumber) void
    }

    class StageIntroOverlay {
        -StageConfig config
        -int currentPage
        -boolean active
        -double animTimer
        +show(StageConfig config) void
        +update(double dt) void
        +render(Graphics2D g) void
        +handleKeyPress(int keyCode) boolean
        +handleClick(int mx, int my) boolean
        +isActive() boolean
    }

    class GameplayPanel {
        -GameEngine engine
        -StageIntroOverlay stageIntro
        +showStageIntro(StageConfig cfg) void
        +paintComponent(Graphics g) void
    }

    class MainMenuPanel {
        -double bobbingTimer
        +paintComponent(Graphics g) void
    }

    class StageSelectPanel {
        -int selectedStage
        +paintComponent(Graphics g) void
        +drawVectorStar(Graphics2D g, int cx, int cy, int outerR, int innerR, Color fill, Color outline)$ void
    }

    class VictoryPanel {
        -int earnedStars
        +paintComponent(Graphics g) void
    }

    class GameOverPanel {
        -int finalScore
        +paintComponent(Graphics g) void
    }

    class RecipeBookDialog {
        +show(Component parent) void
    }

    GameWindow *-- MainMenuPanel
    GameWindow *-- GameplayPanel
    GameWindow *-- StageSelectPanel
    GameWindow *-- VictoryPanel
    GameWindow *-- GameOverPanel
    GameplayPanel *-- StageIntroOverlay
    GameplayPanel --> GameEngine
    GameplayPanel ..> RecipeBookDialog : opens
    GameplayPanel ..> AssetLoader : loads PNGs
    AssetLoader ..> VectorArtRenderer : fallback
    StageSelectPanel ..> ProfileManager : uses
    GameplayPanel ..> SoundManager : calls
    MainMenuPanel ..> SoundManager : calls
```

---

## 2. การประยุกต์ใช้หลักการเชิงวัตถุ (OOP Concepts Applied)

| หลักการ OOP | รูปแบบที่ใช้งานในเกม POTION PANIC | ประโยชน์ |
| :--- | :--- | :--- |
| **Abstraction** | อินเทอร์เฟซ `Renderable`, `Damageable` และคลาสแม่ `Monster`, `Potion` | แยกสัญญาการทำงาน (Contract) ออกจากรายละเอียดภายใน ทำให้ `GameEngine` สั่งวาดภาพหรือคำนวณดาเมจได้โดยไม่ต้องรู้ชนิดเฉพาะของอ็อบเจกต์ |
| **Inheritance** | `Monster` $\rightarrow$ `FireGolem`, `PlantCreeper`, `ArmoredBeast`, `SwiftImp`, `ChaosBoss`<br>`Potion` $\rightarrow$ `FirePotion`, `IcePotion`, `PoisonPotion`, `LightningPotion`, `FrostVenomPotion`, `ExplosivePotion`, `FailedPotion` | นำโค้ดตำแหน่ง พิกัด การเคลื่อนที่ หลอดเลือด และระบบแอนิเมชันมาใช้งานซ้ำ ลดความซ้ำซ้อนของโค้ดลงอย่างมีนัยสำคัญ |
| **Polymorphism** | เมธอด `onImpact(...)` ในแต่ละคลาสลูกของ `Potion`<br>เมธอด `update()` และ `takeDamage(...)` ใน `Monster` และ `ChaosBoss` | ขวดยาแต่ละชนิดสร้างผลลัพธ์บนสนามรบต่างกัน (บ่อพิษกัดกร่อน, สายฟ้าชิ่ง, สโลว์น้ำแข็ง, ระเบิดมหาเวท) โดยถูกเรียกผ่านตัวแปรคลาสแม่ `Potion` ได้อย่างยืดหยุ่น |
| **Encapsulation** | ฟิลด์สถานะทั้งหมดเป็น `private`/`protected` มีการเข้าถึงผ่าน Getter/Setter และเมธอดที่มีเงื่อนไขตรวจสอบ | ป้องกันการแก้ไขค่าพลังชีวิต สล็อตวัตถุดิบ หรือจำนวนดาวจากภายนอกโดยไม่ผ่านการคำนวณที่ถูกต้อง |
| **Composition & Aggregation** | `GameEngine` ประกอบด้วย `Tower`, `Cauldron`, `PotionRack`, `ConveyorBelt`, `WaveManager`<br>รวมถึงเก็บคอลเลกชันของ `Monster`, `Projectile`, และ `VisualEffect` | การประกอบอ็อบเจกต์ย่อยที่มีหน้าที่จำเพาะ (Single Responsibility) มารวมกันเป็นระบบเกมเพลย์ที่สมบูรณ์และเป็นระเบียบ |
| **Singleton Pattern** | `ProfileManager.getInstance()`, `SoundManager.getInstance()` | เป็นศูนย์กลางจัดการข้อมูลคะแนน เซฟเกม และระบบเสียงเวทมนตร์เพียงจุดเดียวตลอดการทำงานของแอปพลิเคชัน |
