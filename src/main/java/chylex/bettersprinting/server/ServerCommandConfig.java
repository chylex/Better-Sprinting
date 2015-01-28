package chylex.bettersprinting.server;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ServerCommandConfig extends CommandBase{ // TODO
	@Override
	public String getName(){
		return null;
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "";
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException{
		
	}
}
