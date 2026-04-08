package betterquesting.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.core.BetterQuesting;
import betterquesting.network.handlers.NetBulkSync;
import betterquesting.storage.LifeDatabase;
import betterquesting.storage.NameCache;
import betterquesting.storage.QuestSettings;
import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;

public class BQPlayerTracker implements IPlayerTracker {
    @Override
    public void onPlayerLogin(EntityPlayer player) {
        if(player.worldObj.isRemote || MinecraftServer.getServer() == null || !(player instanceof EntityPlayerMP)) return;

        EntityPlayerMP mpPlayer = (EntityPlayerMP) player;

        if(BetterQuesting.proxy.isClient() && !MinecraftServer.getServer().isDedicatedServer() && MinecraftServer.getServer().getServerOwner().equals(player.username))
        {
            NameCache.INSTANCE.updateName(mpPlayer);
            return;
        }

        NetBulkSync.sendReset(mpPlayer, true, true);
    }

    @Override
    public void onPlayerLogout(EntityPlayer entityPlayer) {

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer entityPlayer) {

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player) {
        if(QuestSettings.INSTANCE.getProperty(NativeProps.HARDCORE) && player instanceof EntityPlayerMP && !((EntityPlayerMP)player).playerConqueredTheEnd)
        {
            EntityPlayerMP mpPlayer = (EntityPlayerMP) player;

            int lives = LifeDatabase.INSTANCE.getLives(QuestingAPI.getQuestingUUID(mpPlayer));

            if(lives <= 0)
            {
                MinecraftServer server = MinecraftServer.getServer();

                if(server == null)
                {
                    return;
                }

                if (server.isSinglePlayer() && mpPlayer.getCommandSenderName().equals(server.getServerOwner()))
                {
                    mpPlayer.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    server.deleteWorldAndStopServer();
                }
                else
                {
                    BanEntry banEntry = new BanEntry(mpPlayer.username);
                    banEntry.setBannedBy("Death in Hardcore");
                    banEntry.setBanReason("(You just lost the game)");
                    server.getConfigurationManager().getBannedPlayers().put(banEntry);
                    mpPlayer.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                }
            } else
            {
                if(lives == 1)
                {
                    mpPlayer.sendChatToPlayer("This is your last life!");
                } else
                {
                    mpPlayer.sendChatToPlayer(lives + " lives remaining!");
                }
            }
        }
    }
}
