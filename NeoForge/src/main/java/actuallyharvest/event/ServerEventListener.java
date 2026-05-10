package actuallyharvest.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ServerEventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = HarvestEventHandler.rightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec());

        if (InteractionResult.SUCCESS.equals(result)) {
            if (event.getEntity() instanceof ServerPlayer sp) {
                ItemStack heldStack = sp.getItemInHand(event.getHand());
                Inventory inventory = sp.getInventory();
                int slot = inventory.findSlotMatchingItem(heldStack);

                if (!heldStack.isEmpty()) {
                    sp.connection.send(sp.getInventory().createInventoryUpdatePacket(slot));
                }
            }

            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

}
