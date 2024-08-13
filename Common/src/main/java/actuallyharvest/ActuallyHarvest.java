package actuallyharvest;

import java.util.Random;

import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actuallyharvest.config.ConfigHandler;

public class ActuallyHarvest {

    public static final String MODID = "actuallyharvest";
    public static final String MOD_NAME = "Actually Harvest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public static void init() {
        SpectreConfigLoader.add(SpectreConfig.Type.COMMON, ConfigHandler.COMMON_SPEC, MODID);
    }

}
