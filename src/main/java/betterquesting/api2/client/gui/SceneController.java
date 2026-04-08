package betterquesting.api2.client.gui;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SceneController implements ITickHandler {
    private static IScene curScene = null;

    @Nullable
    public static IScene getActiveScene() {
        return curScene;
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        updateScene();
    }

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {
        updateScene();
    }

    private void updateScene() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof IScene) {
            // TODO: Review the following
            // Does this need to be cleared if the GUI isn't compatible?
            // Would this interfere with an overlay canvas?
            curScene = (IScene) gui;
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return null;
    }

    @Override
    public String getLabel() {
        return "";
    }
}
