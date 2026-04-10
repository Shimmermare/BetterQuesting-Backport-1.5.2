package betterquesting.api.placeholders;

import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class FluidPlaceholder extends BlockStationary {
    public FluidPlaceholder(int i) {
        super(i, Material.water);
        setUnlocalizedName("betterquesting.fluid_placeholder");
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        this.theIcon = new Icon[]{par1IconRegister.registerIcon("betterquesting:fluid_placeholder")};
    }
}
