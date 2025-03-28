package actuallyharvest.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.DoublePlantBlock;

import org.apache.commons.lang3.tuple.Pair;

import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import actuallyharvest.util.BlockHelper;
import actuallyharvest.util.ToolHelper;

public class ConfigHandler {

    public static final SpectreConfigSpec COMMON_SPEC;

    private static final Common COMMON;

    static {
        final Pair<Common, SpectreConfigSpec> specPairCommon = new SpectreConfigSpec.Builder().configure(Common::new);

        COMMON_SPEC = specPairCommon.getRight();
        COMMON = specPairCommon.getLeft();
    }

    public static void init() {
        BooleanProperty upper = BooleanProperty.create("upper");
        BooleanProperty top = BooleanProperty.create("top");

        Common.crops.clear();
        Common.rightClickBlocks.clear();
        Common.hoeTools.clear();

        if (Common.autoConfigMods()) {
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!BlockHelper.isVanilla(block) && !Common.isBlacklistMod(block) && !Common.isBlacklistCrop(block)) {
                    if (block instanceof CropBlock cropBlock) {
                        BlockState cropBlockstate = cropBlock.defaultBlockState();
                        BlockState maxAgeCropBlockstate = cropBlock.getStateForAge(cropBlock.getMaxAge());

                        if (cropBlockstate.hasProperty(upper)) {
                            cropBlockstate = cropBlockstate.setValue(upper, true);
                            maxAgeCropBlockstate = maxAgeCropBlockstate.setValue(upper, true);
                        }
                        else if (cropBlockstate.hasProperty(top)) {
                            cropBlockstate = cropBlockstate.setValue(top, true);
                            maxAgeCropBlockstate = maxAgeCropBlockstate.setValue(top, true);
                        }
                        else if (cropBlockstate.hasProperty(DoublePlantBlock.HALF)) {
                            cropBlockstate = cropBlockstate.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                            maxAgeCropBlockstate = maxAgeCropBlockstate.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                        }

                        if (BlockHelper.isBottomBlock(block)) continue;

                        Common.crops.put(maxAgeCropBlockstate, cropBlockstate);
                    } else if ((block instanceof BushBlock || block instanceof GrowingPlantBlock)
                            && block instanceof BonemealableBlock) {
                        Common.rightClickBlocks.add(block);
                    }
                }
            }
        }

        for (String cropKey : COMMON.harvestableCrops.get()) {
            BlockState initial;
            BlockState result;
            String[] parts = BlockHelper.parseBlockString(cropKey);

            initial = BlockHelper.fromString(cropKey);
            Block block = initial.getBlock();

            if (block != Blocks.AIR && !Common.isBlacklistCrop(block) && !Common.isBlacklistMod(block)) {
                if (parts.length > 1) {
                    result = BlockHelper.fromString(parts[1]);
                }
                else {
                    result = block.defaultBlockState();
                }

                Common.crops.put(initial, result);
            }
        }

        for (String blockKey : COMMON.harvestableBlocks.get()) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockKey));

            if (block != Blocks.AIR && !Common.isBlacklistCrop(block) && !Common.isBlacklistMod(block)) {
                Common.rightClickBlocks.add(block);
            }
        }

        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof DiggerItem digger) {
                Tool tool = digger.components().get(DataComponents.TOOL);
                TagKey<Block> tagKey = null;

                if (tool != null) {
                    for (Tool.Rule rule : tool.rules()) {
                        if (rule.correctForDrops().isPresent()) {
                            Optional<TagKey<Block>> optionalBlockTagKey = rule.blocks().unwrapKey();

                            if (optionalBlockTagKey.isPresent()) {
                                tagKey = optionalBlockTagKey.get();
                            }
                         }
                    }
                }

                if (tagKey == BlockTags.MINEABLE_WITH_HOE) {
                    int tier = 0;

                    try {
                        tier = Tiers.valueOf(digger.getTier().toString()).ordinal();
                    }
                    catch(Exception ignore) {}
                    Common.hoeTools.put(digger, ToolHelper.getBaseRange(tier));
                }
            }
        });

        // Add config overrides
        for (String hoeItem : COMMON.hoeItems.get()) {
            String[] parts = hoeItem.split("-");
            int range = ToolHelper.getBaseRange(Integer.parseInt(parts[1]));
            ResourceLocation loc = ResourceLocation.parse(parts[0]);
            Item item = BuiltInRegistries.ITEM.get(loc);

            Common.hoeTools.put(item, range);
        }
    }

    public static class Common {

        private final SpectreConfigSpec.BooleanValue allowEmptyHand;
        private final SpectreConfigSpec.BooleanValue damageTool;
        private final SpectreConfigSpec.BooleanValue autoConfigMods;
        private final SpectreConfigSpec.IntValue xpFromHarvestChance;
        private final SpectreConfigSpec.IntValue xpFromHarvestAmount;
        private final SpectreConfigSpec.BooleanValue xpFromHarvestUseRange;
        private final SpectreConfigSpec.ConfigValue<String> xpFromHarvestRangeAmount;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> harvestableCrops;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> harvestableBlocks;
        private final SpectreConfigSpec.BooleanValue expandHoeRange;
        private final SpectreConfigSpec.IntValue smallTierExpansionRange;
        private final SpectreConfigSpec.IntValue highTierExpansionRange;
        private final SpectreConfigSpec.BooleanValue expandHoeRangeEnchanted;
        private final SpectreConfigSpec.IntValue maxHoeExpansionRange;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> hoeItems;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> blacklistCrops;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> blacklistMods;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> blacklistHeldItems;
        private final SpectreConfigSpec.BooleanValue allowFakePlayer;

        private static final Map<BlockState, BlockState> crops = Maps.newHashMap();
        private static final Set<Block> rightClickBlocks = Sets.newHashSet();
        private static final List<String> harvestableCropsList = List.of("harvestableCrops");
        private static final String[] defaultHarvestableCrops = new String[] {
            "minecraft:wheat[age=7]",
            "minecraft:carrots[age=7]",
            "minecraft:potatoes[age=7]",
            "minecraft:beetroots[age=3]",
            "minecraft:nether_wart[age=3]",
            "minecraft:cocoa[age=2,facing=north],minecraft:cocoa[age=0,facing=north]",
            "minecraft:cocoa[age=2,facing=south],minecraft:cocoa[age=0,facing=south]",
            "minecraft:cocoa[age=2,facing=east],minecraft:cocoa[age=0,facing=east]",
            "minecraft:cocoa[age=2,facing=west],minecraft:cocoa[age=0,facing=west]"
        };
        private static final List<String> harvestableBlocksList = List.of("harvestableBlocks");
        private static final String[] defaultHarvestableBlocks = new String[] {
            "minecraft:sweet_berry_bush",
            "minecraft:cave_vines"
        };
        private static final Predicate<Object> resourceLocationValidator = s -> s instanceof String
            && ((String) s).matches("[a-z]+[:]{1}[a-z_]+");
        // See: https://github.com/MinecraftForge/MinecraftForge/blob/1.18.x/fmlloader/src/main/java/net/minecraftforge/fml/loading/moddiscovery/ModInfo.java
        private static final Predicate<Object> modidValidator = s -> s instanceof String
            && ((String) s).matches("^[a-z][a-z0-9_]{1,63}$");
        private static final Map<Item, Integer> hoeTools = Maps.newHashMap();
        private static final Predicate<Object> hoeItemValidator = s -> s instanceof String
            && ((String) s).matches("[a-z][a-z0-9_]{1,63}+[:]{1}[a-z_]+[-]{1}[0-9]+");
        private static final Predicate<Object> xpRangeValidator = s -> s instanceof String
            && ((String) s).matches("[0-9]+[-]{1}[0-9]+");
        private static final List<String> hoeItemList = List.of("hoeItems");
        private static final String[] defaultHoeItemList = new String[] {};
        private static final List<String> blacklistCropsList = List.of("blacklistCrops");
        private static final String[] defaultBlacklistCrops = new String[] {};
        private static final List<String> blacklistModsList = List.of("blacklistMods");
        private static final String[] defaultBlacklistMods = new String[] {};
        private static final List<String> blacklistHeldItemsList = List.of("blacklistHeldItems");
        private static final String[] defaultBlacklistHeldItems = new String[] {};

        public Common(SpectreConfigSpec.Builder builder) {
            builder.push("general");
            allowEmptyHand = builder
                .comment("Allow harvesting with empty hand. If disabled, requires hoe.")
                .define("allowEmptyHand", true);
            damageTool = builder
                .comment("Harvesting crops costs durability.")
                .define("damageTool", false);
            autoConfigMods = builder
                .comment("Attempt to automatically register crops from non-vanilla mods.")
                .define("autoConfigMods", true);
            xpFromHarvestChance = builder
                .comment("Chance of XP dropping on harvest.")
                .defineInRange("xpFromHarvestChance", 100, 0, 100);
            xpFromHarvestAmount = builder
                .comment("Amount of XP dropped on harvest.")
                .defineInRange("xpFromHarvestAmount", 1, 0, 10);
            xpFromHarvestUseRange = builder
                .comment("Use range for XP drop, instead of set amount.")
                .define("xpFromHarvestUseRange", false);
            xpFromHarvestRangeAmount = builder
                .comment("Range of XP dropped on harvest. Format: \"min-max\", example: \"0-3\"")
                .define("xpFromHarvestRangeAmount", "0-3", xpRangeValidator);
            harvestableCrops = builder
                .comment(
                    "Harvestable crops.\n"
                    + "Format: \"harvestState[,afterHarvest]\", i.e. \"minecraft:wheat[age=7]\" or \"minecraft:cocoa[age=2,facing=north],minecraft:cocoa[age=0,facing=north]\""
                )
                .defineListAllowEmpty(harvestableCropsList, getCropsList(), s -> (s instanceof String));
            harvestableBlocks = builder
                .comment(
                    "Blocks that right clicking should simulate click instead of breaking.\n"
                    + "For blocks like berry bushes that have built-in right click harvest."
                )
                .defineListAllowEmpty(harvestableBlocksList, getHarvestableBlocksList(), resourceLocationValidator);
            expandHoeRange = builder
                .comment("Expand hoe range based on tier.")
                .define("expandHoeRange", true);
            smallTierExpansionRange = builder
                .comment("Regular hoe (gold, wood, iron) expansion range.")
                .defineInRange("smallTierExpansionRange", 2, 1, 5);
            highTierExpansionRange = builder
                .comment("Regular hoe (gold, wood, iron) expansion range.")
                .defineInRange("highTierExpansionRange", 3, 1, 5);
            expandHoeRangeEnchanted = builder
                .comment("Expand hoe range by 1 for each level of efficiency enchantment level.")
                .define("expandHoeRangeEnchanted", true);
            maxHoeExpansionRange = builder
                .comment("Maximum range hoe can expand for harvesting. This is the maximum of tier + efficiency enchantment.")
                .defineInRange("maxHoeExpansionRange", 11, 1, 11);
            hoeItems = builder
                .comment("List of individual hoe tools and their harvest tier. This is for modded items not covered. Format: minecraft:wooden_hoe-0 (with number being tier)")
                .defineListAllowEmpty(hoeItemList, getHoeItems(), hoeItemValidator);
            blacklistCrops = builder
                .comment("List of crops to blacklist from right-click harvest. Format: \"modid:block\"")
                .defineListAllowEmpty(blacklistCropsList, getBlacklistCrops(), resourceLocationValidator);
            blacklistMods = builder
                .comment("List of mods to blacklist from right-click harvest. Format: \"modid\"")
                .defineListAllowEmpty(blacklistModsList, getBlacklistMods(), modidValidator);
            allowFakePlayer = builder
                .comment("Allow machines, like Create's deployer, to harvest crops.")
                .define("allowFakePlayer", true);
            blacklistHeldItems = builder
                .comment("List of held items to blacklist from right-click harvest. Format: \"modid:item\"")
                .defineListAllowEmpty(blacklistHeldItemsList, getBlacklistHeldItems(), resourceLocationValidator);
        }

        public static boolean allowEmptyHand() {
            return COMMON.allowEmptyHand.get();
        }

        public static boolean damageTool() {
            return COMMON.damageTool.get();
        }

        public static boolean autoConfigMods() {
            return COMMON.autoConfigMods.get();
        }

        public static int xpFromHarvestChance() {
            return COMMON.xpFromHarvestChance.get();
        }

        public static int xpFromHarvestAmount() {
            return COMMON.xpFromHarvestAmount.get();
        }

        public static boolean xpFromHarvestUseRange() {
            return COMMON.xpFromHarvestUseRange.get();
        }

        public static Pair<Integer, Integer> xpFromHarvestRangeAmount() {
            String[] amounts = COMMON.xpFromHarvestRangeAmount.get().split("-");
            int left = Integer.parseInt(amounts[0]);
            int right = Integer.parseInt(amounts[1]);

            // If the left value is greater than the right value, return default values
            if (left > right) {
                return Pair.of(0, 3);
            }

            return Pair.of(left, right);
        }

        public static Set<Block> getRightClickBlocks() {
            return rightClickBlocks;
        }

        public static Map<BlockState, BlockState> getCrops() {
            return crops;
        }

        public static boolean expandHoeRange() {
            return COMMON.expandHoeRange.get();
        }

        public static int smallTierExpansionRange() {
            return COMMON.smallTierExpansionRange.get();
        }

        public static int highTierExpansionRange() {
            return COMMON.highTierExpansionRange.get();
        }

        public static boolean expandHoeRangeEnchanted() {
            return COMMON.expandHoeRangeEnchanted.get();
        }

        public static int maxHoeExpansionRange() {
            return COMMON.maxHoeExpansionRange.get();
        }

        public static Map<Item, Integer> getHoeTools() {
            return hoeTools;
        }

        public static boolean allowFakePlayer() {
            return COMMON.allowFakePlayer.get();
        }

        private static Supplier<List<? extends String>> getCropsList() {
            return () -> Arrays.asList(Common.defaultHarvestableCrops);
        }

        private static Supplier<List<? extends String>> getHarvestableBlocksList() {
            return () -> Arrays.asList(Common.defaultHarvestableBlocks);
        }

        private static Supplier<List<? extends String>> getHoeItems() {
            return () -> Arrays.asList(Common.defaultHoeItemList);
        }

        private static Supplier<List<? extends String>> getBlacklistCrops() {
            return () -> Arrays.asList(Common.defaultBlacklistCrops);
        }

        private static Supplier<List<? extends String>> getBlacklistMods() {
            return () -> Arrays.asList(Common.defaultBlacklistMods);
        }

        private static Supplier<List<? extends String>> getBlacklistHeldItems() {
            return () -> Arrays.asList(Common.defaultBlacklistHeldItems);
        }

        private static boolean isBlacklistCrop(Block block) {
            return COMMON.blacklistCrops.get().contains(BlockHelper.getBlockId(block).toString());
        }

        private static boolean isBlacklistMod(Block block) {
            return COMMON.blacklistMods.get().contains(BlockHelper.getBlockId(block).getNamespace());
        }

        public static boolean isBlacklistHeldItem(ItemStack stack) {
            return COMMON.blacklistHeldItems.get().contains(ToolHelper.getItemStackId(stack).toString());
        }

    }

}
