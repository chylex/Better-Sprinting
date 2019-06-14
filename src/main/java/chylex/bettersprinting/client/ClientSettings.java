package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
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
	
	public static final EnumValue<KeyModifier> keyModSprintHold;
	public static final EnumValue<KeyModifier> keyModSprintToggle;
	public static final EnumValue<KeyModifier> keyModSneakToggle;
	public static final EnumValue<KeyModifier> keyModOptionsMenu;
	
	public static final EnumValue<InputMappings.Type> keyTypeSprintHold;
	public static final EnumValue<InputMappings.Type> keyTypeSprintToggle;
	public static final EnumValue<InputMappings.Type> keyTypeSneakToggle;
	public static final EnumValue<InputMappings.Type> keyTypeOptionsMenu;
	
	public static final IntValue flySpeedBoost;
	public static final BooleanValue flyOnGround;
	public static final BooleanValue enableDoubleTap;
	public static final BooleanValue enableAllDirs;
	public static final BooleanValue disableMod;
	
	public static final BooleanValue enableUpdateNotifications;
	public static final BooleanValue enableBuildCheck;
	
	private static final ForgeConfigSpec configSpec;
	
	static void register(ModLoadingContext ctx){
		BetterSprintingConfig.register(ctx, ModConfig.Type.CLIENT, configSpec, "client");
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
		
		flySpeedBoost   = builder.defineInRange("flySpeedBoost", 3, 0, 7);
		flyOnGround     = builder.define("flyOnGround", false);
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
		ClientModManager.keyBindSprintHold.setKeyModifierAndCode(keyModSprintHold.get(), keyTypeSprintHold.get().getOrMakeInput(keyCodeSprintHold.get()));
		ClientModManager.keyBindSprintToggle.setKeyModifierAndCode(keyModSprintToggle.get(), keyTypeSprintToggle.get().getOrMakeInput(keyCodeSprintToggle.get()));
		ClientModManager.keyBindSneakToggle.setKeyModifierAndCode(keyModSneakToggle.get(), keyTypeSneakToggle.get().getOrMakeInput(keyCodeSneakToggle.get()));
		ClientModManager.keyBindOptionsMenu.setKeyModifierAndCode(keyModOptionsMenu.get(), keyTypeOptionsMenu.get().getOrMakeInput(keyCodeOptionsMenu.get()));
		KeyBinding.resetKeyBindingArrayAndHash();
	}
}
