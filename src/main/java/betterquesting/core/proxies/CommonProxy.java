package betterquesting.core.proxies;

import betterquesting.backport.OreDictionaryHelper;
import betterquesting.core.BetterQuesting;
import betterquesting.core.ExpansionLoader;
import betterquesting.handlers.EventHandler;
import betterquesting.handlers.GuiHandler;
import betterquesting.handlers.BQPlayerTracker;
import betterquesting.handlers.ServerTickHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		ExpansionLoader.INSTANCE.initCommonAPIs();
		
		MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new OreDictionaryHelper());
		MinecraftForge.TERRAIN_GEN_BUS.register(EventHandler.INSTANCE);

        TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        GameRegistry.registerPlayerTracker(new BQPlayerTracker());

		NetworkRegistry.instance().registerGuiHandler(BetterQuesting.instance, new GuiHandler());
	}
	
	public void registerRenderers()
	{
	}
}
