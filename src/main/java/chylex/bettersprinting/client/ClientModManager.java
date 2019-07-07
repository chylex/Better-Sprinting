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
	
	static boolean svSurvivalFlyBoost = false, svRunInAllDirs = false, svDisableMod = false;
	
	static void onDisconnectedFromServer(){
		svSurvivalFlyBoost = svRunInAllDirs = svDisableMod = false;
	}
	
	private static boolean notInGame(){
		return mc.player == null || mc.world == null;
	}
	
	public static boolean canManuallyEnableMod(){
		return notInGame() || mc.isSingleplayer();
	}
	
	public static boolean isModDisabled(){
		return ClientSettings.disableMod.get() || svDisableMod;
	}
	
	public enum Feature{
		FLY_BOOST{
			@Override protected boolean checkEnableCondition(){
				return mc.player.abilities.isFlying && (mc.player.isCreative() || mc.player.isSpectator() || svSurvivalFlyBoost);
			}
		},
		
		FLY_ON_GROUND{
			@Override protected boolean checkEnableCondition(){
				return mc.player.abilities.isFlying && ClientSettings.flyOnGround.get() && mc.player.isCreative();
			}
		},
		
		RUN_IN_ALL_DIRS{
			@Override protected boolean checkEnableCondition(){
				return ClientSettings.enableAllDirs.get();
			}
			
			@Override public boolean isAvailable(){
				return super.isAvailable() && (notInGame() || mc.isSingleplayer() || svRunInAllDirs);
			}
		};
		
		protected abstract boolean checkEnableCondition();
		
		public final boolean isEnabled(){
			return isAvailable() && checkEnableCondition();
		}
		
		public boolean isAvailable(){
			return !isModDisabled();
		}
	}
}
