package actuallyharvest.platform;

import java.util.ServiceLoader;

import actuallyharvest.ActuallyHarvest;
import actuallyharvest.platform.services.IPlatform;

public class Services {

    public static final IPlatform PLATFORM = load(IPlatform.class);
    
    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(
                () -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ActuallyHarvest.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }

}
