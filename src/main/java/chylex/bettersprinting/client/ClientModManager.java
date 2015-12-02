package chylex.bettersprinting.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientModManager{
	public static final String chatPrefix = EnumChatFormatting.GREEN+"[Better Sprinting]"+EnumChatFormatting.RESET+" ";
	
	public static final KeyBinding keyBindSprintHold = new KeyBinding("bs.sprint.hold",29,"key.categories.movement");
    public static final KeyBinding keyBindSprintToggle = new KeyBinding("bs.sprint.toggle",34,"key.categories.movement");
    public static final KeyBinding keyBindSneakToggle = new KeyBinding("bs.sneak.toggle",21,"key.categories.movement");
    public static final KeyBinding keyBindOptionsMenu = new KeyBinding("bs.menu",24,"key.categories.movement");
    
	static boolean svSurvivalFlyingBoost = false, svRunInAllDirs = false, svDisableMod = false;
    
    public static boolean inMenu(Minecraft mc){
    	return mc.thePlayer == null || mc.theWorld == null;
    }
    
    public static boolean canRunInAllDirs(Minecraft mc){
    	return !isModDisabled() && (inMenu(mc) || mc.isSingleplayer() || svRunInAllDirs);
    }
    
    public static boolean canBoostFlying(Minecraft mc){
    	return !isModDisabled() && (inMenu(mc) || mc.isSingleplayer() || mc.thePlayer.capabilities.isCreativeMode || svSurvivalFlyingBoost);
    }
    
    public static boolean isModDisabled(){
    	return ClientSettings.disableMod || svDisableMod;
    }
    
    public static boolean isModDisabledByServer(){
    	return svDisableMod;
    }
}
