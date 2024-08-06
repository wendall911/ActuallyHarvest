package actuallyharvest.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ClickResult result = HarvestEventHandler.rightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec());

        if (result.isPresent()) {
            event.setCanceled(true);
            event.setCancellationResult(result.getInteractionResult());
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
        }
    }

}
