package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import java.util.Collections;
import java.util.List;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args){
		if (args.length == 0){
			sendMessage(sender, TextFormatting.GREEN + "[Better Sprinting]");
			sendMessage(sender, "/bettersprinting info");
			sendMessage(sender, "/bettersprinting disablemod <true|false>");
			sendMessage(sender, "/bettersprinting setting <survivalFlyBoost|runInAllDirs> <true|false>");
		}
		else if (args[0].equalsIgnoreCase("info")){
			sendMessageTranslated(sender, "bs.command.info");
		}
		else if (args[0].equalsIgnoreCase("disablemod")){
			if (isValidBool(args, 1)){
				ServerSettings.disableClientMod = getBool(args, 1);
				ServerSettings.update(BetterSprintingMod.config);
				
				sendMessageTranslated(sender, ServerSettings.disableClientMod ? "bs.command.disableMod" : "bs.command.enableMod");
				ServerNetwork.sendToAll(server.getPlayerList().getPlayers(), ServerNetwork.writeDisableMod(ServerSettings.disableClientMod));
			}
			else sendMessageTranslated(sender, "bs.command.invalidSyntax");
		}
		else if (args[0].equalsIgnoreCase("setting")){
			if (args.length <= 1 || !isValidBool(args, 2)){
				sendMessageTranslated(sender, "bs.command.invalidSyntax");
			}
			else{
				if (args[1].equalsIgnoreCase("survivalFlyBoost")){
					ServerSettings.enableSurvivalFlyBoost = getBool(args, 2);
					ServerSettings.update(BetterSprintingMod.config);
					
					sendMessageTranslated(sender, ServerSettings.enableSurvivalFlyBoost ? "bs.command.enableFlyBoost" : "bs.command.disableFlyBoost");
					ServerNetwork.sendToAll(server.getPlayerList().getPlayers(), ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost, ServerSettings.enableAllDirs));
				}
				else if (args[1].equalsIgnoreCase("runInAllDirs")){
					ServerSettings.enableAllDirs = getBool(args, 2);
					ServerSettings.update(BetterSprintingMod.config);
					
					sendMessageTranslated(sender, ServerSettings.enableAllDirs ? "bs.command.enableAllDirs" : "bs.command.disableAllDirs");
					ServerNetwork.sendToAll(server.getPlayerList().getPlayers(), ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost, ServerSettings.enableAllDirs));
				}
			}
		}
		else sendMessageTranslated(sender, "bs.command.invalidSyntax");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos){
		if (args.length == 1){
			return getListOfStringsMatchingLastWord(args, "info", "disablemod", "setting");
		}
		else if (args[0].equalsIgnoreCase("disablemod")){
			if (args.length == 2){
				return getListOfStringsMatchingLastWord(args, "true", "false");
			}
		}
		else if (args[0].equalsIgnoreCase("setting")){
			if (args.length == 2){
				return getListOfStringsMatchingLastWord(args, "survivalFlyBoost", "runInAllDirs");
			}
			else if (args.length == 3){
				return getListOfStringsMatchingLastWord(args, "true", "false");
			}
		}
		
		return Collections.emptyList();
	}
	
	private void sendMessage(ICommandSender sender, String text){
		sender.sendMessage(new TextComponentString(text));
	}
	
	private void sendMessageTranslated(ICommandSender sender, String translationName){
		if (sender instanceof EntityPlayer && ServerNetwork.hasBetterSprinting((EntityPlayer)sender)){
			sender.sendMessage(new TextComponentTranslation(translationName));
		}
		else{
			sender.sendMessage(new TextComponentString(I18n.translateToLocal(translationName)));
		}
	}
	
	private boolean isValidBool(String[] args, int index){
		return index < args.length && (args[index].equalsIgnoreCase("true") || args[index].equalsIgnoreCase("false"));
	}
	
	private boolean getBool(String[] args, int index){
		return args[index].equalsIgnoreCase("true");
	}
}
