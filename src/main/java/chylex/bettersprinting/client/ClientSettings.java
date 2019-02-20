package chylex.bettersprinting.client;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Type;
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

@OnlyIn(Dist.CLIENT)
public class ClientSettings{
	public static final IntValue keyCodeSprintHold;
	public static final IntValue keyCodeSprintToggle;
	public static final IntValue keyCodeSneakToggle;
	public static final IntValue keyCodeOptionsMenu;
	
	public static ConfigValue<KeyModifier> keyModSprintHold;
	public static ConfigValue<KeyModifier> keyModSprintToggle;
	public static ConfigValue<KeyModifier> keyModSneakToggle;
	public static ConfigValue<KeyModifier> keyModOptionsMenu;
	
	public static InputMappings.Type keyTypeSprintHold = Type.KEYSYM;
	public static InputMappings.Type keyTypeSprintToggle = Type.KEYSYM;
	public static InputMappings.Type keyTypeSneakToggle = Type.KEYSYM;
	public static InputMappings.Type keyTypeOptionsMenu = Type.KEYSYM;
	
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
		
		/* TODO fucked
		keyModSprintHold   = builder.defineEnum("keyModSprintHold", KeyModifier.NONE);
		keyModSprintToggle = builder.defineEnum("keyModSprintToggle", KeyModifier.NONE);
		keyModSneakToggle  = builder.defineEnum("keyModSneakToggle", KeyModifier.NONE);
		keyModOptionsMenu  = builder.defineEnum("keyModOptionsMenu", KeyModifier.NONE);*/
		
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
	
	public static void updateKeyBindings(){
		ClientModManager.keyBindSprintHold.setKeyModifierAndCode(/* TODO keyModSprintHold.get()*/ KeyModifier.NONE, keyTypeSprintHold.getOrMakeInput(keyCodeSprintHold.get()));
		ClientModManager.keyBindSprintToggle.setKeyModifierAndCode(/* TODO keyModSprintToggle.get()*/ KeyModifier.NONE, keyTypeSprintToggle.getOrMakeInput(keyCodeSprintToggle.get()));
		ClientModManager.keyBindSneakToggle.setKeyModifierAndCode(/* TODO keyModSneakToggle.get()*/ KeyModifier.NONE, keyTypeSneakToggle.getOrMakeInput(keyCodeSneakToggle.get()));
		ClientModManager.keyBindOptionsMenu.setKeyModifierAndCode(/* TODO keyModOptionsMenu.get()*/ KeyModifier.NONE, keyTypeOptionsMenu.getOrMakeInput(keyCodeOptionsMenu.get()));
		KeyBinding.resetKeyBindingArrayAndHash();
	}
}
