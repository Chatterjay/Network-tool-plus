# Network Tool Plus

A Minecraft NeoForge mod that enhances AE2's **Network Tool** with auto-collection, expanded inventory, and toolbox improvements.

## Features

- **Collector Mode** — Sneak + right-click to toggle. When active, the tool gains an enchantment glint and automatically pulls upgrade cards from your inventory into the tool
- **Auto-Collect** — Works whether the tool is in your inventory or a Curios slot. Scans your inventory every 10 ticks for AE2 upgrade cards and moves them into the tool
- **21 Upgrade Slots** — Expanded from the vanilla 9 slots to 21 (7×3 grid), with custom GUI textures
- **Toolbox Smart-Fill** — Shift-clicking upgrade cards from the toolbox in a machine GUI directly fills upgrade slots. Incompatible cards stay put. Terminal GUIs retain original behavior
- **Tool Slot Locking** — When any AE2 menu is open, Network Tools in your inventory are locked to prevent accidental shift-click movement
- **Curios Integration** — (Optional) Wear the Network Tool in a Curios slot. Full collector mode and auto-collect support

## Dependencies

| Dependency | Version | Required |
|---|---|---|
| Minecraft | 1.21.1 | Yes |
| NeoForge | 21.1.219+ | Yes |
| AE2 | 19.0+ | Yes |
| Curios | 9.1.0+ | No |

## Usage

1. Hold a Network Tool and sneak + right-click to toggle Collector Mode (tool gains enchantment glow)
2. Any upgrade cards in your inventory will be automatically collected into the tool
3. Open any AE2 machine GUI — the tool's upgrade slots appear in the toolbox panel (7×3 instead of 3×3)

## License

GNU LGPL 3.0
