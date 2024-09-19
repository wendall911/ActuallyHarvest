package actuallyharvest.util;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import actuallyharvest.common.TagManager;
import actuallyharvest.config.ConfigHandler;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockHelper {

    public static boolean isVanilla(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("minecraft");
    }

    public static boolean isBottomBlock(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath().contains("_bottom");
    }

    public static String[] parseBlockString(String blockString) {
        boolean inBracket = false;

        for (int i = 0; i < blockString.length(); i++) {
            char c = blockString.charAt(i);

            if (c == '[') {
                inBracket = true;
            }
            else if (c == ']') {
                inBracket = false;
            }
            else if (c == ',' && !inBracket) {
                return new String[] {
                    blockString.substring(0, i),
                    blockString.substring(i + 1)
                };
            }
        }

        return new String[] { blockString };
    }

    public static BlockState fromString(String key) {
        try {
            BlockStateParser.BlockResult result =
                BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(key), false);

            return result.blockState();
        } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public static BlockState getToolModifiedState(BlockState state, UseOnContext context, String toolActionType, boolean simulate) {
        return switch (toolActionType) {
            case "axe_strip" -> getAxeStrippingState(state);
            case "axe_scrape" -> WeatheringCopper.getPrevious(state).orElse(null);
            case "axe_wax_off" -> Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state)).orElse(null);
            case "shovel_flatten" -> getShovelPathingState(state);
            case "hoe_till" -> {
                Block block = state.getBlock();
                if (block == Blocks.ROOTED_DIRT) {
                    if (!simulate && !context.getLevel().isClientSide) {
                        Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
                    }
                    yield Blocks.DIRT.defaultBlockState();
                } else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir())
                    yield block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
                else
                    yield null;
            }
            default -> null;
        };
    }

    public static InteractionType getInteractionTypeForBlock(BlockState state, boolean canRightClick) {
        state = getModifiedState(state).getLeft();

        if (state.is(TagManager.Blocks.HARVEST_BLACKLIST)) {
            return InteractionType.NONE;
        }
        else if (canRightClick && ConfigHandler.Common.getRightClickBlocks().contains(state.getBlock())) {
            return InteractionType.CLICK;
        }
        else if (ConfigHandler.Common.getCrops().containsKey(state)) {
            return InteractionType.HARVEST;
        }

        return InteractionType.NONE;
    }

    public static Pair<BlockState, Boolean> getModifiedState(BlockState state) {
        IntegerProperty distance = IntegerProperty.create("distance", 1, 7);
        AtomicBoolean useDefault = new AtomicBoolean(false);

        state.getProperties().forEach(property -> {
            if (property.equals(distance)) {
                useDefault.set(true);
            }
        });

        if (useDefault.get()) {
            Block block = BuiltInRegistries.BLOCK.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
            if (block instanceof CropBlock cropBlock) {
                Integer age = state.getValue(cropBlock.getAgeProperty());
                state = cropBlock.defaultBlockState().setValue(cropBlock.getAgeProperty(), age);
            }
        }

        return Pair.of(state, useDefault.get());
    }

    private static BlockState getAxeStrippingState(BlockState state) {
        Block block = AxeItem.STRIPPABLES.get(state.getBlock());

        return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
    }

    private static BlockState getShovelPathingState(BlockState state) {
        return ShovelItem.FLATTENABLES.get(state.getBlock());
    }

    public enum InteractionType {
        NONE, CLICK, HARVEST;
    }

}
