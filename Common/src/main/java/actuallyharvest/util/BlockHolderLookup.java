package actuallyharvest.util;

import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockHolderLookup {

    public static HolderLookup.RegistryLookup<Block> asLookup() {
        ImmutableMap.Builder<TagKey<Block>, HolderSet.Named<Block>> builder = ImmutableMap.builder();
        ImmutableMap<TagKey<Block>, HolderSet.Named<Block>> immutablemap = builder.build();

        return new HolderLookup.RegistryLookup.Delegate<Block>() {

            @Override
            public @NotNull RegistryLookup<Block> parent() {
                return BuiltInRegistries.BLOCK.filterElements(block -> block instanceof Block);
            }

            @Override
            public @NotNull Optional<HolderSet.Named<Block>> get(@NotNull TagKey<Block> tagKey) {
                return Optional.ofNullable(immutablemap.get(tagKey));
            }

            @Override
            public @NotNull Stream<HolderSet.Named<Block>> listTags() {
                return immutablemap.values().stream();
            }
        };
    }

}
