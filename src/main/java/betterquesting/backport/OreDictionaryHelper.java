package betterquesting.backport;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

/**
 * Because
 */
public class OreDictionaryHelper {

    private static final Map<ItemKey, Set<Integer>> itemToOreIDs = new HashMap<ItemKey, Set<Integer>>();

    private static ItemKey getKey(ItemStack stack) {
        return new ItemKey(stack.itemID, stack.getItemDamage());
    }

    public static void init() {
        for (String oreName : OreDictionary.getOreNames()) {
            int id = OreDictionary.getOreID(oreName);
            for (ItemStack stack : OreDictionary.getOres(id)) {
                ItemKey key = getKey(stack);
                Set<Integer> ids = itemToOreIDs.get(key);
                if (ids == null) {
                    ids = new HashSet<Integer>();
                    itemToOreIDs.put(key, ids);
                }
                ids.add(id);
            }
        }
        MinecraftForge.EVENT_BUS.register(new OreDictionaryHelper());
    }

    @ForgeSubscribe
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        ItemKey key = getKey(event.Ore);
        Set<Integer> ids = itemToOreIDs.get(key);
        if (ids == null) {
            ids = new HashSet<Integer>();
            itemToOreIDs.put(key, ids);
        }
        ids.add(OreDictionary.getOreID(event.Name));
    }

    public static int[] getOreIDs(ItemStack itemStack) {
        if (itemStack == null) return new int[0];

        Set<Integer> ids = new HashSet<Integer>();

        ItemKey specificKey = getKey(itemStack);
        if (itemToOreIDs.containsKey(specificKey)) {
            ids.addAll(itemToOreIDs.get(specificKey));
        }

        ItemKey wildcardKey = new ItemKey(itemStack.itemID, OreDictionary.WILDCARD_VALUE);
        if (itemToOreIDs.containsKey(wildcardKey)) {
            ids.addAll(itemToOreIDs.get(wildcardKey));
        }

        int[] result = new int[ids.size()];
        int i = 0;
        for (Integer id : ids) {
            result[i++] = id;
        }
        return result;
    }

    public static boolean match(ItemStack itemStack1, ItemStack itemStack2) {
        int[] ids1 = getOreIDs(itemStack1);
        int[] ids2 = getOreIDs(itemStack2);
        for (int id1 : ids1)
            for (int id2 : ids2)
                if (id1 == id2)
                    return true;
        return false;
    }

    private static class ItemKey {
        private final int id;
        private final int meta;

        public ItemKey(int id, int meta) {
            this.id = id;
            this.meta = meta;
        }

        public int id() {
            return id;
        }

        public int meta() {
            return meta;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            ItemKey itemKey = (ItemKey) o;
            return id == itemKey.id && meta == itemKey.meta;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + meta;
            return result;
        }
    }
}
