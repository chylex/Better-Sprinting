package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingConfig;

public class ServerSettings{
	public static boolean enableSurvivalFlyBoost = false;
	public static boolean enableAllDirs = false;
	public static boolean disableClientMod = false;
	
	public static void reload(BetterSprintingConfig config){
		config.setCategory("server");
		enableSurvivalFlyBoost = config.getBool("enableSurvivalFlyBoost",enableSurvivalFlyBoost,"").getBoolean();
		enableAllDirs = config.getBool("enableAllDirs",enableAllDirs,"").getBoolean();
		disableClientMod = config.getBool("disableClientMod",disableClientMod,"").getBoolean();
	}
}
