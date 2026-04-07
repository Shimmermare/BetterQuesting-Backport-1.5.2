package betterquesting.api.properties.basic;

import betterquesting.backport.NbtUtils;
import betterquesting.backport.ResourceLocation;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;

public class PropertyTypeDouble extends PropertyTypeBase<Double>
{
	public PropertyTypeDouble(ResourceLocation key, Double def)
	{
		super(key, def);
	}

	@Override
	public Double readValue(NBTBase nbt)
	{
		if(nbt == null || !NbtUtils.isPrimitive(nbt))
		{
			return this.getDefault();
		}

		return NbtUtils.doubleValue(nbt);
	}

	@Override
	public NBTBase writeValue(Double value)
	{
		if(value == null)
		{
			return new NBTTagDouble(null, this.getDefault());
		}

		return new NBTTagDouble(null, value);
	}
}
