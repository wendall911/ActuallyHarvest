package actuallyharvest.util;

import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;

import actuallyharvest.config.ConfigHandler;

public class ToolHelper {

    public static boolean isHoe(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof HoeItem || stack.is(ItemTags.HOES));
    }

    public static int getRange(ItemStack hoe) {
        int range = ConfigHandler.Common.getHoeTools().getOrDefault(hoe.getItem(), 1);
        int expandedRange = 0;

        if (ConfigHandler.Common.expandHoeRangeEnchanted()) {
            ItemEnchantments enchantments = hoe.getEnchantments();

            for (Holder<Enchantment> enchantmentHolder : enchantments.keySet()) {
                if (enchantmentHolder.is(Enchantments.EFFICIENCY)) {
                    expandedRange = enchantments.getLevel(enchantmentHolder);
                }
            }
        }

        return Math.min(range + expandedRange, ConfigHandler.Common.maxHoeExpansionRange());
    }

    public static int getBaseRange(int level) {
        if (ConfigHandler.Common.expandHoeRange()) {
            if (level <= 2) {
                return ConfigHandler.Common.smallTierExpansionRange();
            }

            return ConfigHandler.Common.highTierExpansionRange();
        }

        return 1;
    }

    public static int getToolTier(HolderSet<Block> blocks) {
        int tier = 0;
        Map<TagKey<Block>, Integer> tiers = Map.of(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 0,
            BlockTags.INCORRECT_FOR_STONE_TOOL, 1,
            BlockTags.INCORRECT_FOR_IRON_TOOL, 2,
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 3,
            BlockTags.INCORRECT_FOR_GOLD_TOOL, 4,
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 5
        );

        try {
            Optional<TagKey<Block>> key = blocks.unwrapKey();

            if (key.isPresent()) {
                tier = tiers.getOrDefault(key.get(), 0);
            }
        }
        catch(Exception ignore) {}

        return tier;
    }

}
