package betterquesting.api2.client.gui.panels.content;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.ValueFuncIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PanelPlayerPortrait implements IGuiPanel
{
	private final IGuiRect transform;
	private boolean enabled = true;
	
	private final EntityPlayer player;
	
	private final IValueIO<Float> basePitch;
	private final IValueIO<Float> baseYaw;
	private IValueIO<Float> pitchDriver;
	private IValueIO<Float> yawDriver;
	
	private float zDepth = 100F;
	
	public PanelPlayerPortrait(IGuiRect rect, UUID playerID, String username)
	{
		this(rect, new EntityPlayerPreview(Minecraft.getMinecraft().theWorld, username));
	}
	
	public PanelPlayerPortrait(IGuiRect rect, EntityPlayer player)
	{
		this.transform = rect;
		this.player = new EntityPlayerPreview(player.worldObj, player.username);
		this.player.limbSwing = 0F;
		this.player.limbYaw = 0F;
		this.player.rotationYawHead = 0F;

        // Keep original skin URL even if it doesn't work so fixer mods can handle this natively
        this.player.skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + this.player.username + ".png";
		
		this.basePitch = new ValueFuncIO<Float>(new Callable<Float>() {
            @Override
            public Float call() {
                return 15F;
            }
        });
		this.pitchDriver = basePitch;
		
		this.baseYaw = new ValueFuncIO<Float>(new Callable<Float>() {
            @Override
            public Float call() {
                return -30F;
            }
        });
		this.yawDriver = baseYaw;
	}
	
	public PanelPlayerPortrait setRotationFixed(float pitch, float yaw)
	{
		this.pitchDriver = basePitch;
		this.yawDriver = baseYaw;
		basePitch.writeValue(pitch);
		baseYaw.writeValue(yaw);
		return this;
	}
	
	public PanelPlayerPortrait setRotationDriven(IValueIO<Float> pitch, IValueIO<Float> yaw)
	{
		this.pitchDriver = pitch == null? basePitch : pitch;
		this.yawDriver = yaw == null? baseYaw : yaw;
		return this;
	}
	
	public PanelPlayerPortrait setDepth(float z)
	{
		this.zDepth = z;
		return this;
	}
	
	@Override
	public void initPanel()
	{
	}
	
	@Override
	public void setEnabled(boolean state)
	{
		this.enabled = state;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	@Override
	public IGuiRect getTransform()
	{
		return transform;
	}
	
	@Override
	public void drawPanel(int mx, int my, float partialTick)
	{
		IGuiRect bounds = this.getTransform();
        GL11.glPushMatrix();
		RenderUtils.startScissor(new GuiRectangle(bounds));
        
        GL11.glColor4f(1F, 1F, 1F, 1F);
		int scale = Math.min(bounds.getWidth(), bounds.getHeight());
		RenderUtils.RenderEntity(bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2 + (int)(scale*1.5F), zDepth, scale, yawDriver.readValue(), pitchDriver.readValue(), player);
		
		RenderUtils.endScissor();
		GL11.glPopMatrix();
	}
	
	@Override
	public boolean onMouseClick(int mx, int my, int click)
	{
		return false;
	}
	
	@Override
	public boolean onMouseRelease(int mx, int my, int click)
	{
		return false;
	}
	
	@Override
	public boolean onMouseScroll(int mx, int my, int scroll)
	{
		return false;
	}
	
	@Override
	public boolean onKeyTyped(char c, int keycode)
	{
		return false;
	}
	
	@Override
	public List<String> getTooltip(int mx, int my)
	{
		return null;
	}

    private static class EntityPlayerPreview extends EntityOtherPlayerMP
    {
        public EntityPlayerPreview(World worldIn, String gameProfileIn)
        {
            super(worldIn, gameProfileIn);
        }
    }
}
