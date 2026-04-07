package betterquesting.commands.user;

import betterquesting.api.properties.NativeProps;
import betterquesting.commands.QuestCommandBase;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetSettingSync;
import betterquesting.storage.QuestSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class QuestCommandSPHardcore extends QuestCommandBase
{
	@Override
	public String getCommand()
	{
		return "hardcore";
	}
	
	@Override
	public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args)
	{
		if(!server.isSinglePlayer() || !server.getServerOwner().equalsIgnoreCase(sender.getCommandSenderName()))
		{
			sender.sendChatToPlayer(EnumChatFormatting.RED + StatCollector.translateToLocal("commands.generic.permission"));
			return;
		}
		
		QuestSettings.INSTANCE.setProperty(NativeProps.HARDCORE, true);
        SaveLoadHandler.INSTANCE.saveDatabases();
        
		sender.sendChatToPlayer(StatCollector.translateToLocalFormatted("betterquesting.cmd.hardcore",
                StatCollector.translateToLocal("options.on")));
		NetSettingSync.sendSync(null);
	}
}
