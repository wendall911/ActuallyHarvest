package actuallyharvest.util;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import technology.roughness.whitenoise.platform.Services;

import actuallyharvest.common.TagManager;
import actuallyharvest.config.ConfigHandler;

public class BlockHelper {

    public static boolean isVanilla(Block block) {
        return getBlockId(block).getNamespace().equals("minecraft");
    }

    public static boolean isBottomBlock(Block block) {
        return getBlockId(block).getPath().contains("_bottom");
    }

    public static Identifier getBlockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
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
                BlockStateParser.parseForBlock(BlockHolderLookup.asLookup(), new StringReader(key), false);

            return result.blockState();
        } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public static BlockState getToolModifiedState(BlockState state, UseOnContext context) {
        Block block = state.getBlock();
        if (block == Blocks.ROOTED_DIRT) {
            if (!context.getLevel().isClientSide()) {
                Block.popResourceFromFace(
                    context.getLevel(),
                    context.getClickedPos(),
                    context.getClickedFace(),
                    new ItemStack(Items.HANGING_ROOTS)
                );
            }
            return Blocks.DIRT.defaultBlockState();
        }
        else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT ||
                block == Blocks.COARSE_DIRT) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir())
            return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
        else
            return null;
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
            Block block = null;
            Optional<Holder.Reference<Block>> optionalBlock = BuiltInRegistries.BLOCK.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

            if (optionalBlock.isPresent()) {
                block = optionalBlock.get().value();
            }

            if (block instanceof CropBlock cropBlock) {
                Integer age = state.getValue(cropBlock.getAgeProperty());
                state = cropBlock.defaultBlockState().setValue(cropBlock.getAgeProperty(), age);
            }
        }

        return Pair.of(state, useDefault.get());
    }

    public static boolean playerCanHarvest(Player player) {
        if (ConfigHandler.Common.allowFakePlayer()) return true;

        return !Services.WN_PLATFORM.isFakePlayer(player);
    }

    public enum InteractionType {
        NONE, CLICK, HARVEST;
    }

    public static boolean isBlockItem(ItemStack stack) {
        return Block.byItem(stack.getItem()) instanceof Block;
    }

}
