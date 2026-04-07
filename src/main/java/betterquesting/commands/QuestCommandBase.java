package betterquesting.commands;

import betterquesting.api.api.QuestingAPI;
import betterquesting.storage.NameCache;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class QuestCommandBase
{
	public abstract String getCommand();
	
	public String getUsageSuffix()
	{
		return "";
	}
	
	/**
	 * Are the passed arguments valid?<br>
	 * NOTE: Argument 1 is always the returned value of getCommand()
	 */
	public boolean validArgs(String[] args)
	{
		return args.length == 1;
	}
	
	public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args)
	{
		return new ArrayList<String>();
	}
	
	public abstract void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args);
	
	public final WrongUsageException getException(CommandBase command)
	{
		String message = command.getCommandName() + " " + getCommand();
		
		if(getUsageSuffix().length() > 0)
		{
			message += " " + getUsageSuffix();
		}
		
		return new WrongUsageException(message);
	}
	
	/**
	 * Attempts to find the players ID from the given name or convert it to a UUID if valid
	 */
	public UUID findPlayerID(MinecraftServer server, ICommandSender sender, String name)
	{
		UUID playerID;
		
		EntityPlayerMP player = null;
		
		try
		{
			player = getPlayer(sender, name);
		} catch(Exception ignored){}
		
		if(player == null)
		{
			if(name.startsWith("@"))
			{
				return null;
			}
			
			try
			{
				playerID = UUID.fromString(name);
			} catch(Exception e)
			{
				playerID = NameCache.INSTANCE.getUUID(name);
			}
		} else
		{
			playerID = QuestingAPI.getQuestingUUID(player);
		}
		
		return playerID;
	}
	
	public boolean isArgUsername(String[] args, int index)
	{
		return false;
	}

    public static EntityPlayerMP getPlayer(ICommandSender sender, String username) {
        EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, username);

        if (player != null) {
            return player;
        } else {
            player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);

            if (player == null) {
                throw new PlayerNotFoundException();
            } else {
                return player;
            }
        }
    }
}
