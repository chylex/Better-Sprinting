package chylex.bettersprinting.client;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import chylex.bettersprinting.BetterSprintingConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSettings{
	public static int keyCodeSprintHold = 29;
	public static int keyCodeSprintToggle = 34;
	public static int keyCodeSneakToggle = 21;
	public static int keyCodeOptionsMenu = 24;

	public static int flySpeedBoost = 3;
	public static boolean flyOnGround = false;
	public static boolean enableDoubleTap = false;
	public static boolean enableAllDirs = false;
	public static boolean disableMod = false;
	
	public static boolean enableUpdateNotifications = true;
	public static boolean enableBuildCheck = true;
	
	public static void reload(BetterSprintingConfig config){
		config.setCategory("client");
		
		keyCodeSprintHold = config.get("keySprintHold", keyCodeSprintHold).setShowInGui(false).getInt();
		keyCodeSprintToggle = config.get("keySprintToggle", keyCodeSprintToggle).setShowInGui(false).getInt();
		keyCodeSneakToggle = config.get("keySneakToggle", keyCodeSneakToggle).setShowInGui(false).getInt();
		keyCodeOptionsMenu = config.get("keyOptionsMenu", keyCodeOptionsMenu).setShowInGui(false).getInt();
		
		flySpeedBoost = MathHelper.clamp_int(config.get("flySpeedBoost", flySpeedBoost).setShowInGui(false).getInt(), 0, 7);
		flyOnGround = config.get("flyOnGround", flyOnGround).setShowInGui(false).getBoolean();
		enableDoubleTap = config.get("enableDoubleTap", enableDoubleTap).setShowInGui(false).getBoolean();
		enableAllDirs = config.get("enableAllDirs", enableAllDirs).setShowInGui(false).getBoolean();
		disableMod = config.get("disableMod", disableMod).setShowInGui(false).getBoolean();
		
		enableUpdateNotifications = config.get("enableUpdateNotifications", enableUpdateNotifications, I18n.format("bs.config.notifications")).getBoolean();
		enableBuildCheck = config.get("enableBuildCheck", enableBuildCheck, I18n.format("bs.config.buildCheck")).getBoolean();
		
		config.update();
	}
	
	public static void update(BetterSprintingConfig config){
		config.setCategory("client");
		
		config.set("keySprintHold", keyCodeSprintHold);
		config.set("keySprintToggle", keyCodeSprintToggle);
		config.set("keySneakToggle", keyCodeSneakToggle);
		config.set("keyOptionsMenu", keyCodeOptionsMenu);
		
		config.set("flySpeedBoost", flySpeedBoost);
		config.set("flyOnGround", flyOnGround);
		config.set("enableDoubleTap", enableDoubleTap);
		config.set("enableAllDirs", enableAllDirs);
		config.set("disableMod", disableMod);
		
		config.set("enableUpdateNotifications", enableUpdateNotifications);
		config.set("enableBuildCheck", enableBuildCheck);
		
		config.update();
	}
}
