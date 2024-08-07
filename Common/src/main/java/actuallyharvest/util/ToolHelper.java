package actuallyharvest.util;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import actuallyharvest.config.ConfigHandler;

public class ToolHelper {

    public static boolean isHoe(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof HoeItem || stack.is(ItemTags.HOES));
    }

    public static int getRange(ItemStack hoe) {
        int range = ConfigHandler.Common.getHoeTools().getOrDefault(hoe.getItem(), 1);
        int expandedRange = 0;

        if (ConfigHandler.Common.expandHoeRangeEnchanted()) {
            expandedRange = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EFFICIENCY, hoe);
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

}
