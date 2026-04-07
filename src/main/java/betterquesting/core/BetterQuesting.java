package betterquesting.core;

import betterquesting.api.placeholders.EntityPlaceholder;
import betterquesting.api.placeholders.FluidPlaceholder;
import betterquesting.api.placeholders.ItemPlaceholder;
import betterquesting.blocks.BlockSubmitStation;
import betterquesting.blocks.TileSubmitStation;
import betterquesting.client.CreativeTabQuesting;
import betterquesting.commands.BQ_CommandAdmin;
import betterquesting.commands.BQ_CommandDebug;
import betterquesting.commands.BQ_CommandUser;
import betterquesting.core.proxies.CommonProxy;
import betterquesting.handlers.ConfigHandler;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.items.ItemExtraLife;
import betterquesting.items.ItemGuideBook;
import betterquesting.network.PacketHandler;
import betterquesting.network.PacketTypeRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.logging.Logger;

@Mod(modid = BetterQuesting.MODID, name = BetterQuesting.NAME)
@NetworkMod(
        clientSideRequired = true,
        channels = {BetterQuesting.CHANNEL},
        packetHandler = PacketHandler.class
)
public class BetterQuesting
{
    public static final String MODID = "betterquesting";
    public static final String NAME = "BetterQuesting";
    public static final String PROXY = "betterquesting.core.proxies";
    public static final String CHANNEL = "BQ_NET_CHAN";
    public static final String FORMAT = "2.0.0";
	
	@Instance(MODID)
	public static BetterQuesting instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	
	public static CreativeTabs tabQuesting = new CreativeTabQuesting();

    // FIXME Make block and item IDs configurable
	public static Item extraLife = new ItemExtraLife(8250);
	public static Item guideBook = new ItemGuideBook(8251);

	public static Block submitStation = new BlockSubmitStation(2325);
    
    @ForgeSubscribe
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	
    	ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile(), true);
    	ConfigHandler.initConfigs();
    	
    	proxy.registerHandlers();
    	
    	PacketTypeRegistry.INSTANCE.init();
    }
    
    @ForgeSubscribe
    public void init(FMLInitializationEvent event)
    {
        // FIXME: fluidPlaceholder
    	FluidRegistry.registerFluid(FluidPlaceholder.fluidPlaceholder);
    	
    	GameRegistry.registerItem(ItemPlaceholder.placeholder, "placeholder");
    	GameRegistry.registerItem(extraLife, "extra_life");
    	GameRegistry.registerItem(guideBook, "guide_book");
    	
    	GameRegistry.registerBlock(submitStation, "submit_station");
    	
    	GameRegistry.registerTileEntity(TileSubmitStation.class, "submit_station");
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(submitStation), new ItemStack(Item.book), new ItemStack(Block.glass), new ItemStack(Block.chest));
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 1, 0), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 2));
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 1, 0), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 1));
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 1, 0), new ItemStack(extraLife, 1, 1), new ItemStack(extraLife, 1, 1));
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 2, 1), new ItemStack(extraLife, 1, 0));
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 1, 1), new ItemStack(extraLife, 1, 2), new ItemStack(extraLife, 1, 2));
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(extraLife, 2, 2), new ItemStack(extraLife, 1, 1));
    	
    	GameRegistry.addShapelessRecipe(new ItemStack(submitStation), new ItemStack(Item.book), new ItemStack(Block.chest), new ItemStack(Block.glass));
    	
    	EntityRegistry.registerModEntity(EntityPlaceholder.class, "placeholder", 0, this, 16, 1, false);
    	
    	proxy.registerRenderers();
    }
    
    @ForgeSubscribe
    public void postInit(FMLPostInitializationEvent event)
    {
    }
	
	@ForgeSubscribe
	public void serverStart(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		
		manager.registerCommand(new BQ_CommandAdmin());
		manager.registerCommand(new BQ_CommandUser());
  
		if((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")) manager.registerCommand(new BQ_CommandDebug());
		
		SaveLoadHandler.INSTANCE.loadDatabases(server);
	}
	
	@ForgeSubscribe
	public void serverStop(FMLServerStoppedEvent event)
	{
		SaveLoadHandler.INSTANCE.unloadDatabases();
	}
}
