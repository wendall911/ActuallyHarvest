package actuallyharvest.platform.services;

import net.minecraft.world.entity.player.Player;

public interface IPlatform {

    boolean isModLoaded(String name);

    boolean isPhysicalClient();

    boolean isFakePlayer(Player player);

}
