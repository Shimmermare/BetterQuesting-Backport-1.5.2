package betterquesting.api2.client.gui.resources.textures;

import net.minecraft.client.renderer.Tessellator;

public class GuiTextureUtils {
    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
        float mult = 1 / 256F;
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, zLevel, u * mult, (v + height) * mult);
        t.addVertexWithUV(x + width, y + height, zLevel, (u + width) * mult, (v + height) * mult);
        t.addVertexWithUV(x + width, y, zLevel, (u + width) * mult, v * mult);
        t.addVertexWithUV(x, y, zLevel, u * mult, v * mult);
        t.draw();
    }
}
