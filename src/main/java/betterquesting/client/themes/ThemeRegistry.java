package betterquesting.client.themes;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.FileExtensionFilter;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.lines.SimpleLine;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.SlicedTexture;
import betterquesting.api2.client.gui.themes.GuiKey;
import betterquesting.api2.client.gui.themes.IGuiTheme;
import betterquesting.api2.client.gui.themes.IThemeRegistry;
import betterquesting.api2.client.gui.themes.gui_args.GArgsCallback;
import betterquesting.api2.client.gui.themes.gui_args.GArgsFileBrowser;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNBT;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNone;
import betterquesting.api2.client.gui.themes.presets.*;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.client.gui2.GuiHome;
import betterquesting.client.gui2.editors.GuiFileBrowser;
import betterquesting.client.gui2.editors.GuiTextEditor;
import betterquesting.client.gui2.editors.nbt.GuiEntitySelection;
import betterquesting.client.gui2.editors.nbt.GuiFluidSelection;
import betterquesting.client.gui2.editors.nbt.GuiItemSelection;
import betterquesting.client.gui2.editors.nbt.GuiNbtEditor;
import betterquesting.core.BetterQuesting;
import betterquesting.handlers.ConfigHandler;
import com.google.gson.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import betterquesting.backport.ResourceLocation;
import net.minecraftforge.common.Configuration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import betterquesting.backport.Function;
import net.minecraftforge.liquids.LiquidStack;

public class ThemeRegistry implements IThemeRegistry
{
	public static final ThemeRegistry INSTANCE = new ThemeRegistry();
	
	private static final IGuiTexture NULL_TEXTURE = new SlicedTexture(PresetTexture.TX_NULL, new GuiRectangle(0,0,32,32), new GuiPadding(8,8,8,8));
	private static final IGuiLine NULL_LINE = new SimpleLine();
	private static final IGuiColor NULL_COLOR = new GuiColorStatic(0xFF000000);
	
	private final HashMap<ResourceLocation, IGuiTexture> defTextures = new HashMap<ResourceLocation, IGuiTexture>();
	private final HashMap<ResourceLocation, IGuiLine> defLines = new HashMap<ResourceLocation, IGuiLine>();
	private final HashMap<ResourceLocation, IGuiColor> defColors = new HashMap<ResourceLocation, IGuiColor>();
	private final HashMap<GuiKey<?>, Function<?, GuiScreen>> defGuis = new HashMap<GuiKey<?>, Function<?, GuiScreen>>();
	
	private final HashMap<ResourceLocation, IGuiTheme> themes = new HashMap<ResourceLocation, IGuiTheme>();
	private final List<ResourceLocation> loadedThemes = new ArrayList<ResourceLocation>();
	
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private IGuiTheme activeTheme = null;
	
	private boolean setup = false;
	
