package actuallyharvest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import actuallyharvest.event.ServerEventListener;

@Mod(ActuallyHarvest.MODID)
public class ActuallyHarvestNeoForge {

    public ActuallyHarvestNeoForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        ActuallyHarvest.init();
    }

    private void setup(final FMLCommonSetupEvent evt) {
        MinecraftForge.EVENT_BUS.register(ServerEventListener.class);
    }

}

