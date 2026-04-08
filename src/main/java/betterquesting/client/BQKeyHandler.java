package betterquesting.client;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.api2.client.gui.GuiScreenTest;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNone;
import betterquesting.api2.client.gui.themes.presets.PresetGUIs;
import betterquesting.client.gui2.GuiHome;
import betterquesting.client.themes.ThemeRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import java.util.EnumSet;

public class BQKeyHandler extends KeyBindingRegistry.KeyHandler
{
    public BQKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
    {
        super(keyBindings, repeatings);
    }

    @Override
    public String getLabel()
    {
        return "BetterQuestingKeys";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (tickEnd && BQ_Keybindings.openQuests.isPressed())
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.thePlayer.isSneaking() && mc.thePlayer.getCommandSenderName().equalsIgnoreCase("Funwayguy"))
            {
                mc.displayGuiScreen(new GuiScreenTest(mc.currentScreen));
            } else
            {
                if(BQ_Settings.useBookmark && GuiHome.bookmark != null)
                {
                    mc.displayGuiScreen(GuiHome.bookmark);
                } else
                {
                    mc.displayGuiScreen(ThemeRegistry.INSTANCE.getGui(PresetGUIs.HOME, GArgsNone.NONE));
                }
            }
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }
}