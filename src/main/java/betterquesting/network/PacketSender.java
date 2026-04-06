package betterquesting.network;

import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api2.utils.BQThreadedIO;
import betterquesting.core.BetterQuesting;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
                Packet250CustomPayload[] fragmentPackets = new Packet250CustomPayload[fragments.size()];
                for (int i = 0; i < fragmentPackets.length; i++) {
                    fragmentPackets[i] = partToPacket(fragments.get(i));
                }
                for(EntityPlayerMP p : players)
                {
                    for(Packet250CustomPayload packet : fragmentPackets)
                    {
                        PacketDispatcher.sendPacketToPlayer(packet, (Player) p);
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
                    PacketDispatcher.sendPacketToAllPlayers(partToPacket(p));
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
                    PacketDispatcher.sendPacketToServer(partToPacket(p));
                }
            }
        });
	}
	
	@Override
	public void sendToAround(final QuestingPacket payload, final double x, final double y, final double z,
                             final double range, final int dimensionId)
	{
		payload.getPayload().setString("ID", payload.getHandler().toString());
		
		BQThreadedIO.INSTANCE.enqueue(new Runnable() {
            @Override
            public void run() {
                for(NBTTagCompound p : PacketAssembly.INSTANCE.splitPacket(payload.getPayload()))
                {
                    PacketDispatcher.sendPacketToAllAround(x, y, z, range, dimensionId, partToPacket(p));
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
                    PacketDispatcher.sendPacketToAllInDimension(partToPacket(p), dimension);
                }
            }
        });
	}

    private Packet250CustomPayload partToPacket(NBTTagCompound p) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);
        // No compression - payload is already compressed
        NBTTagCompound.writeNamedTag(p, new DataOutputStream(outBytes));

        return new Packet250CustomPayload(
                BetterQuesting.CHANNEL,
                outBytes.toByteArray()
        );
    }
}
