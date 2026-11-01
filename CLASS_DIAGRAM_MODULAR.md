# POTION PANIC - แผนภาพ Class Diagram ฉบับแยก 3 ส่วนเชื่อมโยงสมบูรณ์ (100% True-to-Code & Modular Mermaid)

รวมโค้ด Mermaid Syntax แบบแยกเป็น 3 แผนภาพย่อยตามสถาปัตยกรรมและหลักการ OOP ที่ถูกต้องตรงตามซอร์สโค้ดเกมจริง 100% แสดงความเชื่อมโยงระหว่างระบบครบวงจร ออกแบบในทิศทางแนวนอน (`direction LR`) และลงสีสันสวยงาม อ่านง่าย ชัดเจน 100% ไม่มีปัญหาเรื่อง Parser หรือขอบตัวอักษรแหว่ง

---

## 🎨 แผนภาพที่ 1: สถาปัตยกรรมระบบหลัก หน้าจอ GUI และความสัมพันธ์แบบประกอบ (Core Engine, UI & Composition)
> **วางในรายงาน:** บทที่ 2 หัวข้อ **Composition (การประกอบ Has-A)**, **Encapsulation**, และ **Singleton Pattern**  
> **แนวคิด:** แสดงลำดับชั้นตั้งแต่หน้าต่างแสดงผล (`GameWindow`, `GameplayPanel`) ควบคุมเอนจินเกม (`GameEngine`) ซึ่งประกอบขึ้นจาก (`*--`) อุปกรณ์บนโต๊ะปรุงยา (`Tower`, `ConveyorBelt`, `Cauldron`, `PotionRack`) การเรียกใช้ตำราสูตรยา (`RecipeBook`) และคลาสผู้จัดการระดับโลกแบบ Singleton (`SoundManager`, `ProfileManager`)

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'lineColor': '#334155'}}}%%
classDiagram
    direction LR

    class GameWindow {
        -CardLayout cardLayout
        -GameEngine engine
        -GameplayPanel gameplayPanel
        +showScene(name) void
        +toggleFullScreen() void
    }

    class GameplayPanel {
        -GameEngine engine
        -Timer gameLoopTimer
        -StageIntroOverlay introOverlay
        +showStageIntro(config) void
        +paintComponent(Graphics g) void
    }

    class SoundManager {
        <<Singleton>>
        -SoundManager instance$
        -boolean soundEnabled
        +getInstance()$ SoundManager
        +playBgm(BgmType type) void
        +playHit() void
        +toggleSound() void
    }

    class ProfileManager {
        <<Singleton>>
        -ProfileManager instance$
        -int unlockedStage
        -int starPoints
        +getInstance()$ ProfileManager
        +recordStageClear(stage, stars) int
        +buyUpgrade(UpgradeType type) boolean
    }

    class GameEngine {
        <<Facade / Controller>>
        -Tower tower
        -ConveyorBelt belt
        -Cauldron cauldron
        -PotionRack potionRack
        -WaveManager waveManager
        -List~Monster~ activeMonsters
        -List~Projectile~ activeProjectiles
        -List~VisualEffect~ activeEffects
        +startStage(stageNumber) void
        +update(double dt) void
        +grabIngredientToCauldron(slotIndex) void
        +brewPotion() void
        +throwPotionAt(targetX, targetY) boolean
        +rerollBelt() boolean
    }

    class Tower {
        -int hp
        -int maxHp
        +takeDamage(amount, element) void
        +isDead() boolean
        +reset() void
        +render(Graphics2D g) void
    }

    class ConveyorBelt {
        -IngredientType[] slots
        -double[] respawnTimers
        +update(double dt) void
        +grab(slotIndex) IngredientType
        +getSmartIngredient(slotIndex) IngredientType
        +rerollAll() boolean
        +render(Graphics2D g) void
    }

    class Cauldron {
        -List~IngredientType~ ingredients
        -double explosionTimer
        +addIngredient(item) boolean
        +removeLastIngredient() boolean
        +brew() Potion
        +clear() void
        +render(Graphics2D g) void
    }

    class PotionRack {
        -Potion[] slots
        -int selectedIndex
        +addPotion(potion) boolean
        +selectSlot(index) void
        +consumeSelected() Potion
        +render(Graphics2D g) void
    }

    class RecipeBook {
        <<Utility>>
        -List~RecipeEntry~ RECIPES$
        +craft(ingredients)$ Potion
        +isValidRecipe(ingredients)$ boolean
        +shouldTriggerMiracleExplosive(isClutch)$ boolean
    }

    GameWindow "1" *-- "1" GameEngine : owns
    GameWindow "1" *-- "1" GameplayPanel : displays
    GameplayPanel --> GameEngine : updates & renders
    GameEngine ..> SoundManager : plays BGM / SFX
    GameEngine ..> ProfileManager : loads upgrades
    GameEngine "1" *-- "1" Tower : defends
    GameEngine "1" *-- "1" ConveyorBelt : feeds
    GameEngine "1" *-- "1" Cauldron : brews
    GameEngine "1" *-- "1" PotionRack : stores
    Cauldron ..> RecipeBook : validates & crafts

    classDef uiStyle fill:#E0F2F1,stroke:#00695C,stroke-width:2px,color:#004D40;
    classDef controller fill:#E8EAF6,stroke:#3F51B5,stroke-width:2px,color:#1A237E;
    classDef itemStyle fill:#FFF8E1,stroke:#FFA000,stroke-width:2px,color:#E65100;

    class GameWindow:::uiStyle
    class GameplayPanel:::uiStyle
    class SoundManager:::controller
    class ProfileManager:::controller
    class GameEngine:::controller
    class Tower:::itemStyle
    class ConveyorBelt:::itemStyle
    class Cauldron:::itemStyle
    class PotionRack:::itemStyle
    class RecipeBook:::itemStyle