	public ThemeRegistry()
	{
		PresetTexture.registerTextures(this);
		PresetIcon.registerIcons(this);
		PresetLine.registerLines(this);
		PresetColor.registerColors(this);
        
        setDefaultGui(PresetGUIs.HOME, new Function<GArgsNone, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsNone arg) {
                return new GuiHome(arg.parent);
            }
        });

        setDefaultGui(PresetGUIs.EDIT_NBT, new Function<GArgsNBT, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsNBT arg) {
                if (arg.value instanceof NBTTagCompound) {
                    //noinspection unchecked
                    return new GuiNbtEditor(arg.parent, (NBTTagCompound) arg.value, arg.callback);
                } else if (arg.value instanceof NBTTagList) {
                    //noinspection unchecked
                    return new GuiNbtEditor(arg.parent, (NBTTagList) arg.value, arg.callback);
                } else {
                    return null;
                }
            }
        });
        
        setDefaultGui(PresetGUIs.EDIT_ITEM, new Function<GArgsCallback<BigItemStack>, GuiScreen>() {
                    @Override
                    public GuiScreen apply(GArgsCallback<BigItemStack> arg) {
                        return new GuiItemSelection(arg.parent, arg.value, arg.callback);
                    }
                });
        setDefaultGui(PresetGUIs.EDIT_FLUID, new Function<GArgsCallback<LiquidStack>, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsCallback<LiquidStack> arg) {
                return new GuiFluidSelection(arg.parent, arg.value, arg.callback);
            }
        });
        setDefaultGui(PresetGUIs.EDIT_ENTITY, new Function<GArgsCallback<Entity>, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsCallback<Entity> arg) {
                return new GuiEntitySelection(arg.parent, arg.value, arg.callback);
            }
        });
        setDefaultGui(PresetGUIs.EDIT_TEXT, new Function<GArgsCallback<String>, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsCallback<String> arg) {
                return new GuiTextEditor(arg.parent, arg.value, arg.callback);
            }
        });
        setDefaultGui(PresetGUIs.FILE_EXPLORE, new Function<GArgsFileBrowser, GuiScreen>() {
            @Override
            public GuiScreen apply(GArgsFileBrowser arg) {
                return new GuiFileBrowser(arg.parent, arg.callback, arg.root, arg.filter)
                        .allowMultiSelect(arg.multiSelect);
            }
        });
	}
	
	@Override
	public void registerTheme(IGuiTheme theme)
	{
		if(theme == null || theme.getID() == null)
		{
			throw new NullPointerException("Cannot register null theme");
		} else if(themes.containsKey(theme.getID()))
		{
			throw new IllegalArgumentException("Cannot register duplicate theme: " + theme.getID());
		}
		
		themes.put(theme.getID(), theme);
		
		if(activeTheme == null) setup = false; // A theme was registered that could possibly resolve the currently configured theme
	}
	
	/**
	 * Sets the default fallback texture. Only use if you're defining your own custom texture ID
	 */
	@Override
	public void setDefaultTexture(ResourceLocation key, IGuiTexture tex)
	{
		if(key == null || tex == null)
		{
			throw new NullPointerException("Tried to register a default theme texture with one or more NULL arguments");
		}
		
		defTextures.put(key, tex);
	}
	
	/**
	 * Sets the default fallback texture. Only use if you're defining your own custom texture ID
	 */
	@Override
	public void setDefaultLine(ResourceLocation key, IGuiLine line)
	{
		if(key == null || line == null)
		{
			throw new NullPointerException("Tried to register a default theme line with one or more NULL arguments");
		}
		
        defLines.put(key, line);
	}
	
	/**
	 * Sets the default fallback texture. Only use if you're defining your own custom texture ID
	 */
	@Override
	public void setDefaultColor(ResourceLocation key, IGuiColor color)
	{
	    if(key == null || color == null)
        {
            throw new NullPointerException("Tried to register default theme colour with one or more NULL arguments");
        }
		
        defColors.put(key, color);
	}
	
	@Override
	public <T> void setDefaultGui(GuiKey<T> key, Function<T, GuiScreen> func)
    {
        if(key == null || func == null)
        {
            throw new NullPointerException("Tried to register a default gui with one or more NULL arguments");
        }
        
        defGuis.put(key, func);
    }
	
	@Override
	public void setTheme(ResourceLocation id)
	{
		setTheme(themes.get(id), id);
	}
	
	@Override
    public IGuiTheme getTheme(ResourceLocation key)
    {
        if(key == null) return null;
        return themes.get(key);
    }
	
	private void setTheme(IGuiTheme theme, ResourceLocation id)
	{
		this.activeTheme = theme;
		
		BQ_Settings.curTheme = id == null ? "" : id.toString();
		
		if(ConfigHandler.config != null)
		{
			ConfigHandler.config.get(Configuration.CATEGORY_GENERAL, "Theme", "").set(BQ_Settings.curTheme);
			ConfigHandler.config.save();
		} else
		{
			BetterQuesting.logger.log(Level.WARNING, "Unable to save theme setting");
		}
	}
	
	@Override
	public IGuiTheme getCurrentTheme()
	{
	    if(!setup && this.activeTheme == null)
        {
            this.activeTheme = this.getTheme(new ResourceLocation(BQ_Settings.curTheme));
            setup = true;
        }
        
		return this.activeTheme;
	}
	
    @Override
    @SuppressWarnings("unchecked")
    public void loadResourceThemes() {
        for (ResourceLocation resLoc : loadedThemes) {
            themes.remove(resLoc);
        }
        loadedThemes.clear();

        File themeFile = new File(BetterQuesting.modConfigDir, "hq_themes.json");
        if (!themeFile.exists()) {
            placeDefaultThemeFile(themeFile);
        }

        loadThemeFromFile(themeFile);
    }

    private void placeDefaultThemeFile(File themeFile) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = ThemeRegistry.class.getResourceAsStream("/mods/betterquesting/default_bq_themes.json");
            if (in == null) {
                throw new IllegalStateException("Can't find default_hq_themes.json in jar");
            }
            out = new FileOutputStream(themeFile);
            byte[] buffer = new byte[1024];
            int length;

            // Read from source and write to destination
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (Exception e) {
            BetterQuesting.logger.log(Level.SEVERE, "Failed to write default theme file to " + themeFile.getPath(), e);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException ignored) {}
        }
    }

    private void loadThemeFromFile(File themeFile) {
        // No resourcepacks in 1.5.2, assume everyting is in mod domain.
        String domain = BetterQuesting.MODID;

        InputStreamReader isr = null;
        try
        {
            isr = new InputStreamReader(new FileInputStream(themeFile), Charset.forName("UTF-8"));
            JsonElement jsonElement = GSON.fromJson(isr, JsonElement.class);
            isr.close();

            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("themeType")) {
                BetterQuesting.logger.warning("Deprecated legacy theme " + themeFile.getName()
                        + " - please convert to the new format.");
                return;
            }

            JsonArray jAry = jsonElement.getAsJsonArray();

            for(int i = 0; i < jAry.size(); i++)
            {
                JsonElement je = jAry.get(i);

                if(!(je instanceof JsonObject))
                {
                    BetterQuesting.logger.log(Level.WARNING, "Invalid theme entry at index " + i + " in " + domain);
                    continue;
                }

                JsonObject jThm = je.getAsJsonObject();

                ResourceLocation parentID = !jThm.has("themeParent") ? null : new ResourceLocation(JsonHelper.GetString(jThm, "themeParent", "minecraft:null"));
                String themeName = JsonHelper.GetString(jThm, "themeName", "Unnamed Theme");
                String idRaw = JsonHelper.GetString(jThm, "themeID", themeName);
                idRaw = idRaw.toLowerCase().trim().replaceAll(" ", "_");
                if(!idRaw.contains(":")) idRaw = domain + ":" + idRaw;
                ResourceLocation themeId = new ResourceLocation(idRaw);

                int n = 0;
                while(themes.containsKey(themeId)) themeId = new ResourceLocation(domain, idRaw + n++);

                ResourceTheme resTheme;

                try
                {
                    resTheme = new ResourceTheme(parentID, themeId, themeName);
                } catch(Exception e)
                {
                    BetterQuesting.logger.log(Level.SEVERE, "Failed to load theme entry " + i + " in " + domain, e);
                    continue;
                }

                JsonObject jsonTextureRoot = JsonHelper.GetObject(jThm, "textures");
                for(Entry<String, JsonElement> entry : jsonTextureRoot.entrySet())
                {
                    if(!entry.getValue().isJsonObject()) continue;
                    JsonObject joTex = entry.getValue().getAsJsonObject();

                    ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joTex, "textureType", ""));
                    IFactoryData<IGuiTexture, JsonObject> tFact = ResourceRegistry.INSTANCE.getTexReg().getFactory(typeID);

                    if(tFact == null)
                    {
                        BetterQuesting.logger.severe("Unknown texture type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    IGuiTexture gTex = tFact.loadFromData(joTex);

                    if(gTex == null)
                    {
                        BetterQuesting.logger.severe("Failed to load texture type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    resTheme.setTexture(new ResourceLocation(entry.getKey()), gTex);
                }

                JsonObject jsonColourRoot = JsonHelper.GetObject(jThm, "colors");
                for(Entry<String, JsonElement> entry : jsonColourRoot.entrySet())
                {
                    if(!(entry.getValue() instanceof JsonObject)) continue;
                    JsonObject joCol = entry.getValue().getAsJsonObject();

                    ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joCol, "colorType", ""));
                    IFactoryData<IGuiColor, JsonObject> cFact = ResourceRegistry.INSTANCE.getColorReg().getFactory(typeID);

                    if(cFact == null)
                    {
                        BetterQuesting.logger.severe("Unknown color type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    IGuiColor gCol = cFact.loadFromData(joCol);

                    if(gCol == null)
                    {
                        BetterQuesting.logger.severe("Failed to load color type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    resTheme.setColor(new ResourceLocation(entry.getKey()), gCol);
                }

                JsonObject jsonLinesRoot = JsonHelper.GetObject(jThm, "lines");
                for(Entry<String, JsonElement> entry : jsonLinesRoot.entrySet())
                {
                    if(!(entry.getValue() instanceof JsonObject)) continue;
                    JsonObject joLine = entry.getValue().getAsJsonObject();

                    ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joLine, "lineType", ""));
                    IFactoryData<IGuiLine, JsonObject> lFact = ResourceRegistry.INSTANCE.getLineReg().getFactory(typeID);

                    if(lFact == null)
                    {
                        BetterQuesting.logger.severe("Unknown line type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    IGuiLine gLine = lFact.loadFromData(joLine);

                    if(gLine == null)
                    {
                        BetterQuesting.logger.severe("Failed to load line type " + typeID + " for theme " + themeName + " in " + domain);
                        continue;
                    }

                    resTheme.setLine(new ResourceLocation(entry.getKey()), gLine);
                }

                themes.put(resTheme.getID(), resTheme);
                loadedThemes.add(resTheme.getID());
            }
        } catch (Exception e)
        {
            BetterQuesting.logger.log(Level.SEVERE, "Error reading theme file at " + themeFile.getPath(), e);
        } finally
        {
            if(isr != null)
            {
                try { isr.close(); } catch(Exception ignored) {}
            }
        }
    }
	
	@Override
	public IGuiTexture getTexture(ResourceLocation key)
	{
		if(key == null) return NULL_TEXTURE;
		
		IGuiTexture tex = null;
		
		if(getCurrentTheme() != null) tex = activeTheme.getTexture(key);
		if(tex == null) tex = defTextures.get(key);
		return tex == null? NULL_TEXTURE : tex;
	}
	
	@Override
	public IGuiLine getLine(ResourceLocation key)
	{
		if(key == null) return NULL_LINE;
		
		IGuiLine line = null;
		
		if(getCurrentTheme() != null) line = activeTheme.getLine(key);
		if(line == null) line = defLines.get(key);
		return line == null? NULL_LINE : line;
	}
	
	@Override
	public IGuiColor getColor(ResourceLocation key)
	{
		if(key == null) return NULL_COLOR;
		
		IGuiColor color = null;
		
		if(getCurrentTheme() != null) color = activeTheme.getColor(key);
		if(color == null) color = defColors.get(key);
		return color == null? NULL_COLOR : color;
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public <T> GuiScreen getGui(GuiKey<T> key, T args)
    {
        if(key == null) return null;
        
        Function<T, GuiScreen> func = null;
        
        if(getCurrentTheme() != null) func = activeTheme.getGui(key);
        if(func == null) func = (Function<T, GuiScreen>)defGuis.get(key);
        
        return func == null ? null : func.apply(args);
    }
	
	@Override
	public List<IGuiTheme> getAllThemes()
	{
		return new ArrayList<IGuiTheme>(themes.values());
	}
    
    @Override
    public ResourceLocation[] getKnownTextures()
    {
        return defTextures.keySet().toArray(new ResourceLocation[0]);
    }
    
    @Override
    public ResourceLocation[] getKnownColors()
    {
        return defColors.keySet().toArray(new ResourceLocation[0]);
    }
    
    @Override
    public ResourceLocation[] getKnownLines()
    {
        return defLines.keySet().toArray(new ResourceLocation[0]);
    }
    
    @Override
    public GuiKey[] getKnownGuis()
    {
        return defGuis.keySet().toArray(new GuiKey[0]);
    }
}