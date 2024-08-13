package actuallyharvest.event;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import actuallyharvest.config.ConfigHandler;

public class ServerEventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ClickResult result = HarvestEventHandler.rightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec());

        if (result.isPresent()) {
            event.setCanceled(true);
            event.setCancellationResult(result.getInteractionResult());
            event.setUseBlock(TriState.FALSE);
            event.setUseItem(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void initConfig(final ServerStartingEvent event) {
        ConfigHandler.init();
    }

}
