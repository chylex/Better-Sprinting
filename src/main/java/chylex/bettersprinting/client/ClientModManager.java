package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientModManager{
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static final String chatPrefix = TextFormatting.GREEN+"[Better Sprinting]"+TextFormatting.RESET+" ";
	public static final String categoryName = "key.categories.bettersprinting.hidden";
	
	public static boolean showDisableWarningWhenPossible;
	
	public static final KeyBinding keyBindSprintHold = mc.gameSettings.keyBindSprint;
	public static final KeyBinding keyBindSprintToggle = new KeyBinding("bs.sprint.toggle", 34, categoryName);
	public static final KeyBinding keyBindSneakToggle = new KeyBinding("bs.sneak.toggle", 21, categoryName);
	public static final KeyBinding keyBindOptionsMenu = new KeyBinding("bs.menu", 24, categoryName);
	
	public static final KeyBinding[] keyBindings = new KeyBinding[]{
		keyBindSprintHold, keyBindSprintToggle, keyBindSneakToggle, keyBindOptionsMenu
	};
	
	static{
		keyBindSprintHold.keyCategory = categoryName;
		
		for(KeyBinding binding:keyBindings){
			binding.setKeyConflictContext(KeyConflictContext.IN_GAME);
		}
	}
	
	static boolean svSurvivalFlyingBoost = false, svRunInAllDirs = false, svDisableMod = false;
	
	public static boolean inMenu(){
		return mc.player == null || mc.world == null;
	}
	
	public static boolean canRunInAllDirs(){
		return !isModDisabled() && (inMenu() || mc.isSingleplayer() || svRunInAllDirs);
	}
	
	public static boolean canBoostFlying(){
		return !isModDisabled() && (inMenu() || mc.isSingleplayer() || mc.player.capabilities.isCreativeMode || svSurvivalFlyingBoost);
	}
	
	public static boolean isModDisabled(){
		return ClientSettings.disableMod || svDisableMod;
	}
	
	public static boolean isModDisabledByServer(){
		return svDisableMod;
	}
}