```

---

## 🎨 แผนภาพที่ 2: ระบบปล่อยเวฟ อินเทอร์เฟซ และสายการสืบทอดมอนสเตอร์ (WaveManager, Interfaces & Monster Inheritance)
> **วางในรายงาน:** บทที่ 2 หัวข้อ **Abstraction (อินเทอร์เฟซและคลาสนามธรรม)** และ **Inheritance (การสืบทอดคุณสมบัติ)**  
> **แนวคิด:** เชื่อมต่อจาก `GameEngine` ผ่าน `WaveManager` และ `Tower` เพื่อแสดงการ Implement อินเทอร์เฟซ `Damageable` และ `Renderable` และแสดงคลาสแม่ `Monster` สืบทอดไปยังมอนสเตอร์ทั้ง 5 ชนิด พร้อมเมธอดที่ Override จริงในโค้ด

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'lineColor': '#334155'}}}%%
classDiagram
    direction LR

    class Damageable {
        <<interface>>
        +takeDamage(amount, element) void
        +isDead() boolean
        +getHp() int
        +getMaxHp() int
    }

    class Renderable {
        <<interface>>
        +render(Graphics2D g) void
    }

    class Tower {
        -int hp
        -int maxHp
        +takeDamage(amount, element) void
        +isDead() boolean
        +render(Graphics2D g) void
    }

    class WaveManager {
        -StageConfig stageConfig
        -int currentWave
        -int totalWaves
        -Queue~Monster~ spawnQueue
        +startWave(waveNumber) void
        +update(dt, activeMonsters) void
        +getUpcomingWeaknesses() List~ElementType~
        +isAllCompleted() boolean
    }

    class Monster {
        <<abstract>>
        #String name
        #int hp
        #int maxHp
        #int attackPower
        #double baseSpeed
        #ElementType weakness
        #Map~StatusEffect, Double~ statusDurations
        +update(dt, tower) void
        +takeDamage(amount, element) void
        +applyStatus(effect, duration) void
        +getSpriteName() String
        +render(Graphics2D g) void
    }

    class PlantCreeper {
        -double regenTimer
        +update(dt, tower) void
        +getSpriteName() String
    }

    class FireGolem {
        +applyStatus(effect, duration) void
        +getSpriteName() String
    }

    class ArmoredBeast {
        +getSpriteName() String
    }

    class SwiftImp {
        +getSpriteName() String
    }

    class ChaosBoss {
        -double shiftTimer
        -int elementIndex
        -int critHitsTaken
        +update(dt, tower) void
        +shiftWeakness() void
        +takeDamage(amount, element) void
        +getSpriteName() String
    }

    Damageable <|.. Tower
    Renderable <|.. Tower
    Damageable <|.. Monster
    Renderable <|.. Monster
    WaveManager "1" o-- "*" Monster : spawns queue
    Monster --> Tower : attacks on reach

    Monster <|-- PlantCreeper : Weak FIRE (Regen +2 HP/s)
    Monster <|-- FireGolem : Weak ICE (Immune Burn)
    Monster <|-- ArmoredBeast : Weak POISON (Heavy Armor)
    Monster <|-- SwiftImp : Weak LIGHTNING (Fast Speed)
    Monster <|-- ChaosBoss : Boss (Shifts 4 Shields)

    classDef iface fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,stroke-dasharray:4 4,color:#1B5E20;
    classDef managerStyle fill:#E8EAF6,stroke:#3F51B5,stroke-width:2px,color:#1A237E;
    classDef towerStyle fill:#FFF8E1,stroke:#FFA000,stroke-width:2px,color:#E65100;
    classDef baseClass fill:#FFEBEE,stroke:#D32F2F,stroke-width:2.5px,color:#B71C1C;
    classDef monsterSub fill:#FFF1F2,stroke:#F43F5E,stroke-width:1.5px,color:#9F1239;
    classDef bossSub fill:#FCE4EC,stroke:#C2185B,stroke-width:2.5px,color:#880E4F;

    class Damageable:::iface
    class Renderable:::iface
    class WaveManager:::managerStyle
    class Tower:::towerStyle
    class Monster:::baseClass
    class PlantCreeper:::monsterSub
    class FireGolem:::monsterSub
    class ArmoredBeast:::monsterSub
    class SwiftImp:::monsterSub
    class ChaosBoss:::bossSub
```

