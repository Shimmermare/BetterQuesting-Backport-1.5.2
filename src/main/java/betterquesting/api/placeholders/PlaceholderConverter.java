package betterquesting.api.placeholders;

import betterquesting.api.utils.BigItemStack;
import betterquesting.backport.ItemUtils;
import betterquesting.backport.NbtUtils;
import betterquesting.core.BetterQuesting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

/**
 * In charge of safely converting to or from placeholder objects
 */
public class PlaceholderConverter
{
	public static Entity convertEntity(Entity orig, World world, NBTTagCompound nbt)
	{
		Entity entity = orig;
		
		if(orig == null)
		{
			entity = new EntityPlaceholder(world);
			((EntityPlaceholder)entity).SetOriginalTags(nbt);
		} else if(orig instanceof EntityPlaceholder)
		{
			EntityPlaceholder p = (EntityPlaceholder)orig;
			Entity tmp = EntityList.createEntityFromNBT(p.GetOriginalTags(), world);
			entity = tmp != null? tmp : p;
		}
		
		return entity;
	}
	
	public static BigItemStack convertItem(Item item, short id, int count, int damage, String oreDict, NBTTagCompound nbt)
	{
		if(item == null)
		{
			BigItemStack stack = new BigItemStack(BetterQuesting.placeholder, count, damage).setOreDict(oreDict);
			stack.SetTagCompound(new NBTTagCompound());
			stack.GetTagCompound().setShort("orig_id", id);
			stack.GetTagCompound().setInteger("orig_meta", damage);
			if(nbt != null) stack.GetTagCompound().setTag("orig_tag", nbt);
			return stack;
		}

        if(item == BetterQuesting.placeholder && nbt != null)
		{
            int origId = NbtUtils.shortValue(nbt.getTag("orig_id"));
            Item restored = ItemUtils.getByIdOrNull(origId);

            if(restored != null)
            {
                BigItemStack stack = new BigItemStack(restored, count, nbt.hasKey("orig_meta")
                        ? nbt.getInteger("orig_meta")
                        : damage).setOreDict(oreDict);
                if(nbt.hasKey("orig_tag")) stack.SetTagCompound(nbt.getCompoundTag("orig_tag"));

                return stack;
            } else if(damage > 0 && !nbt.hasKey("orig_meta"))
            {
                nbt.setInteger("orig_meta", damage);
                damage = 0;
            }
		}
		
		BigItemStack stack = new BigItemStack(item, count, damage).setOreDict(oreDict);
		if(nbt != null) stack.SetTagCompound(nbt);
		
		return stack;
	}
	
	public static LiquidStack convertFluid(LiquidStack fluid, String name, int amount, NBTTagCompound nbt)
	{
		if(fluid == null)
		{
            LiquidStack stack = new LiquidStack(BetterQuesting.fluidPlaceholder, amount);
			NBTTagCompound orig = new NBTTagCompound();
			orig.setString("orig_id", name);
			if(nbt != null) orig.setTag("orig_tag", nbt);
			stack.extra = orig;
			return stack;
		} else if(fluid.itemID == BetterQuesting.fluidPlaceholder.blockID && nbt != null)
		{
              LiquidStack stack = LiquidDictionary.getLiquid(nbt.getString("orig_id"), amount);
              if(nbt.hasKey("orig_tag")) stack.extra = nbt.getCompoundTag("orig_tag");
              return stack;
		}

        LiquidStack stack = fluid.copy();
        stack.amount = amount;
		if(nbt != null) stack.extra = nbt;
		
		return stack;
	}
}
