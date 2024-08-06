package actuallyharvest;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import actuallyharvest.event.ServerEventListener;

@Mod(ActuallyHarvest.MODID)
public class ActuallyHarvestNeoForge {

    public ActuallyHarvestNeoForge(IEventBus eventBus) {
        eventBus.addListener(this::setup);
        ActuallyHarvest.init();
    }

    private void setup(final FMLCommonSetupEvent evt) {
        NeoForge.EVENT_BUS.register(ServerEventListener.class);
    }

}

