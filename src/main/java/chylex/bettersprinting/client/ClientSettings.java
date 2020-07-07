package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.client.input.KeyBindingInfo;
import chylex.bettersprinting.client.input.SprintKeyMode;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public final class ClientSettings{
	private static final IntValue keyCodeSprintHold;
	private static final IntValue keyCodeSprintToggle;
	private static final IntValue keyCodeSneakToggle;
	private static final IntValue keyCodeOptionsMenu;
	
	private static final EnumValue<KeyModifier> keyModSprintHold;
	private static final EnumValue<KeyModifier> keyModSprintToggle;
	private static final EnumValue<KeyModifier> keyModSneakToggle;
	private static final EnumValue<KeyModifier> keyModOptionsMenu;
	
	private static final EnumValue<InputMappings.Type> keyTypeSprintHold;
	private static final EnumValue<InputMappings.Type> keyTypeSprintToggle;
	private static final EnumValue<InputMappings.Type> keyTypeSneakToggle;
	private static final EnumValue<InputMappings.Type> keyTypeOptionsMenu;
	
	public static final KeyBindingInfo keyInfoSprintHold;
	public static final KeyBindingInfo keyInfoSprintToggle;
	public static final KeyBindingInfo keyInfoSneakToggle;
	public static final KeyBindingInfo keyInfoOptionsMenu;
	
	public static final EnumValue<SprintKeyMode> sprintKeyMode;
	
	public static final IntValue flySpeedBoost;
	public static final BooleanValue flyOnGround;
	public static final BooleanValue enableDoubleTap;
	public static final BooleanValue enableAllDirs;
	public static final BooleanValue disableMod;
	
	private static final ForgeConfigSpec configSpec;
	
	public static ForgeConfigSpec getSpec(){
		return configSpec;
	}
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		builder.push("client");
		
		keyCodeSprintHold   = builder.defineInRange("keyCodeSprintHold", GLFW.GLFW_KEY_LEFT_CONTROL, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeSprintToggle = builder.defineInRange("keyCodeSprintToggle", GLFW.GLFW_KEY_G, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeSneakToggle  = builder.defineInRange("keyCodeSneakToggle", GLFW.GLFW_KEY_Z, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeOptionsMenu  = builder.defineInRange("keyCodeOptionsMenu", GLFW.GLFW_KEY_O, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		keyModSprintHold   = builder.defineEnum("keyModSprintHold", () -> KeyModifier.NONE, obj -> true, KeyModifier.class);
		keyModSprintToggle = builder.defineEnum("keyModSprintToggle", () -> KeyModifier.NONE, obj -> true, KeyModifier.class);
		keyModSneakToggle  = builder.defineEnum("keyModSneakToggle", () -> KeyModifier.NONE, obj -> true, KeyModifier.class);
		keyModOptionsMenu  = builder.defineEnum("keyModOptionsMenu", () -> KeyModifier.NONE, obj -> true, KeyModifier.class);
		
		keyTypeSprintHold   = builder.defineEnum("keyTypeSprintHold", InputMappings.Type.KEYSYM);
		keyTypeSprintToggle = builder.defineEnum("keyTypeSprintToggle", InputMappings.Type.KEYSYM);
		keyTypeSneakToggle  = builder.defineEnum("keyTypeSneakToggle", InputMappings.Type.KEYSYM);
		keyTypeOptionsMenu  = builder.defineEnum("keyTypeOptionsMenu", InputMappings.Type.KEYSYM);
		
		sprintKeyMode = builder.defineEnum("sprintKeyMode", SprintKeyMode.TAP);
		
		flySpeedBoost   = builder.defineInRange("flySpeedBoost", 3, 0, 7);
		flyOnGround     = builder.define("flyOnGround", false);
		enableDoubleTap = builder.define("enableDoubleTap", false);
		enableAllDirs   = builder.define("enableAllDirs", false);
		disableMod      = builder.define("disableMod", false);
		
		builder.pop();
		
		configSpec = builder.build();
		
		keyInfoSprintHold = new KeyBindingInfo(keyCodeSprintHold, keyModSprintHold, keyTypeSprintHold);
		keyInfoSprintToggle = new KeyBindingInfo(keyCodeSprintToggle, keyModSprintToggle, keyTypeSprintToggle);
		keyInfoSneakToggle = new KeyBindingInfo(keyCodeSneakToggle, keyModSneakToggle, keyTypeSneakToggle);
		keyInfoOptionsMenu = new KeyBindingInfo(keyCodeOptionsMenu, keyModOptionsMenu, keyTypeOptionsMenu);
	}
	
	public static void firstTimeSetup(GameSettings settings){
		keyInfoSprintHold.readFrom(settings.keyBindSprint);
		
		KeyModifier sprintModifier = getVanillaKeyModifier(settings.keyBindSprint);
		KeyModifier sneakModifier = getVanillaKeyModifier(settings.keyBindSneak);
		
		if (sprintModifier != KeyModifier.NONE){
			keyInfoSprintToggle.set(sprintModifier, InputMappings.Type.KEYSYM.getOrMakeInput(GLFW.GLFW_KEY_G));
		}
		
		if (sneakModifier != KeyModifier.NONE){
			keyInfoSneakToggle.set(sneakModifier, InputMappings.Type.KEYSYM.getOrMakeInput(GLFW.GLFW_KEY_G));
		}
		
		BetterSprintingConfig.save();
	}
	
	private static KeyModifier getVanillaKeyModifier(KeyBinding binding){
		if (binding.getKeyModifier() != KeyModifier.NONE || binding.getKey().getType() != InputMappings.Type.KEYSYM){
			return KeyModifier.NONE;
		}
		
		switch(binding.getKey().getKeyCode()){
			case GLFW.GLFW_KEY_LEFT_CONTROL: return KeyModifier.CONTROL;
			case GLFW.GLFW_KEY_LEFT_SHIFT: return KeyModifier.SHIFT;
			case GLFW.GLFW_KEY_LEFT_ALT: return KeyModifier.ALT;
			default: return KeyModifier.NONE;
		}
	}
}
