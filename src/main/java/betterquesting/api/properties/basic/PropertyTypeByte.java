package betterquesting.api.properties.basic;

import betterquesting.backport.NbtUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import betterquesting.backport.ResourceLocation;

public class PropertyTypeByte extends PropertyTypeBase<Byte>
{
	public PropertyTypeByte(ResourceLocation key, Byte def)
	{
		super(key, def);
	}

	@Override
	public Byte readValue(NBTBase nbt)
	{
		if(nbt == null || !NbtUtils.isPrimitive(nbt))
		{
			return this.getDefault();
		}
		
		return NbtUtils.byteValue(nbt);
	}

	@Override
	public NBTBase writeValue(Byte value)
	{
		if(value == null)
		{
			return new NBTTagByte(null, this.getDefault());
		}
		
		return new NBTTagByte(null, value);
	}
}
