package betterquesting.core.proxies;

import betterquesting.api.placeholders.EntityPlaceholder;
import betterquesting.api2.client.gui.SceneController;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.client.BQ_Keybindings;
import betterquesting.client.QuestNotification;
import betterquesting.client.renderer.EntityPlaceholderRenderer;
import betterquesting.client.themes.ThemeRegistry;
import betterquesting.client.toolbox.ToolboxRegistry;
import betterquesting.client.toolbox.ToolboxTabMain;
import betterquesting.core.BetterQuesting;
import betterquesting.core.ExpansionLoader;
import cpw.mods.fml.client.registry.RenderingRegistry;
import betterquesting.backport.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void registerHandlers()
	{
		super.registerHandlers();
		
		// TODO: Stencil bits are disabled by default in 1.7.10 and therefore cannot be used reliably for the GUIs
		/*if(!Minecraft.getMinecraft().getFramebuffer().useDepth)
		{
			if(!Minecraft.getMinecraft().getFramebuffer().enableStencil())
			{
				BetterQuesting.logger.severe("[!] FAILED TO ENABLE STENCIL BUFFER. GUIS WILL BREAK! [!]");
			}
		}*/
		
		MinecraftForge.EVENT_BUS.register(PEventBroadcaster.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new SceneController());
		
		ExpansionLoader.INSTANCE.initClientAPIs();
		
		MinecraftForge.EVENT_BUS.register(new QuestNotification());
		BQ_Keybindings.RegisterKeys();
		
		ToolboxRegistry.INSTANCE.registerToolTab(new ResourceLocation(BetterQuesting.MODID, "main"), ToolboxTabMain.INSTANCE);
	}
	
	@Override
	public void registerRenderers()
	{
		super.registerRenderers();
		
		RenderingRegistry.registerEntityRenderingHandler(EntityPlaceholder.class, new EntityPlaceholderRenderer());
		
		ThemeRegistry.INSTANCE.loadResourceThemes();
	}
}
