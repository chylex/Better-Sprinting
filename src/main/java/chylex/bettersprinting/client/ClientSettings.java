package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.input.KeyBindingInfo;
import chylex.bettersprinting.client.input.SprintKeyMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientSettings{
	public static int keyCodeSprintHold = Keyboard.KEY_LCONTROL;
	public static int keyCodeSprintToggle = Keyboard.KEY_G;
	public static int keyCodeSneakToggle = Keyboard.KEY_Y;
	public static int keyCodeOptionsMenu = Keyboard.KEY_O;
	
	public static KeyModifier keyModSprintHold = KeyModifier.NONE;
	public static KeyModifier keyModSprintToggle = KeyModifier.NONE;
	public static KeyModifier keyModSneakToggle = KeyModifier.NONE;
	public static KeyModifier keyModOptionsMenu = KeyModifier.NONE;
	
	public static KeyBindingInfo keyInfoSprintHold;
	public static KeyBindingInfo keyInfoSprintToggle;
	public static KeyBindingInfo keyInfoSneakToggle;
	public static KeyBindingInfo keyInfoOptionsMenu;
	
	public static SprintKeyMode sprintKeyMode = SprintKeyMode.TAP;
	
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
		
		keyModSprintHold = KeyModifier.valueOf(config.get("keyModSprintHold", keyModSprintHold.name()).setShowInGui(false).getString());
		keyModSprintToggle = KeyModifier.valueOf(config.get("keyModSprintToggle", keyModSprintToggle.name()).setShowInGui(false).getString());
		keyModSneakToggle = KeyModifier.valueOf(config.get("keyModSneakToggle", keyModSneakToggle.name()).setShowInGui(false).getString());
		keyModOptionsMenu = KeyModifier.valueOf(config.get("keyModOptionsMenu", keyModOptionsMenu.name()).setShowInGui(false).getString());
		
		sprintKeyMode = SprintKeyMode.valueOf(config.get("sprintKeyMode", sprintKeyMode.name()).setShowInGui(false).getString());
		
		flySpeedBoost = MathHelper.clamp(config.get("flySpeedBoost", flySpeedBoost).setShowInGui(false).getInt(), 0, 7);
		flyOnGround = config.get("flyOnGround", flyOnGround).setShowInGui(false).getBoolean();
		enableDoubleTap = config.get("enableDoubleTap", enableDoubleTap).setShowInGui(false).getBoolean();
		enableAllDirs = config.get("enableAllDirs", enableAllDirs).setShowInGui(false).getBoolean();
		disableMod = config.get("disableMod", disableMod).setShowInGui(false).getBoolean();
		
		enableUpdateNotifications = config.get("enableUpdateNotifications", enableUpdateNotifications, I18n.format("bs.config.notifications")).getBoolean();
		enableBuildCheck = config.get("enableBuildCheck", enableBuildCheck, I18n.format("bs.config.buildCheck")).getBoolean();
		
		keyInfoSprintHold   = new KeyBindingInfo(v -> keyCodeSprintHold = v,   () -> keyCodeSprintHold,   v -> keyModSprintHold = v,   () -> keyModSprintHold);
		keyInfoSprintToggle = new KeyBindingInfo(v -> keyCodeSprintToggle = v, () -> keyCodeSprintToggle, v -> keyModSprintToggle = v, () -> keyModSprintToggle);
		keyInfoSneakToggle  = new KeyBindingInfo(v -> keyCodeSneakToggle = v,  () -> keyCodeSneakToggle,  v -> keyModSneakToggle = v,  () -> keyModSneakToggle);
		keyInfoOptionsMenu  = new KeyBindingInfo(v -> keyCodeOptionsMenu = v,  () -> keyCodeOptionsMenu,  v -> keyModOptionsMenu = v,  () -> keyModOptionsMenu);
		
		config.update();
	}
	
	public static void update(BetterSprintingConfig config){
		config.setCategory("client");
		
		config.set("keySprintHold", keyCodeSprintHold);
		config.set("keySprintToggle", keyCodeSprintToggle);
		config.set("keySneakToggle", keyCodeSneakToggle);
		config.set("keyOptionsMenu", keyCodeOptionsMenu);
		
		config.set("keyModSprintHold", keyModSprintHold.name());
		config.set("keyModSprintToggle", keyModSprintToggle.name());
		config.set("keyModSneakToggle", keyModSneakToggle.name());
		config.set("keyModOptionsMenu", keyModOptionsMenu.name());
		
		config.set("sprintKeyMode", sprintKeyMode.name());
		
		config.set("flySpeedBoost", flySpeedBoost);
		config.set("flyOnGround", flyOnGround);
		config.set("enableDoubleTap", enableDoubleTap);
		config.set("enableAllDirs", enableAllDirs);
		config.set("disableMod", disableMod);
		
		config.set("enableUpdateNotifications", enableUpdateNotifications);
		config.set("enableBuildCheck", enableBuildCheck);
		
		config.update();
	}
	
	public static void firstTimeSetup(){
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		
		keyInfoSprintHold.readFrom(settings.keyBindSprint);
		
		KeyModifier sprintModifier = getVanillaKeyModifier(settings.keyBindSprint);
		KeyModifier sneakModifier = getVanillaKeyModifier(settings.keyBindSneak);
		
		if (sprintModifier != KeyModifier.NONE){
			keyInfoSprintToggle.set(sprintModifier, Keyboard.KEY_G);
		}
		
		if (sneakModifier != KeyModifier.NONE){
			keyInfoSneakToggle.set(sneakModifier, Keyboard.KEY_G);
		}
		
		update(BetterSprintingMod.config);
	}
	
	private static KeyModifier getVanillaKeyModifier(KeyBinding binding){
		if (binding.getKeyModifier() != KeyModifier.NONE){
			return KeyModifier.NONE;
		}
		
		switch(binding.getKeyCode()){
			case Keyboard.KEY_LCONTROL: return KeyModifier.CONTROL;
			case Keyboard.KEY_LSHIFT: return KeyModifier.SHIFT;
			case Keyboard.KEY_LMENU: return KeyModifier.ALT;
			default: return KeyModifier.NONE;
		}
	}
}
