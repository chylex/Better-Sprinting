package chylex.bettersprinting.server;
import chylex.bettersprinting.BetterSprintingConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerSettings{
	public static boolean enableSurvivalFlyBoost = false;
	public static boolean enableAllDirs = false;
	public static boolean disableClientMod = false;
	
	public static void reload(BetterSprintingConfig config){
		config.setCategory("server");
		enableSurvivalFlyBoost = config.get("enableSurvivalFlyBoost", enableSurvivalFlyBoost).getBoolean();
		enableAllDirs = config.get("enableAllDirs", enableAllDirs).getBoolean();
		disableClientMod = config.get("disableClientMod", disableClientMod).getBoolean();
		config.update();
	}
	
	public static void update(BetterSprintingConfig config){
		config.setCategory("server");
		config.set("enableSurvivalFlyBoost", enableSurvivalFlyBoost);
		config.set("enableAllDirs", enableAllDirs);
		config.set("disableClientMod", disableClientMod);
		config.update();
	}
}
