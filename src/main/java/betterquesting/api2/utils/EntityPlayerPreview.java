package betterquesting.api2.utils;

import betterquesting.core.BetterQuesting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import betterquesting.backport.ResourceLocation;
import net.minecraft.world.World;

// FIXME restore behavior
public class EntityPlayerPreview extends EntityOtherPlayerMP
{
	private final ResourceLocation resource;
	
	/**
	 * Backup constructor. DO NOT USE
	 */
	public EntityPlayerPreview(World worldIn)
	{
		this(worldIn, "Notch");
	}
	
	public EntityPlayerPreview(World worldIn, String gameProfileIn)
	{
		super(worldIn, gameProfileIn);
		this.resource = new ResourceLocation(BetterQuesting.MODID, "textures/skin_cache/" + gameProfileIn);
	}


	//@Override
	public ResourceLocation getLocationSkin()
	{
		return this.resource;
	}
	
	//@Override
	public ResourceLocation getLocationCape()
	{
		return null;
	}
	
	//@Override
	public boolean func_152123_o()
	{
		return true;
	}
	
	//@Override
	public String getDisplayName()
	{
		return "";
	}
}
