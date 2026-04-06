package betterquesting.api.network;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IPacketSender
{
    // Server to Client
    void sendToPlayers(QuestingPacket payload, EntityPlayerMP... players);
	void sendToAll(QuestingPacket payload);
	
	// Client to Server
	void sendToServer(QuestingPacket payload);
	
	// Misc.
	void sendToAround(QuestingPacket payload, final double x, final double y, final double z,
                      final double range, final int dimensionId);
	void sendToDimension(QuestingPacket payload, int dimension);
}
