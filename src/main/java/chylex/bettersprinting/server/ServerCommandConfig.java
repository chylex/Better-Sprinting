package chylex.bettersprinting.server;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.server.compatibility.OldNotificationPacketReceiver;
import chylex.bettersprinting.system.PacketPipeline;

public class ServerCommandConfig extends CommandBase{
	@Override
	public String getCommandName(){
		return "bettersprinting";
	}

	@Override
	public String getCommandUsage(ICommandSender sender){
		return "/bettersprinting [...]";
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 3;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException{
		if (args.length == 0){
			sendMessage(sender,EnumChatFormatting.GREEN+"[Better Sprinting]");
			sendMessage(sender,"/bettersprinting info");
			sendMessage(sender,"/bettersprinting disablemod <true|false>");
			sendMessage(sender,"/bettersprinting setting <survivalFlyBoost|runInAllDirs> <true|false>");
		}
		else if (args[0].equalsIgnoreCase("info")){
			sendMessageTranslated(sender,"bs.command.info");
		}
		else if (args[0].equalsIgnoreCase("disablemod")){
			if (isValidBool(args,1)){
				ServerSettings.disableClientMod = getBool(args,1);
				ServerSettings.update(BetterSprintingMod.config);
				sendMessageTranslated(sender,ServerSettings.disableClientMod ? "bs.command.disableMod" : "bs.command.enableMod");
				PacketPipeline.sendToAll(ServerNetwork.writeDisableMod(ServerSettings.disableClientMod));
				if (ServerSettings.disableClientMod)OldNotificationPacketReceiver.kickOldModUsers();
			}
			else sendMessageTranslated(sender,"bs.command.invalidSyntax");
		}
		else if (args[0].equalsIgnoreCase("setting")){
			if (args.length <= 1 || !isValidBool(args,2)){
				sendMessageTranslated(sender,"bs.command.invalidSyntax");
			}
			else{
				if (args[1].equalsIgnoreCase("survivalFlyBoost")){
					ServerSettings.enableSurvivalFlyBoost = getBool(args,2);
					ServerSettings.update(BetterSprintingMod.config);
					sendMessageTranslated(sender,ServerSettings.enableSurvivalFlyBoost ? "bs.command.enableFlyBoost" : "bs.command.disableFlyBoost");
					PacketPipeline.sendToAll(ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost,ServerSettings.enableAllDirs));
				}
				else if (args[1].equalsIgnoreCase("runInAllDirs")){
					ServerSettings.enableAllDirs = getBool(args,2);
					ServerSettings.update(BetterSprintingMod.config);
					sendMessageTranslated(sender,ServerSettings.enableAllDirs ? "bs.command.enableAllDirs" : "bs.command.disableAllDirs");
					PacketPipeline.sendToAll(ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost,ServerSettings.enableAllDirs));
				}
			}
		}
		else sendMessageTranslated(sender,"bs.command.invalidSyntax");
	}
	
	private void sendMessage(ICommandSender sender, String text){
		sender.addChatMessage(new ChatComponentText(text));
	}
	
	private void sendMessageTranslated(ICommandSender sender, String translationName){
		if (sender instanceof EntityPlayer && !ServerNetwork.hasBetterSprinting((EntityPlayer)sender)){
			sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(translationName)));
		}
		else{
			sender.addChatMessage(new ChatComponentTranslation(translationName));
		}
	}
	
	private boolean isValidBool(String[] args, int index){
		if (index >= args.length)return false;
		return args[index].equalsIgnoreCase("true") || args[index].equalsIgnoreCase("false"); 
	}
	
	private boolean getBool(String[] args, int index){
		return args[index].equalsIgnoreCase("true");
	}
}
