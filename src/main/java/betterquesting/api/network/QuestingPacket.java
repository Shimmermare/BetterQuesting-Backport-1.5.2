package betterquesting.api.network;

import net.minecraft.nbt.NBTTagCompound;
import betterquesting.backport.ResourceLocation;

public final class QuestingPacket
{
	private final ResourceLocation handler;
	private final NBTTagCompound payload;
	
	public QuestingPacket(ResourceLocation handler, NBTTagCompound payload)
	{
		this.handler = handler;
		this.payload = payload;
	}
	
	public ResourceLocation getHandler()
	{
		return handler;
	}
	
	public NBTTagCompound getPayload()
	{
		return payload;
	}
}
