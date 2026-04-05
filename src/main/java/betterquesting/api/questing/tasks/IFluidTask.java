package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import net.minecraftforge.liquids.LiquidStack;

import java.util.UUID;

public interface IFluidTask extends ITask {
    boolean canAcceptFluid(UUID owner, DBEntry<IQuest> quest, LiquidStack fluid);

    LiquidStack submitFluid(UUID owner, DBEntry<IQuest> quest, LiquidStack fluid);
}
