package actuallyharvest.common;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class Translations {

    private static final Joiner LINE_JOINER = Joiner.on("\n");
    private static final Map<String, String> translations = Maps.newHashMap();

    static {
        translations.put("general", "General Settings");
        translations.put("allowemptyhand", "Allow harvesting with empty hand. If disabled, requires hoe.");
        translations.put("allowfakeplayer", "Allow machines, like Create's deployer, to harvest crops.");
        translations.put("autoconfigmods", "Automatically register crops.");
        translations.put("blacklistcrops", "List of crops to blacklist from right-click harvest. Format: \"modid:block\"");
        translations.put("blacklisthelditems", "List of held items to blacklist from right-click harvest. Format: \"modid:item\"");
        translations.put("blacklistmods", "List of mods to blacklist from right-click harvest. Format: \"modid\"");
        translations.put("damagetool", "Harvesting crops costs durability.");
        translations.put("expandhoerange", "Expand hoe range based on tier.");
        translations.put("expandhoerangeenchanted", "Expand hoe range by 1 for each level of efficiency enchantment level.");
        translations.put("harvestableblocks", joiner(
                "Blocks that right clicking should simulate click instead of breaking.",
                "For blocks like berry bushes that have built-in right click harvest."
        ));
        translations.put("harvestablecrops", joiner(
                "Harvestable crops.",
                "Format: \"harvestState[,afterHarvest]\", i.e. \"minecraft:wheat[age=7]\"",
                "or \"minecraft:cocoa[age=2,facing=north],minecraft:cocoa[age=0,facing=north]\"",
                "WARNING: If autoConfigMods is set to false, only crops defined here will work.",
                "If not, it will just add to the auto-configured list."
        ));
        translations.put("hightierexpansionrange", "Regular hoe (gold, wood, iron) expansion range.");
        translations.put("hoeitems", joiner(
                "List of individual hoe tools and their harvest tier. This is for modded items not covered.",
                "Format: minecraft:wooden_hoe-0 (with number being tier)"
        ));
        translations.put("maxhoeexpansionrange", joiner(
                "Maximum range hoe can expand for harvesting.",
                "This is the maximum of tier + efficiency enchantment."
        ));
        translations.put("smalltierexpansionrange", "Regular hoe (gold, wood, iron) expansion range.");
        translations.put("xpfromharvestamount", "Amount of XP dropped on harvest.");
        translations.put("xpfromharvestchance", "Chance of XP dropping on harvest.");
        translations.put("xpfromharvestrangeamount", "Range of XP dropped on harvest. Format: \"min-max\", example: \"0-3\"");
        translations.put("xpfromharvestuserange", "Use range for XP drop, instead of set amount.");
    }

    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }

    private static String joiner(String... string) {
        return LINE_JOINER.join(string);
    }

}
