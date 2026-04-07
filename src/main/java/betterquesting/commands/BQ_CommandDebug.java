package betterquesting.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class BQ_CommandDebug extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "bq_debug";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "TO BE USED IN DEV ONLY";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
    }

    @Override
    public int compareTo(Object o) {
        return compareTo((ICommand)o);
    }
}
