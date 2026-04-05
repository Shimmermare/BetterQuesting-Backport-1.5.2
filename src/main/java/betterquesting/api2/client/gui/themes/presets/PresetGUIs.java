package betterquesting.api2.client.gui.themes.presets;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.themes.GuiKey;
import betterquesting.api2.client.gui.themes.gui_args.GArgsCallback;
import betterquesting.api2.client.gui.themes.gui_args.GArgsFileBrowser;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNBT;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNone;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.liquids.LiquidStack;

public class PresetGUIs
{
    public static final GuiKey<GArgsNone> HOME = new GuiKey<GArgsNone>(new ResourceLocation("betterquesting", "home"));
    
    public static final GuiKey<GArgsNBT> EDIT_NBT = new GuiKey<GArgsNBT>(new ResourceLocation("betterquesting", "edit_nbt"));
    public static final GuiKey<GArgsCallback<BigItemStack>> EDIT_ITEM = new GuiKey<GArgsCallback<BigItemStack>>(new ResourceLocation("betterquesting", "edit_item"));
    public static final GuiKey<GArgsCallback<LiquidStack>> EDIT_FLUID = new GuiKey<LiquidStack>(new ResourceLocation("betterquesting", "edit_fluid"));
    public static final GuiKey<GArgsCallback<Entity>> EDIT_ENTITY = new GuiKey<Entity>(new ResourceLocation("betterquesting", "edit_entity"));
    
    public static final GuiKey<GArgsCallback<String>> EDIT_TEXT = new GuiKey<String>(new ResourceLocation("betterquesting", "edit_text"));
    public static final GuiKey<GArgsFileBrowser> FILE_EXPLORE = new GuiKey<GArgsFileBrowser>(new ResourceLocation("betterquesting", "file_explore"));
}
