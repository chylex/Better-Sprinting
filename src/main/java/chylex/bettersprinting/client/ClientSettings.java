package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.client.compatibility.OldClientConfig;

@SideOnly(Side.CLIENT)
public class ClientSettings{
	public static int keyCodeSprintHold = 29;
	public static int keyCodeSprintToggle = 34;
	public static int keyCodeSneakToggle = 21;
	public static int keyCodeOptionsMenu = 24;
	
	public static byte flySpeedBoost = 3;
	public static boolean enableDoubleTap = false;
	public static boolean enableAllDirs = false;
	public static boolean disableMod = false;
	
	public static boolean enableUpdateNotifications = true;
	public static boolean enableBuildCheck = true;
	
	public static void reload(BetterSprintingConfig config){
		config.setCategory("client");
		OldClientConfig.loadAndDeleteOldConfig();
		
		keyCodeSprintHold = config.getInt("keySprintHold",keyCodeSprintHold,"").setShowInGui(false).getInt();
		keyCodeSprintToggle = config.getInt("keySprintToggle",keyCodeSprintToggle,"").setShowInGui(false).getInt();
		keyCodeSneakToggle = config.getInt("keySneakToggle",keyCodeSneakToggle,"").setShowInGui(false).getInt();
		keyCodeOptionsMenu = config.getInt("keyOptionsMenu",keyCodeOptionsMenu,"").setShowInGui(false).getInt();
		
		flySpeedBoost = (byte)config.getInt("flySpeedBoost",flySpeedBoost,"").setShowInGui(false).getInt();
		enableDoubleTap = config.getBool("enableDoubleTap",enableDoubleTap,"").setShowInGui(false).getBoolean();
		enableAllDirs = config.getBool("enableAllDirs",enableAllDirs,"").setShowInGui(false).getBoolean();
		disableMod = config.getBool("disableMod",disableMod,"").setShowInGui(false).getBoolean();
		
		enableUpdateNotifications = config.getBool("enableUpdateNotifications",enableUpdateNotifications,"").getBoolean();
		enableBuildCheck = config.getBool("enableBuildCheck",enableBuildCheck,"").getBoolean();
		
		ClientModManager.keyBindSprintHold.setKeyCode(keyCodeSprintHold);
		ClientModManager.keyBindSprintToggle.setKeyCode(keyCodeSprintToggle);
		ClientModManager.keyBindSneakToggle.setKeyCode(keyCodeSneakToggle);
		ClientModManager.keyBindOptionsMenu.setKeyCode(keyCodeOptionsMenu);
		
		config.update();
		
		Minecraft.getMinecraft().gameSettings.keyBindSprint.setKeyCode(ClientSettings.keyCodeSprintHold);
	}
	
	public static void update(BetterSprintingConfig config){
		config.setCategory("client");
		
		config.setInt("keySprintHold",keyCodeSprintHold);
		config.setInt("keySprintToggle",keyCodeSprintToggle);
		config.setInt("keySneakToggle",keyCodeSneakToggle);
		config.setInt("keyOptionsMenu",keyCodeOptionsMenu);
		
		config.setInt("flySpeedBoost",flySpeedBoost);
		config.setBool("enableDoubleTap",enableDoubleTap);
		config.setBool("enableAllDirs",enableAllDirs);
		config.setBool("disableMod",disableMod);
		
		config.setBool("enableUpdateNotifications",enableUpdateNotifications);
		config.setBool("enableBuildCheck",enableBuildCheck);
		
		config.update();
		
		Minecraft.getMinecraft().gameSettings.keyBindSprint.setKeyCode(ClientSettings.keyCodeSprintHold);
	}
}
