package chylex.bettersprinting.client;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingConfig;

@SideOnly(Side.CLIENT)
public class ClientSettings{
	public static int keyCodeSprintHold = 29;
	public static int keyCodeSprintToggle = 34;
	public static int keyCodeSneakToggle = 21;
	public static int keyCodeOptionsMenu = 24;
	
	public static KeyModifier keyModSprintHold = KeyModifier.NONE;
	public static KeyModifier keyModSprintToggle = KeyModifier.NONE;
	public static KeyModifier keyModSneakToggle = KeyModifier.NONE;
	public static KeyModifier keyModOptionsMenu = KeyModifier.NONE;
	
	public static byte flySpeedBoost = 3;
	public static boolean enableDoubleTap = false;
	public static boolean enableAllDirs = false;
	public static boolean disableMod = false;
	
	public static boolean enableUpdateNotifications = true;
	public static boolean enableBuildCheck = true;
	
	public static void reload(BetterSprintingConfig config){
		config.setCategory("client");
		
		keyCodeSprintHold = config.getInt("keySprintHold", keyCodeSprintHold).setShowInGui(false).getInt();
		keyCodeSprintToggle = config.getInt("keySprintToggle", keyCodeSprintToggle).setShowInGui(false).getInt();
		keyCodeSneakToggle = config.getInt("keySneakToggle", keyCodeSneakToggle).setShowInGui(false).getInt();
		keyCodeOptionsMenu = config.getInt("keyOptionsMenu", keyCodeOptionsMenu).setShowInGui(false).getInt();
		
		keyModSprintHold = KeyModifier.valueOf(config.getString("keyModSprintHold", keyModSprintHold.name()).setShowInGui(false).getString());
		keyModSprintToggle = KeyModifier.valueOf(config.getString("keyModSprintToggle", keyModSprintToggle.name()).setShowInGui(false).getString());
		keyModSneakToggle = KeyModifier.valueOf(config.getString("keyModSneakToggle", keyModSneakToggle.name()).setShowInGui(false).getString());
		keyModOptionsMenu = KeyModifier.valueOf(config.getString("keyModOptionsMenu", keyModOptionsMenu.name()).setShowInGui(false).getString());
		
		flySpeedBoost = (byte)config.getInt("flySpeedBoost", flySpeedBoost).setShowInGui(false).getInt();
		enableDoubleTap = config.getBool("enableDoubleTap", enableDoubleTap).setShowInGui(false).getBoolean();
		enableAllDirs = config.getBool("enableAllDirs", enableAllDirs).setShowInGui(false).getBoolean();
		disableMod = config.getBool("disableMod", disableMod).setShowInGui(false).getBoolean();
		
		enableUpdateNotifications = config.getBool("enableUpdateNotifications", enableUpdateNotifications, I18n.format("bs.config.notifications")).getBoolean();
		enableBuildCheck = config.getBool("enableBuildCheck", enableBuildCheck, I18n.format("bs.config.buildCheck")).getBoolean();
		
		ClientModManager.keyBindSprintHold.setKeyModifierAndCode(keyModSprintHold, keyCodeSprintHold);
		ClientModManager.keyBindSprintToggle.setKeyModifierAndCode(keyModSprintToggle, keyCodeSprintToggle);
		ClientModManager.keyBindSneakToggle.setKeyModifierAndCode(keyModSneakToggle, keyCodeSneakToggle);
		ClientModManager.keyBindOptionsMenu.setKeyModifierAndCode(keyModOptionsMenu, keyCodeOptionsMenu);
		
		config.update();
	}
	
	public static void update(BetterSprintingConfig config){
		config.setCategory("client");
		
		config.setInt("keySprintHold", keyCodeSprintHold);
		config.setInt("keySprintToggle", keyCodeSprintToggle);
		config.setInt("keySneakToggle", keyCodeSneakToggle);
		config.setInt("keyOptionsMenu", keyCodeOptionsMenu);
		
		config.setString("keyModSprintHold", keyModSprintHold.name());
		config.setString("keyModSprintToggle", keyModSprintToggle.name());
		config.setString("keyModSneakToggle", keyModSneakToggle.name());
		config.setString("keyModOptionsMenu", keyModOptionsMenu.name());
		
		config.setInt("flySpeedBoost", flySpeedBoost);
		config.setBool("enableDoubleTap", enableDoubleTap);
		config.setBool("enableAllDirs", enableAllDirs);
		config.setBool("disableMod", disableMod);
		
		config.setBool("enableUpdateNotifications", enableUpdateNotifications);
		config.setBool("enableBuildCheck", enableBuildCheck);
		
		config.update();
	}
}
