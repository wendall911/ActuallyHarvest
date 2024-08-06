package actuallyharvest.util;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;

public class ToolHelper {

    public static boolean isHoe(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof HoeItem || stack.is(ItemTags.HOES));
    }

}
