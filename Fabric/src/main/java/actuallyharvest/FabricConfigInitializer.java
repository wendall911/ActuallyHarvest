package actuallyharvest;

import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import actuallyharvest.config.ConfigHandler;

public class FabricConfigInitializer implements SpectreConfigInitializer {

    @Override
    public void onInitializeConfig() {
        ActuallyHarvest.init();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigHandler.init();
        });
    }

}
