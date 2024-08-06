package actuallyharvest.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class ServerEventListener {

    public static void init() {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> HarvestEventHandler.rightClickBlock(player, hand, hitResult.getBlockPos(), hitResult).getInteractionResult()));
    }

}
