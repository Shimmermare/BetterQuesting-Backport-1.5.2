package betterquesting.network;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.utils.Tuple2;
import betterquesting.backport.Consumer;
import betterquesting.backport.ResourceLocation;
import betterquesting.core.BetterQuesting;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;

public class PacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager networkManager, Packet250CustomPayload customPayload, Player player) {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        byte[] data = customPayload.data;
        if (data.length == 0) {
            BetterQuesting.logger.warning("Received empty packet");
            return;
        }

        NBTTagCompound tags;
        try {
            DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(data));
            tags = (NBTTagCompound) NBTTagCompound.readNamedTag(datainputstream);
        } catch (Exception e) {
            BetterQuesting.logger.log(Level.SEVERE, "Failed to read incoming packet", e);
            return;
        }

        try {
            if (side == Side.SERVER) {
                onServerPacket(tags, (EntityPlayerMP) player);
            } else {
                onClientPacket(tags, player);
            }
        } catch (Exception e) {
            BetterQuesting.logger.log(Level.SEVERE, "Unhandled exception from incoming packet", e);
        }
    }

    private void onServerPacket(NBTTagCompound tags, final EntityPlayerMP player) {
        if (tags == null || player.mcServer == null) {
            BetterQuesting.logger.severe("A critical NPE error occurred during while handling a BetterQuesting packet server side");
            return;
        }

        final NBTTagCompound message = PacketAssembly.INSTANCE.assemblePacket(QuestingAPI.getQuestingUUID(player), tags);

        if (message == null) {
            return;
        } else if (!message.hasKey("ID")) {
            BetterQuesting.logger.warning("Received a packet server side without an ID");
            return;
        }

        final Consumer<Tuple2<NBTTagCompound, EntityPlayerMP>> method = PacketTypeRegistry.INSTANCE.getServerHandler(
                new ResourceLocation(message.getString("ID")));
        if (method == null) {
            BetterQuesting.logger.warning("Received a packet server side with an invalid ID: " + message.getString("ID"));
            return;
        }

        // Removed scheduling task - IPacketHandler is sync here
        method.accept(new Tuple2<NBTTagCompound, EntityPlayerMP>(message, player));
    }

    private void onClientPacket(NBTTagCompound tags, Player player) {
        if (tags == null) {
            BetterQuesting.logger.severe("A critical NPE error occurred during while handling a BetterQuesting packet client side");
            return;
        }

        final NBTTagCompound message = PacketAssembly.INSTANCE.assemblePacket(null, tags);
        if (message == null) {
            return;
        } else if (!message.hasKey("ID")) {
            BetterQuesting.logger.warning("Received a packet server side without an ID");
            return;
        }

        final Consumer<NBTTagCompound> method = PacketTypeRegistry.INSTANCE.getClientHandler(
                new ResourceLocation(message.getString("ID")));
        if (method == null) {
            BetterQuesting.logger.warning("Received a packet server side with an invalid ID: " + message.getString("ID"));
            return;
        }

        // Removed scheduling task - IPacketHandler is sync here
        method.accept(message);
    }
}
