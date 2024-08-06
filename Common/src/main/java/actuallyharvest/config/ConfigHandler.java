package actuallyharvest.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

import actuallyharvest.util.BlockHelper;

public class ConfigHandler {

    public static final SpectreConfigSpec COMMON_SPEC;

    private static final Common COMMON;

    static {
        final Pair<Common, SpectreConfigSpec> specPairCommon = new SpectreConfigSpec.Builder().configure(Common::new);

        COMMON_SPEC = specPairCommon.getRight();
        COMMON = specPairCommon.getLeft();
    }

    public static void init() {
        Common.crops.clear();
        Common.cropBlocks.clear();
        Common.rightClickBlocks.clear();

        if (Common.autoConfigMods()) {
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!BlockHelper.isVanilla(block)) {
                    if (block instanceof CropBlock cropBlock) {
                        Common.crops.put(cropBlock.getStateForAge(cropBlock.getMaxAge()), cropBlock.defaultBlockState());
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

            if (initial.getBlock() != Blocks.AIR) {
                if (parts.length > 1) {
                    result = BlockHelper.fromString(parts[1]);
                }
                else {
                    result = initial.getBlock().defaultBlockState();
                }

                Common.crops.put(initial, result);
            }
        }

        for (String blockKey : COMMON.harvestableBlocks.get()) {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(blockKey));

            if (block != Blocks.AIR) {
                Common.rightClickBlocks.add(block);
            }
        }

        Common.crops.values().forEach(bs -> Common.cropBlocks.add(bs.getBlock()));
    }

    public static class Common {

        private final SpectreConfigSpec.BooleanValue allowEmptyHand;
        private final SpectreConfigSpec.BooleanValue damageTool;
        private final SpectreConfigSpec.BooleanValue autoConfigMods;
        private final SpectreConfigSpec.IntValue xpFromHarvestChance;
        private final SpectreConfigSpec.IntValue xpFromHarvestAmount;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> harvestableCrops;
        private final SpectreConfigSpec.ConfigValue<List<? extends String>> harvestableBlocks;

        private static final Map<BlockState, BlockState> crops = Maps.newHashMap();
        private static final Set<Block> cropBlocks = Sets.newHashSet();
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

        public static Set<Block> getRightClickBlocks() {
            return rightClickBlocks;
        }

        public static Map<BlockState, BlockState> getCrops() {
            return crops;
        }

        private static Supplier<List<? extends String>> getCropsList() {
            return () -> Arrays.asList(Common.defaultHarvestableCrops);
        }

        private static Supplier<List<? extends String>> getHarvestableBlocksList() {
            return () -> Arrays.asList(Common.defaultHarvestableBlocks);
        }

    }

}
