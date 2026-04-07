package betterquesting.api.properties.basic;

import betterquesting.backport.NbtUtils;
import net.minecraft.nbt.NBTBase;
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
		if(nbt == null || !NbtUtils.isPrimitive(nbt))
		{
			return this.getDefault();
		}
		
		return NbtUtils.intValue(nbt);
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
