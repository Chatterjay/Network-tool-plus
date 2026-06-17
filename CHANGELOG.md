# Changelog

## 1.0.0-SNAPSHOT

### Added
- **Collector Mode**: Sneak+right-click toggles enchantment glint collector mode on AE2's Network Tool
- **Auto-Collect**: Continuously collects AE2 upgrade cards from inventory into the tool's upgrade slots
- **21 Slots**: Expanded from original 9 to 21 upgrade card slots (7 columns × 3 rows)
- **Custom GUI Textures**: Override AE2 textures for Network Tool GUI and toolbox panel
- **Multi-UI Support**: Real-time slot refresh across crafting terminals, storage buses, and all AE2 upgrade panels
- **Persistence**: Collector mode and collected cards persist through logout, death, and item drops

### Changed
- NetworkToolScreen: 7-column slot layout with custom background texture
- Toolbox panel: Extended height with custom texture (160×160)
- Slot count: 9 → 21 in NetworkToolMenuHost, ToolboxMenu, and NetworkToolMenu

### Fixed
- Auto-collect now works with ToolboxMenu via reflection (terminal side panel)
- Auto-collect refreshes across all AE2 UI types (MEStorageMenu and UpgradeableMenu)
- Enchantment glint correctly shows collector mode via ItemStack mixin
