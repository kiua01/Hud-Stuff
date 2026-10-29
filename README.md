# HUD Stuff (Fabric 26.3)

Java Fabric port of **HUD Essentials** (Bedrock addon by SandBlock Studios / Marketplace Trader) for Minecraft **26.3**.

This is a **from-scratch Java rewrite**. Bedrock uses `@minecraft/server` scripts; Java uses Fabric client APIs. Features are reimplemented to match the original’s purpose.

## Installed features (current build)

| Feature | Status | Notes |
|--------|--------|-------|
| XYZ / block coords | Done | |
| Facing / yaw | Done | |
| Chunk + local in-chunk | Done | |
| Biome | Done | |
| Light (block/sky) | Done | |
| Day / clock | Done | |
| Moon phase | Done | |
| FPS | Done | |
| Speed (b/s) | Done | |
| Armor durability % | Done | H/C/L/F |
| XP level + progress | Done | |
| Held item + durability | Done | Warns when ≤15% |
| Arrow count | Done | Arrow / spectral / tipped |
| Inventory fullness warn | Done | Shows when ≥24/36 |
| Last death coords + distance | Done | Auto on death |
| Custom waypoint | Done | `.wp set` / `.wp clear` |
| Toggle HUD | Done | `.hud` |
| Chunk borders | Vanilla | Use **F3+G** |
| Settings UI screen | Planned | Chat toggles for now |
| Multi-waypoint (15) UI | Planned | Single waypoint for now |
| Crop info | Planned | |
| Dynamic light | Planned | |
| Distance measure tool | Planned | |
| Shape pattern tools | Planned | |
| Block counter tool | Planned | |
| Redstone / honey / mob info | Planned | |
| Starting book / lore UI | Planned | |

## Commands (client chat — not sent to server)

```
.hud              toggle overlay
.hud help         list commands
.wp set           set waypoint at your feet
.wp clear         clear waypoint
.death clear      clear death marker
```

## Install

1. Minecraft **26.3**
2. Fabric Loader **≥ 0.19.5**
3. Fabric API **0.165.0+26.3**
4. Drop the jar from Actions into `mods/`

## Build

Java 25 + Gradle 9.6+

```bash
gradle build
```

## License

MIT (port). Original Bedrock addon © SandBlock Studios / Marketplace Trader.
