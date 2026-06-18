# ActuallyHarvest - Project Context

## What This Is
A standalone mod forked from the right-click harvest feature in the [Quark](https://modrinth.com/mod/quark) mod. Quark bundles many features together; this mod extracts that one feature for players who want it without the rest of Quark.

Supports most modded crops and trees (Pam's HarvestCraft 2, Croptopia, Farmer's Delight, The Veggie Way, Fruitful Fun) with configurable behavior including hoe range expansion, XP drops, auto-replant, and mod/crop blacklists.  See README.md for full config options.

## Project Structure
Multi-loader: `Common/` + `NeoForge/` + `Fabric/`

## Branch Convention
| Branch | Modloaders        |
|--------|-------------------|
| 1.20.1 | Forge + Fabric    |
| 1.21.1 | NeoForge + Fabric |
| 26.1   | NeoForge + Fabric |

Maintained: 1.20.1, 1.21.1, 26.1

## Dependencies
- WhiteNoise (jarJar/include)

## Distribution
Side: both (clientRequired = true, serverRequired = true)

## Release Process
Follow the standard wendall911 release process in
`../docs/minecraft/MINECRAFT_DEVELOPMENT_NOTES.md`.
