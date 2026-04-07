package betterquesting.api2.client.gui;

import javax.annotation.Nullable;

public class SceneController
{
    private static IScene curScene = null;
    
    @Nullable
    public static IScene getActiveScene()
    {
        return curScene;
    }
    
    public static void setActiveScene(@Nullable IScene scene)
    {
        curScene = scene;
    }

    // FIXME restore behavior
    //@SubscribeEvent
    //@SideOnly(Side.CLIENT)
    //public void onGuiOpened(GuiOpenEvent event)
    //{
    //    if(event.gui instanceof IScene)
    //    {
    //        // TODO: Review the following
    //        // Does this need to be cleared if the GUI isn't compatible?
    //        // Would this interfere with an overlay canvas?
    //        curScene = (IScene)event.gui;
    //    }
    //}
}
