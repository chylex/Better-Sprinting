package chylex.bettersprinting.server;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.server.compatibility.OldNotificationPacketReceiver;
import chylex.bettersprinting.system.PacketPipeline;

public class ServerCommandConfig extends CommandBase{
	@Override
	public String getName(){
		return "bettersprinting";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/bettersprinting [...]";
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 3;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException{
		if (args.length == 0){
			sendMessage(sender,EnumChatFormatting.GREEN+"[Better Sprinting]");
			sendMessage(sender,"/bettersprinting info");
			sendMessage(sender,"/bettersprinting disablemod <true|false>");
			sendMessage(sender,"/bettersprinting setting <survivalFlyBoost|runInAllDirs> <true|false>");
		}
		else if (args[0].equalsIgnoreCase("info")){
			sendMessage(sender,"You can use the command to either disable/enable the mod, or change specific settings of the mod. These will persist after restarting the server, and will also immediately affect players that are already on the server.");
		}
		else if (args[0].equalsIgnoreCase("disablemod")){
			if (isValidBool(args,1)){
				ServerSettings.disableClientMod = getBool(args,1);
				ServerSettings.update(BetterSprintingMod.config);
				sendMessage(sender,ServerSettings.disableClientMod ? "Better Sprinting will be automatically disabled when a user joins." : "Better Sprinting is now allowed on the server.");
				PacketPipeline.sendToAll(ServerNetwork.writeDisableMod(ServerSettings.disableClientMod));
				if (ServerSettings.disableClientMod)OldNotificationPacketReceiver.kickOldModUsers();
			}
			else sendMessage(sender,"Invalid syntax, do /bettersprinting for list of commands.");
		}
		else if (args[0].equalsIgnoreCase("setting")){
			if (args.length <= 1 || !isValidBool(args,2)){
				sendMessage(sender,"Invalid syntax, do /bettersprinting for list of commands.");
			}
			else{
				if (args[1].equalsIgnoreCase("survivalFlyBoost")){
					ServerSettings.enableSurvivalFlyBoost = getBool(args,2);
					ServerSettings.update(BetterSprintingMod.config);
					sendMessage(sender,"Fly boost is now "+(ServerSettings.enableSurvivalFlyBoost ? "enabled" : "disabled")+" when the player is in survival mode.");
					PacketPipeline.sendToAll(ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost,ServerSettings.enableAllDirs));
				}
				else if (args[1].equalsIgnoreCase("runInAllDirs")){
					ServerSettings.enableAllDirs = getBool(args,2);
					ServerSettings.update(BetterSprintingMod.config);
					sendMessage(sender,"Sprinting in all directions is now "+(ServerSettings.enableAllDirs ? "enabled." : "disabled."));
					PacketPipeline.sendToAll(ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost,ServerSettings.enableAllDirs));
				}
			}
		}
		else sendMessage(sender,"Invalid syntax, do /bettersprinting for list of commands.");
	}
	
	private void sendMessage(ICommandSender sender, String text){
		sender.addChatMessage(new ChatComponentText(text));
	}
	
	private boolean isValidBool(String[] args, int index){
		if (index >= args.length)return false;
		return args[index].equalsIgnoreCase("true") || args[index].equalsIgnoreCase("false"); 
	}
	
	private boolean getBool(String[] args, int index){
		return args[index].equalsIgnoreCase("true");
	}
}
