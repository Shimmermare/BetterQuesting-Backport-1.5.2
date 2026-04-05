package betterquesting.backport;

import betterquesting.core.BetterQuesting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.liquids.LiquidStack;

public class LiquidUtils {
    public static final String WATER_NAME = "Water";
    public static final String LAVA_NAME = "Lava";

    private LiquidUtils() {
    }

    public static String getLocalizedName(LiquidStack stack) {
        ItemStack itemStack = stack.asItemStack();
        Item item = itemStack.getItem();
        return item.getLocalizedName(itemStack);
    }

    @SideOnly(Side.CLIENT)
    public static Icon getIcon(LiquidStack stack) {
        if (stack.itemID <= 0 || stack.itemID >= Block.blocksList.length) {
            BetterQuesting.logger.severe("Attempted to get icon for invalid liquid: " + stack.itemID);
            return null;
        }
        Block block = Block.blocksList[stack.itemID];
        return block.getIcon(0, 0);
    }
}
