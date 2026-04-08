package betterquesting.backport;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT)
public class OpenGLHelperBP {
    private static boolean openGL14;

    public static void init() {
        openGL14 = GLContext.getCapabilities().OpenGL14;
    }

    public static void glBlendFunc(int sFactor, int dFactor, int sFactorAlpha, int dFactorAlpha) {
        if (openGL14) {
            GL14.glBlendFuncSeparate(sFactor, dFactor, sFactorAlpha, dFactorAlpha);
        } else {
            GL11.glBlendFunc(sFactor, dFactor);
        }
    }
}

