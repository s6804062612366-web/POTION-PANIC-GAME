# POTION PANIC - แผนภาพ Class Diagram แนวนอน (Mermaid Syntax - Landscape / Direction LR)

เอกสารนี้รวบรวมโค้ด Mermaid Syntax สำหรับนำไปวางใน [Mermaid Live Editor](https://mermaid.live), GitHub Markdown, Notion หรือเครื่องมือสร้าง Diagram โดยจัดวางในทิศทาง **แนวนอน (direction LR)** เพื่อให้เห็นภาพรวมและสถาปัตยกรรม OOP ได้อย่างชัดเจนที่สุด

---

## รูปแบบที่ 1: Core OOP Architecture (ฉบับเน้น 6 เสาหลัก OOP - แนะนำที่สุดสำหรับรายงาน)
> **จุดเด่น:** จัดวางแนวนอนจากซ้ายไปขวา (Singleton -> Engine -> Interfaces -> Concrete Classes) ตัวหนังสือไม่แน่นจนเกินไป มองเห็นสายสืบทอด (Inheritance) และพหุสัณฐาน (Polymorphism) ได้ชัดเจนที่สุด

```mermaid
classDiagram
    direction LR

    %% ==========================================
    %% 1. SINGLETON & CONTROLLER (ซ้ายสุด)
    %% ==========================================
    class SoundManager {
        <<Singleton>>
        -SoundManager instance$
        -boolean soundEnabled
        -SourceDataLine activeBgmLine
        +getInstance()$ SoundManager
        +playBgm(BgmType type) void
        +playHit() void
        +playGrab() void
        +toggleSound() void
    }

    class GameEngine {
        <<Facade / Composition>>
        -Tower tower
        -ConveyorBelt belt
        -Cauldron cauldron
        -PotionRack potionRack
        -WaveManager waveManager
        -List~Monster~ activeMonsters
        -List~Projectile~ activeProjectiles
        -int score
        -int comboCount
        +startStage(int stage) void
        +grabIngredientToCauldron(int slot) void
        +brewPotion() void
        +throwPotionAt(double x, double y) boolean
        +rerollBelt() boolean
    }

    %% ==========================================
    %% 2. INTERFACES (การแยกส่วนนามธรรม)
    %% ==========================================
    class Renderable {
        <<interface>>
        +render(Graphics2D g)* void
    }

    class Damageable {
        <<interface>>
        +takeDamage(int amount, ElementType el)* void
        +getHp()* int
        +getMaxHp()* int
        +isDead()* boolean
    }

    %% ==========================================
    %% 3. COMPOSITION ENTITIES (อุปกรณ์และฐาน)
    %% ==========================================
    class Tower {
        -int hp
        -int maxHp
        +takeDamage(int amount, ElementType el) void
        +repair(int amount) void
        +render(Graphics2D g) void
    }

    class Cauldron {
        -List~IngredientType~ ingredients
        -double explosionTimer
        +addIngredient(IngredientType item) boolean
        +removeLastIngredient() boolean
        +clear() void
        +isExploded() boolean
        +render(Graphics2D g) void
    }

    class ConveyorBelt {
        -IngredientType[] beltSlots
        -double respawnDuration
        +grab(int slot) IngredientType
        +reroll() boolean
        +update(double dt) void
        +render(Graphics2D g) void
    }

    class PotionRack {
        -Potion[] slots
        -int selectedSlot
        +addPotion(Potion p) boolean
        +selectSlot(int index) void
        +consumeSelected() Potion
        +render(Graphics2D g) void
    }

    %% ==========================================
    %% 4. MONSTER HIERARCHY (สายสืบทอดมอนสเตอร์)
    %% ==========================================
    class Monster {
        <<abstract>>
        #String name
        #int hp
        #int maxHp
        #double speed
        #ElementType weakness
        #Map~StatusEffect, Double~ statusDurations
        +update(double dt, Tower tower) void
        +takeDamage(int amount, ElementType el) void
        +applyStatus(StatusEffect eff, double dur) void
        +render(Graphics2D g) void
        +isDead() boolean
    }

    class PlantCreeper {
        +PlantCreeper(double x, double y)
    }
    class FireGolem {
        +FireGolem(double x, double y)
    }
    class ArmoredBeast {
        +ArmoredBeast(double x, double y)
    }
    class SwiftImp {
        +SwiftImp(double x, double y)
    }
    class ChaosBoss {
        -double shiftTimer
        -ElementType currentShield
        +ChaosBoss(double x, double y)
        +shiftWeakness() void
        +takeDamage(int amount, ElementType el) void
    }

    %% ==========================================
    %% 5. POTION HIERARCHY (สายพหุสัณฐานขวดยา)
    %% ==========================================
    class Potion {
        <<abstract>>
        #String name
        #ElementType element
        #int baseDamage
        #double radius
        #Color flaskColor
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects)* void
        +render(Graphics2D g) void
        +getRadius() double
        +getFlaskColor() Color
    }

    class FirePotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    class IcePotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    class PoisonPotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    class LightningPotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    class FrostVenomPotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }
    class ExplosivePotion {
        +onImpact(double x, double y, List~Monster~ monsters, List~VisualEffect~ effects) void
    }

    %% ==========================================
    %% 6. RELATIONSHIPS & CONNECTORS
    %% ==========================================
    %% Singleton
    GameEngine ..> SoundManager : uses sound

    %% Composition (Has-A)
    GameEngine *-- Tower
    GameEngine *-- Cauldron
    GameEngine *-- ConveyorBelt
    GameEngine *-- PotionRack
    GameEngine o-- Monster : manages
    GameEngine o-- Potion : throws

    %% Realization (Implements)
    Damageable <|.. Tower
    Renderable <|.. Tower
    Renderable <|.. Cauldron
    Renderable <|.. ConveyorBelt
    Renderable <|.. PotionRack

    Damageable <|.. Monster
    Renderable <|.. Monster
    Renderable <|.. Potion

    %% Monster Inheritance (Extends)
    Monster <|-- PlantCreeper : Weak FIRE
    Monster <|-- FireGolem : Weak ICE
    Monster <|-- ArmoredBeast : Weak POISON
    Monster <|-- SwiftImp : Weak LIGHTNING
    Monster <|-- ChaosBoss : Boss Shift Shield

    %% Potion Inheritance & Polymorphism (Extends)
    Potion <|-- FirePotion : Fire Burst AoE
    Potion <|-- IcePotion : Ice Spikes Slow
    Potion <|-- PoisonPotion : Acid Puddle
    Potion <|-- LightningPotion : Sky Strike Stun
    Potion <|-- FrostVenomPotion : Freeze & Poison
    Potion <|-- ExplosivePotion : Miracle RARE x3
```

---

## รูปแบบที่ 2: Full Architecture (ฉบับครอบคลุมทุกคลาสและ UI)

```mermaid
classDiagram
    direction LR

    %% UI LAYER
    class GameWindow {
        -CardLayout cardLayout
        +showScene(String name) void
        +startStage(int stageNumber) void
        +toggleFullScreen() void
    }
    class MainMenuPanel { +paintComponent(Graphics g) void }
    class StageSelectPanel { +paintComponent(Graphics g) void }
    class GameplayPanel { +paintComponent(Graphics g) void }
    class VictoryPanel { +paintComponent(Graphics g) void }
    class GameOverPanel { +paintComponent(Graphics g) void }
    class StageIntroOverlay { +render(Graphics2D g) void }

    GameWindow *-- MainMenuPanel
    GameWindow *-- StageSelectPanel
    GameWindow *-- GameplayPanel
    GameWindow *-- VictoryPanel
    GameWindow *-- GameOverPanel
    GameplayPanel *-- StageIntroOverlay

    %% CONTROLLERS
    class GameEngine {
        -Tower tower
        -ConveyorBelt belt
        -Cauldron cauldron
        -PotionRack potionRack
        -WaveManager waveManager
        +update(double dt) void
        +throwPotionAt(double x, double y) boolean
    }
    class WaveManager {
        -int currentWave
        +update(double dt) void
    }
    class ProfileManager {
        <<Singleton>>
        -int starPoints
        +recordStageClear(stage, stars) int
        +buyUpgrade(type) boolean
    }
    class SoundManager {
        <<Singleton>>
        +playBgm(type) void
        +toggleSound() void
    }

    GameplayPanel --> GameEngine
    GameEngine *-- WaveManager
    GameEngine ..> ProfileManager
    GameEngine ..> SoundManager

    %% INTERFACES & ENTITIES
    class Renderable { <<interface>> +render(Graphics2D g)* void }
    class Damageable { <<interface>> +takeDamage(int amount, ElementType el)* void }

    class Tower { -int hp, maxHp }
    class ConveyorBelt { -IngredientType[] beltSlots }
    class Cauldron { -List~IngredientType~ ingredients }
    class PotionRack { -Potion[] slots }
    class RecipeBook { +craft(ingredients)$ Potion }

    GameEngine *-- Tower
    GameEngine *-- ConveyorBelt
    GameEngine *-- Cauldron
    GameEngine *-- PotionRack
    Cauldron ..> RecipeBook

    Damageable <|.. Tower
    Renderable <|.. Tower
    Renderable <|.. Cauldron
    Renderable <|.. ConveyorBelt
    Renderable <|.. PotionRack

    %% MONSTERS
    class Monster { <<abstract>> #ElementType weakness }
    class PlantCreeper { +PlantCreeper(x, y) }
    class FireGolem { +FireGolem(x, y) }
    class ArmoredBeast { +ArmoredBeast(x, y) }
    class SwiftImp { +SwiftImp(x, y) }
    class ChaosBoss { +ChaosBoss(x, y) }

    Damageable <|.. Monster
    Renderable <|.. Monster
    Monster <|-- PlantCreeper
    Monster <|-- FireGolem
    Monster <|-- ArmoredBeast
    Monster <|-- SwiftImp
    Monster <|-- ChaosBoss
    GameEngine o-- Monster

    %% POTIONS & PROJECTILE
    class Potion { <<abstract>> +onImpact()* void }
    class FirePotion { +onImpact() void }
    class IcePotion { +onImpact() void }
    class PoisonPotion { +onImpact() void }
    class LightningPotion { +onImpact() void }
    class FrostVenomPotion { +onImpact() void }
    class ExplosivePotion { +onImpact() void }
    class FailedPotion { +onImpact() void }
    class Projectile { -Potion potion }

    Renderable <|.. Potion
    Renderable <|.. Projectile
    Potion <|-- FirePotion
    Potion <|-- IcePotion
    Potion <|-- PoisonPotion
    Potion <|-- LightningPotion
    Potion <|-- FrostVenomPotion
    Potion <|-- ExplosivePotion
    Potion <|-- FailedPotion

    Projectile o-- Potion
    GameEngine o-- Projectile
```

