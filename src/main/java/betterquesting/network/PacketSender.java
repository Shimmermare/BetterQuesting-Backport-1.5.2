package betterquesting.network;

import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api2.utils.BQThreadedIO;
import betterquesting.core.BetterQuesting;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class PacketSender implements IPacketSender
{
	public static final PacketSender INSTANCE = new PacketSender();
	
	@Override
	public void sendToPlayers(final QuestingPacket payload, final EntityPlayerMP... players)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
        
        BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                List<NBTTagCompound> fragments = PacketAssembly.INSTANCE.splitPacket(payload.getPayload());
                for(EntityPlayerMP p : players)
                {
                    for(NBTTagCompound tag : fragments)
                    {
                        BetterQuesting.instance.network.sendTo(new PacketQuesting(tag), p);
                    }
                }
            }
        });
	}
	
	@Override
	public void sendToAll(final QuestingPacket payload)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
		
		BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload()))
                {
                    BetterQuesting.instance.network.sendToAll(new PacketQuesting(p));
                }
            }
        });
	}
	
	@Override
	public void sendToServer(final QuestingPacket payload)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
		
		BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload()))
                {
                    BetterQuesting.instance.network.sendToServer(new PacketQuesting(p));
                }
            }
        });
	}
	
	@Override
	public void sendToAround(final QuestingPacket payload, final TargetPoint point)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
		
		BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload()))
                {
                    BetterQuesting.instance.network.sendToAllAround(new PacketQuesting(p), point);
                }
            }
        });
	}
	
	@Override
	public void sendToDimension(final QuestingPacket payload, final int dimension)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
		
		BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload()))
                {
                    BetterQuesting.instance.network.sendToDimension(new PacketQuesting(p), dimension);
                }
            }
        });
	}
}
