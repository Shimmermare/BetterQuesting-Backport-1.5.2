package betterquesting.api.properties.basic;

import betterquesting.backport.NbtUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import betterquesting.backport.ResourceLocation;

public class PropertyTypeBoolean extends PropertyTypeBase<Boolean>
{
	public PropertyTypeBoolean(ResourceLocation key, Boolean def)
	{
		super(key, def);
	}
	
	@Override
	public Boolean readValue(NBTBase nbt)
	{
		if(nbt == null || nbt.getId() < 1 || nbt.getId() > 6)
		{
			return this.getDefault();
		}
		
		try
		{
			return NbtUtils.byteValue(nbt) > 0;
		} catch(Exception e)
		{
			return this.getDefault();
		}
	}
	
	@Override
	public NBTBase writeValue(Boolean value)
	{
		if(value == null)
		{
			return new NBTTagByte(null, this.getDefault() ? (byte)1 : (byte)0);
		}
		
		return new NBTTagByte(null, value ? (byte)1 : (byte)0);
	}
}