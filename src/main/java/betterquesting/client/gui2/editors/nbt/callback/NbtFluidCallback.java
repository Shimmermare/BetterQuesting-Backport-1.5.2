package betterquesting.client.gui2.editors.nbt.callback;

import betterquesting.backport.LiquidUtils;
import net.minecraft.nbt.NBTTagCompound;
import betterquesting.api.misc.ICallback;
import betterquesting.api.utils.JsonHelper;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class NbtFluidCallback implements ICallback<LiquidStack>
{
	private final NBTTagCompound json;
	
	public NbtFluidCallback(NBTTagCompound json)
	{
		this.json = json;
	}
	
	public void setValue(LiquidStack stack)
	{
        LiquidStack baseStack;
		
		if(stack != null)
		{
			baseStack = stack;
		} else
		{
            baseStack = LiquidDictionary.getLiquid(LiquidUtils.WATER_NAME, 1000);
		}
		
		JsonHelper.ClearCompoundTag(json);
		JsonHelper.FluidStackToJson(baseStack, json);
	}
}
