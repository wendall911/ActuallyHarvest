package actuallyharvest.platform;

import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.util.FakePlayer;

import actuallyharvest.platform.services.IPlatform;

public class NeoForgePlatform implements IPlatform {

    @Override
    public boolean isModLoaded(String name) {
        return ModList.get().isLoaded(name);
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer;
    }

}
