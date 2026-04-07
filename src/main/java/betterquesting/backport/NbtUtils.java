package betterquesting.backport;

import net.minecraft.nbt.*;
import net.minecraft.util.MathHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NbtUtils {
    public static boolean hasKey(NBTTagCompound compound, String key, int type) {
        NBTBase tag = compound.getTag(key);
        if (tag == null) return false;

        if (type == 99 /* Any numeric */) {
            return tag.getId() >= 1 && tag.getId() <= 6;
        }

        return tag.getId() == type;
    }

    public static byte getId(NBTTagCompound compound, String key) {
        NBTBase tag = compound.getTag(key);
        return tag != null ? tag.getId() : 0;
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

    public static String getStringTagAt(NBTTagList list, int i) {
        if (i >= 0 && i < list.tagCount()) {
            NBTBase tag = list.tagAt(i);
            return tag.toString();
        } else {
            return "";
        }
    }

    public static boolean isPrimitive(NBTBase tag) {
        return tag instanceof NBTTagByte
                || tag instanceof NBTTagShort
                || tag instanceof NBTTagInt
                || tag instanceof NBTTagLong
                || tag instanceof NBTTagFloat
                || tag instanceof NBTTagDouble;
    }

    public static byte byteValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return (byte) (((NBTTagShort) tag).data & 0xFF);
        }
        if (tag instanceof NBTTagInt) {
            return (byte) (((NBTTagInt) tag).data & 0xFF);
        }
        if (tag instanceof NBTTagLong) {
            return (byte) (((NBTTagLong) tag).data & 0xFF);
        }
        if (tag instanceof NBTTagFloat) {
            return (byte) (MathHelper.floor_float(((NBTTagFloat) tag).data) & 0xFF);
        }
        if (tag instanceof NBTTagDouble) {
            return (byte) (MathHelper.floor_double(((NBTTagDouble) tag).data) & 0xFF);
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static short shortValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).data;
        }
        if (tag instanceof NBTTagInt) {
            return (short) (((NBTTagInt) tag).data & 0xFFFF);
        }
        if (tag instanceof NBTTagLong) {
            return (short) (((NBTTagLong) tag).data & 0xFFFF);
        }
        if (tag instanceof NBTTagFloat) {
            return (short) (MathHelper.floor_float(((NBTTagFloat) tag).data) & 0xFFFF);
        }
        if (tag instanceof NBTTagDouble) {
            return (short) (MathHelper.floor_double(((NBTTagDouble) tag).data) & 0xFFFF);
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static int intValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).data;
        }
        if (tag instanceof NBTTagInt) {
            return ((NBTTagInt) tag).data;
        }
        if (tag instanceof NBTTagLong) {
            return (int) (((NBTTagLong) tag).data);
        }
        if (tag instanceof NBTTagFloat) {
            return MathHelper.floor_float(((NBTTagFloat) tag).data);
        }
        if (tag instanceof NBTTagDouble) {
            return MathHelper.floor_double(((NBTTagDouble) tag).data);
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static long longValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).data;
        }
        if (tag instanceof NBTTagInt) {
            return ((NBTTagInt) tag).data;
        }
        if (tag instanceof NBTTagLong) {
            return ((NBTTagLong) tag).data;
        }
        if (tag instanceof NBTTagFloat) {
            return MathHelper.floor_float(((NBTTagFloat) tag).data);
        }
        if (tag instanceof NBTTagDouble) {
            return MathHelper.floor_double_long(((NBTTagDouble) tag).data);
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static float floatValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).data;
        }
        if (tag instanceof NBTTagInt) {
            return ((NBTTagInt) tag).data;
        }
        if (tag instanceof NBTTagLong) {
            return ((NBTTagLong) tag).data;
        }
        if (tag instanceof NBTTagFloat) {
            return ((NBTTagFloat) tag).data;
        }
        if (tag instanceof NBTTagDouble) {
            return (float) ((NBTTagDouble) tag).data;
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static double doubleValue(NBTBase tag) {
        if (tag instanceof NBTTagByte) {
            return ((NBTTagByte) tag).data;
        }
        if (tag instanceof NBTTagShort) {
            return ((NBTTagShort) tag).data;
        }
        if (tag instanceof NBTTagInt) {
            return ((NBTTagInt) tag).data;
        }
        if (tag instanceof NBTTagLong) {
            return ((NBTTagLong) tag).data;
        }
        if (tag instanceof NBTTagFloat) {
            return ((NBTTagFloat) tag).data;
        }
        if (tag instanceof NBTTagDouble) {
            return ((NBTTagDouble) tag).data;
        }
        throw new IllegalArgumentException("Invalid tag type: " + tag.getClass().getName());
    }

    public static Set<String> getKeys(NBTTagCompound compound) {
        @SuppressWarnings("unchecked")
        Collection<NBTBase> tags = (Collection<NBTBase>) compound.getTags();
        if (tags.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> keys = new HashSet<String>();
        for (NBTBase tag : tags) {
            keys.add(tag.getName());
        }
        return keys;
    }

    public static void setAt(NBTTagList list, int index, NBTBase tag) {
        if (index >= 0 && index < list.tagCount()) {
            int tagType = list.tagAt(index).getId();
            if (tagType != 0 && tagType != tag.getId()) {
                System.err.println("WARNING: Adding mismatching tag types to tag list");
                return;
            }

            NBTBase[] items = new NBTBase[list.tagCount()];
            for (int i = 0; i < items.length; i++) {
                items[i] = i == index ? tag : list.tagAt(i);
            }
            for (int i = items.length - 1; i >= 0; i--) {
                list.removeTag(i);
            }
            for (NBTBase item : items) {
                list.appendTag(item);
            }
        } else {
            System.err.println("WARNING: index out of bounds to set tag in tag list");
        }
    }
}
