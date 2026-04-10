BetterQuesting Unofficial 1.5.2 port
============

An unofficial port of BetterQuesting 3 from 1.7.10 to 1.5.2 made for [CakeHiTech Memories](https://github.com/Shimmermare/CakeHiTechMemories) modpack.

**PLEASE DON'T BOTHER ORIGINAL AUTHOR WITH QUESTIONS OR REQUESTS ABOUT THIS PORT!** 
Create issues in this repo, not in the original!

Recommended to be used with [StandardQuestingPack port](https://github.com/Shimmermare/StandardQuestingPack-Backport-1.5.2).  

## Comparison to 1.7.10 original

### Changes
- Config file moved: `BetterQuesting.cfg` -> `BetterQuesting/Main.cfg`
- Themes are now specified in `BetterQuesting/bq_themes.json`. If you want custom textures, 
  add them via texturepack; then you can reference them in a theme file.
- Quest localization files can be specified in `BetterQuesting/lang/<code>.lang` or in a texturepack.

### Unavailable features
- Mod configuration hot reload and editing in GUI.
- Removed support for the legacy theme format.