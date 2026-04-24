package actuallyharvest;

import net.minecraft.client.Minecraft;

import technology.roughness.whitenoise.config.WhiteNoiseConfig;

import actuallyharvest.config.ConfigHandler;

public class ActuallyHarvestClient {

    public static void init(WhiteNoiseConfig commonConfig) {
        commonConfig.addLoadListener((config, reloading) -> {
            Minecraft mc = Minecraft.getInstance();

            if (reloading && mc.level != null) {
                ConfigHandler.init();
            }
        });
    }

}