---

## 🎨 แผนภาพที่ 3: ระบบวิถีขวดยา เอฟเฟกต์สนามรบ และสายพหุสัณฐานขวดยาเวทมนตร์ (Projectile, VisualEffect & Potion Polymorphism)
> **วางในรายงาน:** บทที่ 2 หัวข้อ **Polymorphism (พหุสัณฐาน)** และ **Dynamic Method Dispatch**  
> **แนวคิด:** เชื่อมต่อจาก `GameEngine` ผ่าน `Projectile` และ `VisualEffect` แสดงการทำงานร่วมกันเมื่อขวดยาตกกระทบพื้นสนามรบ และแสดงคลาสแม่ `Potion` สืบทอดไปยังคลาสลูกขวดยาครบทั้ง 7 ชนิดตามโค้ดจริง

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'lineColor': '#334155'}}}%%
classDiagram
    direction LR

    class Renderable {
        <<interface>>
        +render(Graphics2D g) void
    }

    class Projectile {
        -Potion potion
        -double startX
        -double startY
        -double targetX
        -double targetY
        -double progress
        +update(dt, monsters, effects) void
        +isDead() boolean
        +render(Graphics2D g) void
    }

    class VisualEffect {
        -EffectType type
        -double x
        -double y
        -double radius
        -double lifetime
        +createFireBurst(x, y, r)$ VisualEffect
        +createIceSpikes(x, y, r)$ VisualEffect
        +createLingeringPuddle(x, y, r, c)$ VisualEffect
        +createSkyLightningStrike(x, y, r)$ VisualEffect
        +createSupernova(x, y, r)$ VisualEffect
        +update(double dt) void
        +render(Graphics2D g) void
    }

    class Potion {
        <<abstract>>
        #double globalRadiusMultiplier$
        #String name
        #ElementType element
        #int baseDamage
        #Color flaskColor
        #String spriteName
        +getImage() BufferedImage
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class FirePotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class IcePotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class PoisonPotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class LightningPotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class FrostVenomPotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class ExplosivePotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    class FailedPotion {
        +getRadius() double
        +onImpact(x, y, monsters, effects) void
    }

    Renderable <|.. Projectile
    Renderable <|.. VisualEffect
    Projectile "1" *-- "1" Potion : carries & triggers
    Potion ..> VisualEffect : spawns AoE effects

    Potion <|-- FirePotion : FireBurst AoE + Burn 4s
    Potion <|-- IcePotion : IceSpikes + Freeze Slow 50%
    Potion <|-- PoisonPotion : Acid Puddle + Poison DoT 5s
    Potion <|-- LightningPotion : Sky Strike + Chain Stun 1s
    Potion <|-- FrostVenomPotion : Dual Ice/Poison + Slow & DoT
    Potion <|-- ExplosivePotion : Supernova Universal x3 Boss
    Potion <|-- FailedPotion : Dud Smoke (Low Damage)

    classDef iface fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,stroke-dasharray:4 4,color:#1B5E20;
    classDef entityStyle fill:#E0F7FA,stroke:#00838F,stroke-width:2px,color:#006064;
    classDef basePotion fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2.5px,color:#4A148C;
    classDef subPotion fill:#FAF5FF,stroke:#A855F7,stroke-width:1.5px,color:#6B21A8;
    classDef rarePotion fill:#FFF9C4,stroke:#FBC02D,stroke-width:2.5px,color:#B58105;
    classDef dudPotion fill:#ECEFF1,stroke:#607D8B,stroke-width:1.5px,color:#37474F;

    class Renderable:::iface
    class Projectile:::entityStyle
    class VisualEffect:::entityStyle
    class Potion:::basePotion
    class FirePotion:::subPotion
    class IcePotion:::subPotion
    class PoisonPotion:::subPotion
    class LightningPotion:::subPotion
    class FrostVenomPotion:::subPotion
    class ExplosivePotion:::rarePotion
    class FailedPotion:::dudPotion
```
