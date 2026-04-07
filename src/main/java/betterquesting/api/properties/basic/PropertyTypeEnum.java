package betterquesting.api.properties.basic;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import betterquesting.backport.ResourceLocation;

public class PropertyTypeEnum<E extends Enum<E>> extends PropertyTypeBase<E>
{
	private final Class<E> eClazz;
	
	public PropertyTypeEnum(ResourceLocation key, E def)
	{
		super(key, def);
		
		eClazz = def.getDeclaringClass();
	}
	
	@Override
	public E readValue(NBTBase nbt)
	{
		if(nbt == null || nbt.getId() != 8)
		{
			return this.getDefault();
		}
		
		try
		{
			return Enum.valueOf(eClazz, nbt.toString());
		} catch(Exception e)
		{
			return this.getDefault();
		}
	}
	
	@Override
	public NBTBase writeValue(E value)
	{
		if(value == null)
		{
			return new NBTTagString(null, this.getDefault().toString());
		}
		
		return new NBTTagString(null, value.toString());
	}
}
