package actuallyharvest;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricConfigInitializer implements SpectreLibInitializer {

    @Override
    public void onInitializeConfig() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ActuallyHarvest.init();
        });
    }

}

