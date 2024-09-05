/**
 * This class was derived from Quark Mod and written by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github: https://github.com/Vazkii/Quark
 *
 * https://github.com/VazkiiMods/Quark/blob/master/src/main/java/org/violetmoon/quark/content/tweaks/module/SimpleHarvestModule.java
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */
package actuallyharvest.event;

import org.apache.commons.lang3.mutable.MutableBoolean;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import actuallyharvest.ActuallyHarvest;
import actuallyharvest.config.ConfigHandler;
import actuallyharvest.util.BlockHelper;
import actuallyharvest.util.ToolHelper;

public class HarvestEventHandler {

    private static boolean isHarvesting = false;

    public static ClickResult rightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult) {
        if (player.level().isClientSide() || isHarvesting) return ClickResult.pass();

        isHarvesting = true;

        ClickResult result = getClickResult(player, hand, pos, hitResult);

        isHarvesting = false;

        return result;
    }

    private static ClickResult getClickResult(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult) {
        if (player == null || hand == null || player.isSpectator()) return ClickResult.pass();
        if (hitResult.getType() != HitResult.Type.BLOCK || !hitResult.getBlockPos().equals(pos)) return ClickResult.pass();

        Level level = player.level();
        BlockState blockState = level.getBlockState(pos);
        BlockState modifiedState = BlockHelper.getToolModifiedState(blockState, new UseOnContext(player, hand, hitResult), "hoe_till", true);

        if (modifiedState != null) return ClickResult.pass();

        ItemStack heldStack = player.getItemInHand(hand);
        boolean isHoe = ToolHelper.isHoe(heldStack);

        if (!ConfigHandler.Common.allowEmptyHand() && !isHoe) {
            return ClickResult.pass();
        }

        BlockState above = level.getBlockState(pos.above());
        int range = 1;

        if (isHoe) {
            if (BlockHelper.getInteractionTypeForBlock(blockState, true) == BlockHelper.InteractionType.NONE
                    && BlockHelper.getInteractionTypeForBlock(above, true ) == BlockHelper.InteractionType.NONE) {
                return ClickResult.pass();
            }
            range = ToolHelper.getRange(heldStack);
        }

        boolean harvested = false;

        for (int x = 1 - range; x < range; x++) {
            for (int z = 1 - range; z < range; z++) {
                BlockPos shiftPos = pos.offset(x, 0, z);

                if (!tryHarvest(level, shiftPos, player, hand, range > 1)) {
                    shiftPos = shiftPos.above();

                    if (tryHarvest(level, shiftPos, player, hand, range > 1)) {
                        harvested = true;
                    }
                }
                else {
                    harvested = true;
                }
            }
        }

        if (!harvested) return ClickResult.pass();

        return ClickResult.interrupt();
    }

    private static boolean tryHarvest(Level level, BlockPos pos, @Nullable LivingEntity entity, @Nullable InteractionHand hand, boolean canReach) {
        if (entity instanceof Player player && (!level.mayInteract(player, pos))) {
            return false;
        }

        BlockState blockState = level.getBlockState(pos);
        BlockHelper.InteractionType interactionType = BlockHelper.getInteractionTypeForBlock(blockState, canReach);

        if (interactionType != BlockHelper.InteractionType.NONE) {
            if (interactionType == BlockHelper.InteractionType.HARVEST) {
                if (entity instanceof Player) {
                    return harvestAndReplant(level, pos, blockState, entity, hand);
                }
            }
            else if (interactionType == BlockHelper.InteractionType.CLICK && entity instanceof Player) {
                BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, true);

                if (hand == null) hand = InteractionHand.MAIN_HAND;

                if (entity instanceof ServerPlayer sp) {
                    return sp.gameMode.useItemOn(sp, sp.level(), sp.getItemInHand(hand), hand, hitResult).consumesAction();
                }
            }
        }

        return false;
    }

    private static boolean harvestAndReplant(Level level, BlockPos pos, BlockState blockState, LivingEntity entity, InteractionHand hand) {
        BlockState cropBlockState = ConfigHandler.Common.getCrops().get(blockState);
        BlockState above = level.getBlockState(pos.above());

        if (above.getBlock() instanceof CropBlock) {
            cropBlockState = ConfigHandler.Common.getCrops().get(above);
        }

        if (cropBlockState == null) return false;

        if (level instanceof ServerLevel serverLevel) {
            ItemStack copy;
            ItemStack heldStack = null;

            if (entity == null || hand == null) {
                copy = new ItemStack(Items.STICK);
            }
            else {
                heldStack = entity.getItemInHand(hand);
                copy = entity.getItemInHand(hand).copy();
            }

            MutableBoolean hasTaken = new MutableBoolean(false);
            Item blockItem = blockState.getBlock().asItem();
            Block.getDrops(blockState, serverLevel, pos, level.getBlockEntity(pos), entity, copy).forEach((stack) -> {
                if (stack.getItem() == blockItem && !hasTaken.getValue()) {
                    stack.shrink(1);
                    hasTaken.setValue(true);
                }

                if (!stack.isEmpty()) {
                    Block.popResource(level, pos, stack);
                }
            });
            boolean dropXp = entity instanceof Player;
            blockState.spawnAfterBreak(serverLevel, pos, copy, dropXp);

            if (dropXp && ActuallyHarvest.RANDOM.nextInt(100) + 1 <= ConfigHandler.Common.xpFromHarvestChance()) {
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), ConfigHandler.Common.xpFromHarvestAmount());
            }

            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(cropBlockState));
            level.setBlockAndUpdate(pos, cropBlockState);
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(entity, blockState));

            if (heldStack != null && !level.isClientSide && ConfigHandler.Common.damageTool()) {
                heldStack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            }
        }

        return true;
    }

}
