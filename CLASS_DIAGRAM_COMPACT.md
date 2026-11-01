# POTION PANIC - Class Diagram (Colorful & Compact Landscape Mermaid)

โค้ดนี้ถูกปรับแต่งให้มี **สีสันสวยงาม แบ่งแยกบทบาทของคลาสตามหลัก OOP ชัดเจน** พร้อมทั้งบีบอัดความยาวของโค้ดให้กะทัดรัด (Compact ~140 บรรทัด) เพื่อไม่ให้ติดข้อจำกัดจำนวนบรรทัด (Line Limit) ในโปรแกรม Mermaid Chart

---

## 🎨 โค้ด Mermaid แนวนอนแบบเพิ่มสีสัน (Colorful & Compact)

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'lineColor': '#334155', 'fontSize': '12px'}}}%%
classDiagram
    direction LR

    %% ==========================================
    %% 1. CONTROLLERS & SINGLETON (สีฟ้าคราม Indigo)
    %% ==========================================
    class SoundManager {
        <<Singleton>>
        -SoundManager instance$
        -boolean soundEnabled
        +getInstance()$ SoundManager
        +playBgm(type) void
        +playHit() void
        +toggleSound() void
    }

    class GameEngine {
        <<Facade / Composition>>
        -Tower tower
        -Cauldron cauldron
        -ConveyorBelt belt
        -PotionRack potionRack
        -WaveManager waveManager
        -List~Monster~ activeMonsters
        -List~Projectile~ activeProjectiles
        -int score
        -int comboCount
        +startStage(int stage) void
        +grabIngredientToCauldron(slot) void
        +brewPotion() void
        +throwPotionAt(x, y) boolean
        +rerollBelt() boolean
    }

    class ProfileManager {
        <<Singleton>>
        -int starPoints
        -Map~Integer, Integer~ stageStars
        +getInstance()$ ProfileManager
        +recordStageClear(stage, stars) int
        +buyUpgrade(type) boolean
    }

    class WaveManager {
        -int currentWave
        -int totalWaves
        +update(dt, monsters, cfg) void
        +isAllWavesComplete() boolean
    }

    %% ==========================================
    %% 2. INTERFACES (สีเขียวมรกต + เส้นประ)
    %% ==========================================
    class Damageable {
        <<interface>>
        +takeDamage(amount, element)* void
        +getHp()* int
        +isDead()* boolean
    }

    class Renderable {
        <<interface>>
        +render(Graphics2D g)* void
    }

    %% ==========================================
    %% 3. ALCHEMY & DEFENSE (สีเหลืองอำพัน Amber)
    %% ==========================================
    class Tower {
        -int hp, maxHp
        +takeDamage(amount, element) void
        +repair(amount) void
        +render(Graphics2D g) void
    }

    class Cauldron {
        -List~IngredientType~ ingredients
        -double explosionTimer
        +addIngredient(item) boolean
        +removeLastIngredient() boolean
        +clear() void
        +render(Graphics2D g) void
    }

    class ConveyorBelt {
        -IngredientType[] beltSlots
        +grab(slot) IngredientType
        +reroll() boolean
        +update(dt) void
        +render(Graphics2D g) void
    }

    class PotionRack {
        -Potion[] slots
        -int selectedSlot
        +addPotion(p) boolean
        +selectSlot(index) void
        +consumeSelected() Potion
        +render(Graphics2D g) void
    }

    class RecipeBook {
        <<Utility>>
        +craft(ingredients)$ Potion
        +getSuggestions(two)$ Map
        +shouldTriggerMiracleExplosive()$ boolean
    }

    class Projectile {
        -Potion potion
        -double progress
        +update(dt, monsters, effects) void
        +render(Graphics2D g) void
    }

    %% ==========================================
    %% 4. MONSTER HIERARCHY (สีแดงทับทิม Crimson)
    %% ==========================================
    class Monster {
        <<abstract>>
        #String name
        #int hp, maxHp
        #double speed
        #ElementType weakness
        +update(dt, tower) void
        +takeDamage(amount, element) void
        +applyStatus(effect, duration) void
        +render(Graphics2D g) void
        +isDead() boolean
    }

    class PlantCreeper { +takeDamage() void }
    class FireGolem { +takeDamage() void }
    class ArmoredBeast { +takeDamage() void }
    class SwiftImp { +takeDamage() void }
    class ChaosBoss {
        -ElementType currentShield
        +shiftWeakness() void
        +takeDamage() void
    }

    %% ==========================================
    %% 5. POTION HIERARCHY (สีม่วงอเมทิสต์ Purple)
    %% ==========================================
    class Potion {
        <<abstract>>
        #String name
        #ElementType element
        #int baseDamage
        #double radius
        #Color flaskColor
        +onImpact(x, y, monsters, effects)* void
        +render(Graphics2D g) void
    }

    class FirePotion { +onImpact() void }
    class IcePotion { +onImpact() void }
    class PoisonPotion { +onImpact() void }
    class LightningPotion { +onImpact() void }
    class FrostVenomPotion { +onImpact() void }
    class ExplosivePotion { +onImpact() void }
    class FailedPotion { +onImpact() void }

    %% ==========================================
    %% 6. ENUMS (สีเทาบลู Slate)
    %% ==========================================
    class ElementType {
        <<enumeration>>
        FIRE, ICE, POISON, LIGHTNING, UNIVERSAL
    }

    class IngredientType {
        <<enumeration>>
        RED_HERB, BLUE_CRYSTAL, TOXIC_MUSHROOM, THUNDER_ROOT
    }

    %% ==========================================
    %% 7. RELATIONSHIPS
    %% ==========================================
    GameEngine ..> SoundManager : BGM/SFX
    GameEngine ..> ProfileManager : Upgrades
    Cauldron ..> RecipeBook : verifies

    GameEngine "1" *-- "1" Tower : defends
    GameEngine "1" *-- "1" Cauldron : brews
    GameEngine "1" *-- "1" ConveyorBelt : feeds
    GameEngine "1" *-- "1" PotionRack : stores
    GameEngine "1" *-- "1" WaveManager : controls
    GameEngine "1" o-- "*" Monster : battles
    GameEngine "1" o-- "*" Projectile : tracks
    Projectile "1" o-- "1" Potion : carries

    Damageable <|.. Tower
    Renderable <|.. Tower
    Renderable <|.. Cauldron
    Renderable <|.. ConveyorBelt
    Renderable <|.. PotionRack
    Renderable <|.. Projectile

    Damageable <|.. Monster
    Renderable <|.. Monster
    Renderable <|.. Potion

    Monster <|-- PlantCreeper : Weak FIRE
    Monster <|-- FireGolem : Weak ICE
    Monster <|-- ArmoredBeast : Weak POISON
    Monster <|-- SwiftImp : Weak LIGHTNING
    Monster <|-- ChaosBoss : Boss 4-Shields

    Potion <|-- FirePotion : FireBurst AoE
    Potion <|-- IcePotion : IceSpikes Slow
    Potion <|-- PoisonPotion : Acid Puddle
    Potion <|-- LightningPotion : Sky Strike
    Potion <|-- FrostVenomPotion : Freeze+Poison
    Potion <|-- ExplosivePotion : Miracle x3
    Potion <|-- FailedPotion : Smoke Penalty

    Monster --> ElementType : weakness
    Potion --> ElementType : element
    ConveyorBelt --> IngredientType : slots
    Cauldron --> IngredientType : items

    %% ==========================================
    %% 8. COLOR STYLING (ชุดสีสันระดับพรีเมียม)
    %% ==========================================
    classDef controller fill:#E8EAF6,stroke:#3F51B5,stroke-width:2px,color:#1A237E;
    classDef interfaceStyle fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,stroke-dasharray:4 4,color:#1B5E20;
    classDef itemStyle fill:#FFF8E1,stroke:#FFA000,stroke-width:2px,color:#E65100;
    classDef monsterStyle fill:#FFEBEE,stroke:#D32F2F,stroke-width:2px,color:#B71C1C;
    classDef bossStyle fill:#FCE4EC,stroke:#C2185B,stroke-width:3px,color:#880E4F;
    classDef potionStyle fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px,color:#4A148C;
    classDef rarePotion fill:#FFF9C4,stroke:#FBC02D,stroke-width:3px,color:#B58105;
    classDef enumStyle fill:#ECEFF1,stroke:#546E7A,stroke-width:1.5px,color:#263238;

    cssClass "SoundManager,GameEngine,ProfileManager,WaveManager" controller;
    cssClass "Damageable,Renderable" interfaceStyle;
    cssClass "Tower,Cauldron,ConveyorBelt,PotionRack,RecipeBook,Projectile" itemStyle;
    cssClass "Monster,PlantCreeper,FireGolem,ArmoredBeast,SwiftImp" monsterStyle;
    cssClass "ChaosBoss" bossStyle;
    cssClass "Potion,FirePotion,IcePotion,PoisonPotion,LightningPotion,FrostVenomPotion,FailedPotion" potionStyle;
    cssClass "ExplosivePotion" rarePotion;
    cssClass "ElementType,IngredientType" enumStyle;
```
