package actuallyharvest.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ServerEventListener {

    public static void init() {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            InteractionResult result = HarvestEventHandler.rightClickBlock(player, hand, hitResult.getBlockPos(), hitResult);

            if (InteractionResult.SUCCESS.equals(result) && player instanceof ServerPlayer sp) {
                ItemStack heldStack = sp.getItemInHand(hand);
                Inventory inventory = sp.getInventory();
                int slot = inventory.findSlotMatchingItem(heldStack);

                if (!heldStack.isEmpty()) {
                    sp.connection.send(sp.getInventory().createInventoryUpdatePacket(slot));
                }
            }

            return result;
        }));
    }

}
