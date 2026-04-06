package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import betterquesting.backport.ResourceLocation;

public class PropertyTypeInteger extends PropertyTypeBase<Integer>
{
	public PropertyTypeInteger(ResourceLocation key, Integer def)
	{
		super(key, def);
	}

	@Override
	public Integer readValue(NBTBase nbt)
	{
		if(nbt == null || !(nbt instanceof NBTPrimitive))
		{
			return this.getDefault();
		}
		
		return ((NBTPrimitive)nbt).func_150287_d();
	}

	@Override
	public NBTBase writeValue(Integer value)
	{
		if(value == null)
		{
			return new NBTTagInt(null, this.getDefault());
		}
		
		return new NBTTagInt(null, value);
	}
}
