package betterquesting.backport;

import net.minecraft.item.Item;

public class ItemUtils {
    public static Item getByIdOrNull(int id) {
        return id < 0 || id >= Item.itemsList.length ? null : Item.itemsList[id];
    }
}
