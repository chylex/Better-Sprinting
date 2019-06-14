package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

@OnlyIn(Dist.DEDICATED_SERVER)
final class ServerCommandConfig{
	public static void register(CommandDispatcher<CommandSource> dispatcher){
		LiteralArgumentBuilder<CommandSource> builder = literal("bettersprinting").requires(source -> source.hasPermissionLevel(3));
		
		builder.executes(ServerCommandConfig::execHelp);
		builder.then(literal("info").executes(ServerCommandConfig::execInfo));
		builder.then(literal("disablemod").then(argument("true|false", bool()).executes(ServerCommandConfig::execDisableMod)));
		builder.then(literal("setting").then(argument("survivalFlyBoost|runInAllDirs", word()).then(argument("true|false", bool()).executes(ServerCommandConfig::execSetting))));
		
		dispatcher.register(builder);
	}
	
	private static int execHelp(CommandContext<CommandSource> ctx){
		CommandSource source = ctx.getSource();
		sendMessage(source, TextFormatting.GREEN + "[Better Sprinting]");
		sendMessage(source, "/bettersprinting info");
		sendMessage(source, "/bettersprinting disablemod <true|false>");
		sendMessage(source, "/bettersprinting setting <survivalFlyBoost|runInAllDirs> <true|false>");
		return 0;
	}
	
	private static int execInfo(CommandContext<CommandSource> ctx){
		CommandSource source = ctx.getSource();
		sendMessageTranslated(source, "bs.command.info");
		return 0;
	}
	
	private static int execDisableMod(CommandContext<CommandSource> ctx){
		BetterSprintingMod.config.set(ServerSettings.disableClientMod, ctx.getArgument("true|false", Boolean.class));
		BetterSprintingMod.config.save();
		
		CommandSource source = ctx.getSource();
		sendMessageTranslated(source, ServerSettings.disableClientMod.get() ? "bs.command.disableMod" : "bs.command.enableMod", true);
		ServerNetwork.sendToAll(source.getServer().getPlayerList().getPlayers(), ServerNetwork.writeDisableMod(ServerSettings.disableClientMod.get()));
		return 0;
	}
	
	private static int execSetting(CommandContext<CommandSource> ctx){
		String setting = ctx.getArgument("survivalFlyBoost|runInAllDirs", String.class);
		boolean value = ctx.getArgument("true|false", Boolean.class);
		
		CommandSource source = ctx.getSource();
		
		if (setting.equalsIgnoreCase("survivalFlyBoost")){
			BetterSprintingMod.config.set(ServerSettings.enableSurvivalFlyBoost, value);
			BetterSprintingMod.config.save();
			
			sendMessageTranslated(source, ServerSettings.enableSurvivalFlyBoost.get() ? "bs.command.enableFlyBoost" : "bs.command.disableFlyBoost", true);
			ServerNetwork.sendToAll(source.getServer().getPlayerList().getPlayers(), ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost.get(), ServerSettings.enableAllDirs.get()));
		}
		else if (setting.equalsIgnoreCase("runInAllDirs")){
			BetterSprintingMod.config.set(ServerSettings.enableAllDirs, value);
			BetterSprintingMod.config.save();
			
			sendMessageTranslated(source, ServerSettings.enableAllDirs.get() ? "bs.command.enableAllDirs" : "bs.command.disableAllDirs", true);
			ServerNetwork.sendToAll(source.getServer().getPlayerList().getPlayers(), ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost.get(), ServerSettings.enableAllDirs.get()));
		}
		else{
			execHelp(ctx);
		}
		
		return 0;
	}
	
	private static void sendMessage(CommandSource source, String text){
		sendMessage(source, text, false);
	}
	
	private static void sendMessage(CommandSource source, String text, boolean log){
		source.sendFeedback(new StringTextComponent(text), log);
	}
	
	private static void sendMessageTranslated(CommandSource source, String translationKey){
		sendMessageTranslated(source, translationKey, false);
	}
	
	private static void sendMessageTranslated(CommandSource source, String translationName, boolean log){
		Entity entity = source.getEntity();
		
		if (entity instanceof PlayerEntity && ServerNetwork.hasBetterSprinting((PlayerEntity)entity)){
			source.sendFeedback(new TranslationTextComponent(translationName), log);
		}
		else{
			source.sendFeedback(new StringTextComponent(LanguageMap.getInstance().translateKey(translationName)), log);
		}
	}
}
