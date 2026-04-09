package betterquesting.core;

import betterquesting.api.placeholders.EntityPlaceholder;
import betterquesting.api.placeholders.FluidPlaceholder;
import betterquesting.api.placeholders.ItemPlaceholder;
import betterquesting.api.storage.BQ_Settings;
import betterquesting.backport.OreDictionaryHelper;
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
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	public static Item extraLife;
	public static Item guideBook;
    public static Item placeholder;

    public static Block fluidPlaceholder;
	public static Block submitStation;

    public static File modConfigDir;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();

        modConfigDir = new File(event.getModConfigurationDirectory(), NAME);

        File modConfigFile = new File(modConfigDir, "Main.cfg");
    	ConfigHandler.config = new Configuration(modConfigFile, true);
    	ConfigHandler.initConfigs();
    	
    	extraLife = new ItemExtraLife(BQ_Settings.itemExtraLifeId);
    	guideBook = new ItemGuideBook(BQ_Settings.itemGuideBookId);
    	placeholder = new ItemPlaceholder(BQ_Settings.itemPlaceholderId);
    	
    	fluidPlaceholder = new FluidPlaceholder(BQ_Settings.blockFluidPlaceholderId);
    	submitStation = new BlockSubmitStation(BQ_Settings.blockSubmitStationId);
    	
    	proxy.registerHandlers();
    	
    	PacketTypeRegistry.INSTANCE.init();

        loadLocalizations();
    }
    
    @Mod.Init
    public void init(FMLInitializationEvent event)
    {
        LiquidDictionary.getOrCreateLiquid("Placeholder",  new LiquidStack(fluidPlaceholder, 1000));
    	
    	GameRegistry.registerItem(BetterQuesting.placeholder, "placeholder");
    	GameRegistry.registerItem(extraLife, "extra_life");
    	GameRegistry.registerItem(guideBook, "guide_book");
    	
    	GameRegistry.registerBlock(submitStation, "submit_station");
    	GameRegistry.registerBlock(fluidPlaceholder, "fluid_placeholder");

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
    
    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        OreDictionaryHelper.init();
    }
	
	@Mod.ServerStarting
	public void serverStart(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		
		manager.registerCommand(new BQ_CommandAdmin());
		manager.registerCommand(new BQ_CommandUser());

		if(isDeobfuscated()) manager.registerCommand(new BQ_CommandDebug());
		
		SaveLoadHandler.INSTANCE.loadDatabases(server);
	}
	
	@Mod.ServerStopped
	public void serverStop(FMLServerStoppedEvent event)
	{
		SaveLoadHandler.INSTANCE.unloadDatabases();
	}

    private boolean isDeobfuscated() {
        return World.class.getSimpleName().equals("World");
    }

    private void loadLocalizations() {
        @SuppressWarnings("unchecked")
        Map<String, String> languages = StringTranslate.getInstance().getLanguageList();

        List<String> loaded = new ArrayList<String>();
        for (Map.Entry<String, String> entry : languages.entrySet()) {
            String lang = entry.getKey();
            String file = "/mods/betterquesting/lang/" + lang + ".lang";
            if (this.getClass().getResource(file) != null) {
                LanguageRegistry.instance().loadLocalization(file, lang, false);
                loaded.add(lang);
            }
        }

        logger.info("Loaded localizations: " + loaded);
    }
}
