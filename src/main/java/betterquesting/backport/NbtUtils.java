package betterquesting.backport;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NbtUtils {
    public static boolean hasKey(NBTTagCompound compound, String key, int type) {
        NBTBase tag = compound.getTag(key);
        if (tag == null) return false;

        if (type == 99 /* Any numeric */) {
            return tag.getId() >= 1 && tag.getId() <= 6;
        }

        return tag.getId() == type;
    }

    public static NBTTagList getTagList(NBTTagCompound compound, String key, int type) {
        NBTBase tag = compound.getTag(key);
        if (tag == null || tag.getId() != 9) return new NBTTagList();

        NBTTagList list = (NBTTagList) tag;
        if (list.tagCount() == 0) return list;


        return list.tagAt(list.tagCount() - 1).getId() == type ? list : new NBTTagList();
    }

    public static NBTTagCompound getCompoundTagAt(NBTTagList list, int i) {
        if (i >= 0 && i < list.tagCount()) {
            NBTBase tag = list.tagAt(i);
            return tag.getId() == 10 ? (NBTTagCompound) tag : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }
}
