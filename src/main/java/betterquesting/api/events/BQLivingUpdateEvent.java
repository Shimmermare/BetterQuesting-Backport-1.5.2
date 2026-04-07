package betterquesting.api.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Event;

public class BQLivingUpdateEvent extends Event
{
    public final EntityLiving entityLiving;
    public final Entity entity;
    
    public BQLivingUpdateEvent(EntityPlayerMP player)
    {
        this.entityLiving = player;
        this.entity = player;
    }
}
