package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

@OnlyIn(Dist.DEDICATED_SERVER)
final class ServerCommandConfig{
	private static final String SETTING_SURVIVAL_FLY_BOOST = "survivalFlyBoost";
	private static final String SETTING_RUN_IN_ALL_DIRS = "runInAllDirs";
	
	private static final String[] SETTINGS_ALL = { SETTING_SURVIVAL_FLY_BOOST, SETTING_RUN_IN_ALL_DIRS };
	
	private static final String ARG_BOOLEAN = "true|false";
	private static final String ARG_SETTINGS = String.join("|", SETTINGS_ALL);
	
	private static final SuggestionProvider<CommandSource> SUGGEST_SETTING = (context, builder) -> ISuggestionProvider.suggest(SETTINGS_ALL, builder);
	
	public static void register(CommandDispatcher<CommandSource> dispatcher){
		LiteralArgumentBuilder<CommandSource> builder = literal("bettersprinting").requires(source -> source.hasPermissionLevel(3));
		
		builder.executes(ServerCommandConfig::execHelp);
		builder.then(literal("info").executes(ServerCommandConfig::execInfo));
		builder.then(literal("disablemod").then(argument(ARG_BOOLEAN, bool()).executes(ServerCommandConfig::execDisableMod)));
		builder.then(literal("setting").then(argument(ARG_SETTINGS, word()).suggests(SUGGEST_SETTING).then(argument(ARG_BOOLEAN, bool()).executes(ServerCommandConfig::execSetting))));
		
		dispatcher.register(builder);
	}
	
	// Executors
	
	private static int execHelp(CommandContext<CommandSource> ctx){
		CommandSource source = ctx.getSource();
		sendMessage(source, TextFormatting.GREEN + "[Better Sprinting]");
		sendMessage(source, "/bettersprinting info");
		sendMessage(source, "/bettersprinting disablemod <" + ARG_BOOLEAN + '>');
		sendMessage(source, "/bettersprinting setting <" + ARG_SETTINGS + "> <" + ARG_BOOLEAN + '>');
		return 0;
	}
	
	private static int execInfo(CommandContext<CommandSource> ctx){
		CommandSource source = ctx.getSource();
		sendMessageTranslated(source, "bs.command.info", false);
		return 0;
	}
	
	private static int execDisableMod(CommandContext<CommandSource> ctx){
		BetterSprintingMod.config.set(ServerSettings.disableClientMod, ctx.getArgument(ARG_BOOLEAN, Boolean.class));
		BetterSprintingMod.config.save();
		
		CommandSource source = ctx.getSource();
		sendMessageTranslated(source, ServerSettings.disableClientMod.get() ? "bs.command.disableMod" : "bs.command.enableMod", true);
		ServerNetwork.sendToAll(source.getServer().getPlayerList().getPlayers(), ServerNetwork.writeDisableMod(ServerSettings.disableClientMod.get()));
		return 0;
	}
	
	private static int execSetting(CommandContext<CommandSource> ctx){
		String setting = ctx.getArgument(ARG_SETTINGS, String.class);
		boolean value = ctx.getArgument(ARG_BOOLEAN, Boolean.class);
		
		CommandSource source = ctx.getSource();
		
		if (setting.equalsIgnoreCase(SETTING_SURVIVAL_FLY_BOOST)){
			BetterSprintingMod.config.set(ServerSettings.enableSurvivalFlyBoost, value);
			BetterSprintingMod.config.save();
			
			sendMessageTranslated(source, ServerSettings.enableSurvivalFlyBoost.get() ? "bs.command.enableFlyBoost" : "bs.command.disableFlyBoost", true);
			ServerNetwork.sendToAll(source.getServer().getPlayerList().getPlayers(), ServerNetwork.writeSettings(ServerSettings.enableSurvivalFlyBoost.get(), ServerSettings.enableAllDirs.get()));
		}
		else if (setting.equalsIgnoreCase(SETTING_RUN_IN_ALL_DIRS)){
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
	
	// Helpers
	
	private static void sendMessage(CommandSource source, String text){
		source.sendFeedback(new StringTextComponent(text), false);
	}
	
	private static void sendMessageTranslated(CommandSource source, String translationName, boolean log){
		Entity entity = source.getEntity();
		
		if (entity instanceof PlayerEntity && ServerNetwork.hasBetterSprinting((PlayerEntity)entity)){
			source.sendFeedback(new TranslationTextComponent(translationName), log);
		}
		else{
			source.sendFeedback(new StringTextComponent(LanguageMap.getInstance().func_230503_a_(translationName)), log);
		}
	}
}
