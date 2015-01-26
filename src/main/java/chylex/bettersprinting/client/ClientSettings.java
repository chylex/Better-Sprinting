package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.client.compatibility.OldClientConfig;

public class ClientSettings{
	public static int keyCodeSprintHold = 29;
	public static int keyCodeSprintToggle = 34;
	public static int keyCodeSneakToggle = 21;
	public static int keyCodeSprintMenu = 24;
	
	public static byte flySpeedBoost = 3;
	public static boolean enableDoubleTap = false;
	public static boolean enableAllDirs = false;
	public static boolean disableMod = false;
	
	public static boolean enableUpdateNotifications = true;
	public static boolean enableBuildCheck = true;
	
	public static boolean showedSneakWarning = false;
	
	public static void load(BetterSprintingConfig config){
		config.setCategory("client");
		OldClientConfig.loadAndDeleteOldConfig();
		
		// TODO load
		
		ClientModManager.keyBindSprintHold.setKeyCode(keyCodeSprintHold);
		ClientModManager.keyBindSprintToggle.setKeyCode(keyCodeSprintToggle);
		ClientModManager.keyBindSneakToggle.setKeyCode(keyCodeSneakToggle);
		ClientModManager.keyBindSprintMenu.setKeyCode(keyCodeSprintMenu);
	}
}
