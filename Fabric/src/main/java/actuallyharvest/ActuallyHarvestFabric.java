package actuallyharvest;

import net.fabricmc.api.ModInitializer;

import actuallyharvest.event.ServerEventListener;

public class ActuallyHarvestFabric implements ModInitializer {

	@Override
    public void onInitialize() {
        ServerEventListener.init();
    }

}

