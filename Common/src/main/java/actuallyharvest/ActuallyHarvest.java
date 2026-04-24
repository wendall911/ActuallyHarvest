package actuallyharvest;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;
import technology.roughness.whitenoise.platform.Services;

import actuallyharvest.config.ConfigHandler;

public class ActuallyHarvest {

    public static final String MODID = "actuallyharvest";
    public static final String MOD_NAME = "Actually Harvest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public static void init() {
        WhiteNoiseConfig commonConfig = WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, ConfigHandler.COMMON_SPEC, MODID);

        commonConfig.addStartupListener((config) -> ConfigHandler.init());

        if (Services.WN_PLATFORM.isPhysicalClient()) {
            ActuallyHarvestClient.init(commonConfig);
        }
    }

}
