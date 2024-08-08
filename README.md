# ActuallyHarvest [![Project](http://cf.way2muchnoise.eu/full_1076278_downloads.svg)](https://minecraft.curseforge.com/projects/1076278)
![Minecraft](http://cf.way2muchnoise.eu/versions/For%20MC_1076278_all.svg)
![MIT](https://img.shields.io/badge/license-MIT-blue.svg?longCache=true&style=flat)

A mod that actually works with right click harvest for both Forge and Fabric. There are a ton of options out there. All require an external library and they are flaky! Either they don't work at all, or require manual configuration for all the common farming and foods mods (and there aren't very many).

This mod supports most modded crops and trees, is configurable and will actually work when released.

There are possible conflicts with using other right click harvest features in other mods, please report if so and I can update to resolve the conflicts. It is best to just disable the feature in other mods providing duplicate functionality.

## Supported Mods
 - Pam's HarvestCraft 2
 - Croptopia
 - Farmer's Delight
 - The Veggie Way
 - Fruitful Fun

### Example Config
```
[general]
	#Allow harvesting with empty hand. If disabled, requires hoe.
	allowEmptyHand = true
	#Harvesting crops costs durability.
	damageTool = false
	#Attempt to automatically register crops from non-vanilla mods.
	autoConfigMods = true
	#Chance of XP dropping on harvest.
	#Range: 0 ~ 100
	xpFromHarvestChance = 100
	#Amount of XP dropped on harvest.
	#Range: 0 ~ 10
	xpFromHarvestAmount = 1
	#Harvestable crops.
	#Format: "harvestState[,afterHarvest]", i.e. "minecraft:wheat[age=7]" or "minecraft:cocoa[age=2,facing=north],minecraft:cocoa[age=0,facing=north]"
	harvestableCrops = ["minecraft:wheat[age=7]", "minecraft:carrots[age=7]", "minecraft:potatoes[age=7]", "minecraft:beetroots[age=3]", "minecraft:nether_wart[age=3]", "minecraft:cocoa[age=2,facing=north],minecraft:cocoa[age=0,facing=north]", "minecraft:cocoa[age=2,facing=south],minecraft:cocoa[age=0,facing=south]", "minecraft:cocoa[age=2,facing=east],minecraft:cocoa[age=0,facing=east]", "minecraft:cocoa[age=2,facing=west],minecraft:cocoa[age=0,facing=west]"]
	#Blocks that right clicking should simulate click instead of breaking.
	#For blocks like berry bushes that have built-in right click harvest.
	harvestableBlocks = ["minecraft:sweet_berry_bush", "minecraft:cave_vines"]
	#Expand hoe range based on tier.
	expandHoeRange = true
	#Regular hoe (gold, wood, iron) expansion range.
	#Range: 1 ~ 5
	smallTierExpansionRange = 2
	#Regular hoe (gold, wood, iron) expansion range.
	#Range: 1 ~ 5
	highTierExpansionRange = 3
	#Expand hoe range by 1 for each level of efficiency enchantment level.
	expandHoeRangeEnchanted = true
	#Maximum range hoe can expand for harvesting. This is the maximum of tier + efficiency enchantment.
	#Range: 1 ~ 11
	maxHoeExpansionRange = 11
	#List of individual hoe tools and their harvest tier. This is for modded items not covered. Format: minecraft:wooden_hoe-0 (with number being tier)
	hoeItems = []
```

All Downloads:

[![Files](https://curse.nikky.moe/api/img/1076278/files?logo)](https://minecraft.curseforge.com/projects/1076278/files)

## Links of Interest

+ [ActuallyHarvest Curseforge Page](https://www.curseforge.com/minecraft/mc-mods/actually-harvest)
+ [ActuallyHarvest Modrinth Page](https://modrinth.com/project/actually-harvest)
