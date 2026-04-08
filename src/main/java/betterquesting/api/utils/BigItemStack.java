package betterquesting.api.utils;

import betterquesting.api2.utils.OreIngredient;
import betterquesting.backport.ItemUtils;
import betterquesting.backport.NbtUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose built container class for holding ItemStacks larger than 127. <br>
 * <b>For storage purposes only!
 */
public class BigItemStack
{
    private static final OreIngredient NO_ORE = new OreIngredient("");
	public int stackSize;
	private String oreDict = "";
	private OreIngredient oreIng = NO_ORE;
	@Nonnull
	private ItemStack baseStack;
	
	public BigItemStack(ItemStack stack)
	{
        this.baseStack = stack.copy();
        this.stackSize = baseStack.stackSize;
        baseStack.stackSize = 1;
	}
	
	public BigItemStack(@Nonnull Block block)
	{
		this(block, 1, 0);
	}
	
	public BigItemStack(@Nonnull Block block, int amount)
	{
		this(block, amount, 0);
	}
	
	public BigItemStack(@Nonnull Block block, int amount, int damage)
	{
		baseStack = new ItemStack(block, 1, damage);
		this.stackSize = amount;
	}
	
	public BigItemStack(@Nonnull Item item)
	{
		this(item, 1, 0);
	}
	
	public BigItemStack(@Nonnull Item item, int amount)
	{
		this(item, amount, 0);
	}
	
	public BigItemStack(@Nonnull Item item, int amount, int damage)
	{
		baseStack = new ItemStack(item, 1, damage);
		this.stackSize = amount;
	}
	
	/**
	 * @return ItemStack this BigItemStack is based on. Changing the base stack size does NOT affect the BigItemStack's size
	 */
	@Nonnull
	public ItemStack getBaseStack()
	{
		return baseStack;
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasOreDict()
    {
        return oreDict != null && !oreDict.isEmpty() && this.oreIng.getMatchingStacks().length > 0;
    }
	
	@Nonnull
	public String getOreDict()
    {
        return this.oreDict;
    }
    
    @Nonnull
    public OreIngredient getOreIngredient()
    {
        return this.oreIng;
    }
    
    public BigItemStack setOreDict(@Nonnull String ore)
    {
        this.oreDict = ore;
        this.oreIng = ore.length() <= 0 ? NO_ORE : new OreIngredient(ore);
        return this;
    }
	
	/**
	 * Shortcut method to the NBTTagCompound in the base ItemStack
	 */
	public NBTTagCompound GetTagCompound()
	{
		return baseStack.getTagCompound();
	}
	
	/**
	 * Shortcut method to the NBTTagCompound in the base ItemStack
	 */
	public void SetTagCompound(NBTTagCompound tags)
	{
		baseStack.setTagCompound(tags);
	}
	
	/**
	 * Shortcut method to the NBTTagCompound in the base ItemStack
	 */
	@SuppressWarnings("WeakerAccess")
    public boolean HasTagCompound()
	{
		return baseStack.hasTagCompound();
	}
	
	/**
	 * Breaks down this big stack into smaller ItemStacks for Minecraft to use (Individual stack size is dependent on the item)
	 */
	@SuppressWarnings("unused")
    public List<ItemStack> getCombinedStacks()
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		int tmp1 = Math.max(1, stackSize); // Guarantees this method will return at least 1 item
		
		while(tmp1 > 0)
		{
			int size = Math.min(tmp1, baseStack.getMaxStackSize());
			ItemStack stack = baseStack.copy();
			stack.stackSize = size;
			list.add(stack);
			tmp1 -= size;
		}
		
		return list;
	}
	
	public BigItemStack copy()
	{
		BigItemStack stack = new BigItemStack(baseStack.copy());
		stack.stackSize = this.stackSize;
		stack.oreDict = this.oreDict;
		return stack;
	}
	
	@Override
	public boolean equals(Object stack)
	{
	    if(stack == baseStack) return true;
	    
		if(stack instanceof ItemStack)
		{
			return baseStack.isItemEqual((ItemStack)stack) && ItemStack.areItemStackTagsEqual(baseStack, (ItemStack)stack);
		}
		
		return super.equals(stack);
	}
	
	@Nullable
    public static BigItemStack loadItemStackFromNBT(@Nonnull NBTTagCompound nbt) // Can load normal ItemStack NBTs. Does NOT deal with placeholders
	{
        int itemID = nbt.getShort("id");
        if (ItemUtils.getByIdOrNull(itemID) == null) {
            return null;
        }

        int stackSize = NbtUtils.intValue(nbt.getTag("Count"));
        int meta = nbt.getShort("Damage");
        if (meta < 0) {
            meta = OreDictionary.WILDCARD_VALUE;
        }

        NBTTagCompound tag = nbt.hasKey("tag") ? nbt.getCompoundTag("tag") : null;

        ItemStack miniStack = new ItemStack(itemID, stackSize, meta);
        miniStack.setTagCompound(tag);

		BigItemStack bigStack = new BigItemStack(miniStack);
		bigStack.stackSize = stackSize;
		bigStack.setOreDict(nbt.getString("OreDict"));

		return bigStack;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		baseStack.writeToNBT(nbt);
		nbt.setInteger("Count", this.stackSize);
		nbt.setString("OreDict", this.getOreDict());
		return nbt;
	}
}
