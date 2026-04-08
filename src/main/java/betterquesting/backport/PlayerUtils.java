package betterquesting.backport;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

import java.util.Set;

public class PlayerUtils {
    public static boolean isEffectivelyOP(EntityPlayerMP player) {
        MinecraftServer server = player.mcServer;

        if (server instanceof IntegratedServer) {
            return true;
        }

        Set<String> ops = server.getConfigurationManager().getOps();
        return ops.contains(player.username) || ops.contains(player.username.toLowerCase());
    }
}
