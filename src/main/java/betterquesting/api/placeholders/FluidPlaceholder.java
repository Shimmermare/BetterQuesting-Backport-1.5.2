package betterquesting.api.placeholders;

import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;

public class FluidPlaceholder extends BlockStationary {
    public FluidPlaceholder(int i) {
        super(i, Material.water);
        setUnlocalizedName("betterquesting.fluid_placeholder");
    }
}
