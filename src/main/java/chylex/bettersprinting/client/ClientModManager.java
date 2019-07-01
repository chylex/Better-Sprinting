package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public final class ClientModManager{
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static final String chatPrefix = TextFormatting.GREEN + "[Better Sprinting]" + TextFormatting.RESET + " ";
	public static final String categoryName = "key.categories.bettersprinting.hidden";
	
	public static final KeyBinding keyBindSprintHold = mc.gameSettings.keyBindSprint;
	public static final KeyBinding keyBindSprintToggle = new KeyBinding("bs.sprint.toggle", -1, categoryName);
	public static final KeyBinding keyBindSneakToggle = new KeyBinding("bs.sneak.toggle", -1, categoryName);
	public static final KeyBinding keyBindOptionsMenu = new KeyBinding("bs.menu", -1, categoryName);
	
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
	
	static void onDisconnectedFromServer(){
		svSurvivalFlyingBoost = svRunInAllDirs = svDisableMod = false;
	}
	
	private static boolean notInGame(){
		return mc.player == null || mc.world == null;
	}
	
	public static boolean canRunInAllDirs(){
		return !isModDisabled() && (notInGame() || mc.isSingleplayer() || svRunInAllDirs);
	}
	
	public static boolean canBoostFlying(){
		return !isModDisabled() && (notInGame() || mc.player.isCreative() || mc.player.isSpectator() || svSurvivalFlyingBoost);
	}
	
	public static boolean canFlyOnGround(){
		return !isModDisabled() && (notInGame() || mc.player.isCreative());
	}
	
	public static boolean canEnableMod(){
		return notInGame() || mc.isSingleplayer();
	}
	
	public static boolean isModDisabled(){
		return ClientSettings.disableMod.get() || svDisableMod;
	}
	
	public static boolean isModDisabledByServer(){
		return svDisableMod;
	}
}
