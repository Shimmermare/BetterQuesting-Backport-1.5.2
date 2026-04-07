package betterquesting.api.properties.basic;

import betterquesting.backport.NbtUtils;
import betterquesting.backport.ResourceLocation;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;

public class PropertyTypeFloat extends PropertyTypeBase<Float>
{
	public PropertyTypeFloat(ResourceLocation key, Float def)
	{
		super(key, def);
	}

	@Override
	public Float readValue(NBTBase nbt)
	{
		if(nbt == null || !NbtUtils.isPrimitive(nbt))
		{
			return this.getDefault();
		}
		
		return NbtUtils.floatValue(nbt);
	}

	@Override
	public NBTBase writeValue(Float value)
	{
		if(value == null)
		{
			return new NBTTagFloat(null, this.getDefault());
		}
		
		return new NBTTagFloat(null, value);
	}
}
