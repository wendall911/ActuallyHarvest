package actuallyharvest.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import actuallyharvest.ActuallyHarvest;

public class TagManager {

    public static final class Blocks {

        public static final TagKey<Block> HARVEST_BLACKLIST = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ActuallyHarvest.MODID, "harvest_blacklist"));

    }

}
