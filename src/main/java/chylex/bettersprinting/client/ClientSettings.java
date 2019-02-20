package chylex.bettersprinting.client;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;
import java.util.Arrays;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientSettings{
	public static final IntValue keyCodeSprintHold;
	public static final IntValue keyCodeSprintToggle;
	public static final IntValue keyCodeSneakToggle;
	public static final IntValue keyCodeOptionsMenu;
	
	public static ConfigValue<String> keyModSprintHold;
	public static ConfigValue<String> keyModSprintToggle;
	public static ConfigValue<String> keyModSneakToggle;
	public static ConfigValue<String> keyModOptionsMenu;
	
	public static ConfigValue<String> keyTypeSprintHold;
	public static ConfigValue<String> keyTypeSprintToggle;
	public static ConfigValue<String> keyTypeSneakToggle;
	public static ConfigValue<String> keyTypeOptionsMenu;
	
	public static final IntValue flySpeedBoost;
	public static final BooleanValue enableDoubleTap;
	public static final BooleanValue enableAllDirs;
	public static final BooleanValue disableMod;
	
	public static final BooleanValue enableUpdateNotifications;
	public static final BooleanValue enableBuildCheck;
	
	private static final ForgeConfigSpec configSpec;
	
	static void register(ModLoadingContext ctx){
		ctx.registerConfig(ModConfig.Type.CLIENT, configSpec);
	}
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		builder.push("client");
		
		keyCodeSprintHold   = builder.defineInRange("keyCodeSprintHold", GLFW.GLFW_KEY_LEFT_CONTROL, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeSprintToggle = builder.defineInRange("keyCodeSprintToggle", GLFW.GLFW_KEY_G, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeSneakToggle  = builder.defineInRange("keyCodeSneakToggle", GLFW.GLFW_KEY_Z, Integer.MIN_VALUE, Integer.MAX_VALUE);
		keyCodeOptionsMenu  = builder.defineInRange("keyCodeOptionsMenu", GLFW.GLFW_KEY_O, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		keyModSprintHold   = defineEnum(builder, "keyModSprintHold", KeyModifier.NONE);
		keyModSprintToggle = defineEnum(builder, "keyModSprintToggle", KeyModifier.NONE);
		keyModSneakToggle  = defineEnum(builder, "keyModSneakToggle", KeyModifier.NONE);
		keyModOptionsMenu  = defineEnum(builder, "keyModOptionsMenu", KeyModifier.NONE);
		
		keyTypeSprintHold   = defineEnum(builder, "keyTypeSprintHold", InputMappings.Type.KEYSYM);
		keyTypeSprintToggle = defineEnum(builder, "keyTypeSprintToggle", InputMappings.Type.KEYSYM);
		keyTypeSneakToggle  = defineEnum(builder, "keyTypeSneakToggle", InputMappings.Type.KEYSYM);
		keyTypeOptionsMenu  = defineEnum(builder, "keyTypeOptionsMenu", InputMappings.Type.KEYSYM);
		
		flySpeedBoost   = builder.defineInRange("flySpeedBoost", 3, 0, 7);
		enableDoubleTap = builder.define("enableDoubleTap", false);
		enableAllDirs   = builder.define("enableAllDirs", false);
		disableMod      = builder.define("disableMod", false);
		
		builder.pop();
		builder.push("updates");
		
		enableUpdateNotifications = builder.translation("bs.config.notifications").define("enableUpdateNotifications", true);
		enableBuildCheck          = builder.translation("bs.config.buildCheck").define("enableBuildCheck", true);
		
		builder.pop();
		
		configSpec = builder.build();
	}
	
	// TODO wait for toml lib to fix stuff
	private static ConfigValue<String> defineEnum(ForgeConfigSpec.Builder builder, String path, Enum<?> defaultValue){
		return builder.defineInList(path, defaultValue.name(), Arrays.stream(defaultValue.getDeclaringClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
	}
	
	private static KeyModifier getModifier(ConfigValue<String> value){
		return KeyModifier.valueFromString(value.get());
	}
	
	private static InputMappings.Type getType(ConfigValue<String> value){
		try{
			return InputMappings.Type.valueOf(value.get());
		}catch(Exception e){
			return InputMappings.Type.KEYSYM;
		}
	}
	
	public static void updateKeyBindings(){
		ClientModManager.keyBindSprintHold.setKeyModifierAndCode(getModifier(keyModSprintHold), getType(keyTypeSprintHold).getOrMakeInput(keyCodeSprintHold.get()));
		ClientModManager.keyBindSprintToggle.setKeyModifierAndCode(getModifier(keyModSprintToggle), getType(keyTypeSprintToggle).getOrMakeInput(keyCodeSprintToggle.get()));
		ClientModManager.keyBindSneakToggle.setKeyModifierAndCode(getModifier(keyModSneakToggle), getType(keyTypeSneakToggle).getOrMakeInput(keyCodeSneakToggle.get()));
		ClientModManager.keyBindOptionsMenu.setKeyModifierAndCode(getModifier(keyModOptionsMenu), getType(keyTypeOptionsMenu).getOrMakeInput(keyCodeOptionsMenu.get()));
		KeyBinding.resetKeyBindingArrayAndHash();
	}
}
