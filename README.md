# HUD Stuff (Fabric 26.3)

Java Fabric port of **HUD Essentials** (Bedrock addon by SandBlock Studios / Marketplace Trader) for Minecraft **26.3**.

From-scratch rewrite: Bedrock scripts → Fabric client APIs.

## Features

| Feature | Status | How |
|--------|--------|-----|
| XYZ / block / facing / chunk | Done | Overlay |
| Biome / light / day-clock / moon | Done | Overlay |
| FPS / speed (b/s) | Done | Overlay |
| Armor durability H/C/L/F % | Done | Overlay |
| XP level + progress | Done | Overlay |
| Held item + durability | Done | Overlay |
| Arrow count | Done | Overlay |
| Inventory fullness warn | Done | Overlay |
| Last death + distance | Done | Auto on death |
| **Chunk borders** (F3+G style) | Done | `.chunk` |
| **Slime chunk** indicator | Done | Overlay (SP) |
| **Look-at block** info | Done | Overlay |
| **Distance measure** A→B | Done | `.measure a/b` |
| **15 waypoints** | Done | `.wp set/list/del` |
| Toggle HUD | Done | `.hud` |
| Settings UI screen | Planned | |
| Crop / farmland info | Planned | |
| Dynamic light | Planned | |
| Shape / block counter tools | Planned | |
| Redstone / honey / mob lines | Planned | |

## Commands (client chat — not sent to server)

```
.hud                  toggle overlay
.hud help             list commands
.chunk                toggle chunk borders (same wireframe as F3+G)
.wp set [name]        add waypoint at feet (max 15)
.wp list              list waypoints
.wp del <n>           delete waypoint number n
.wp clear             clear all waypoints
.measure a            set measure point A
.measure b            set measure point B
.measure clear        clear measure
.death clear          clear death marker
```

## Install

1. Minecraft **26.3**
2. Fabric Loader **≥ 0.19.5**
3. Fabric API **0.165.0+26.3**
4. Jar from Actions → `mods/`

## Build

Java 25 + Gradle 9.6+

```bash
gradle build
```

## License

MIT (port). Original Bedrock addon © SandBlock Studios / Marketplace Trader.
