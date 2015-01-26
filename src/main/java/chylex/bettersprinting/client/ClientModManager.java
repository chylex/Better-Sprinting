package chylex.bettersprinting.client;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import chylex.bettersprinting.system.Log;

public final class ClientModManager{
	public static KeyBinding keyBindSprint = new KeyBinding("Sprint (hold)",29,"key.categories.movement");
    public static KeyBinding keyBindSprintToggle = new KeyBinding("Sprint (toggle)",34,"key.categories.movement");
    public static KeyBinding keyBindSneakToggle = new KeyBinding("Sneak (toggle)",21,"key.categories.movement");
    public static KeyBinding keyBindSprintMenu = new KeyBinding("Sprint menu",24,"key.categories.movement");
    public static int flyingBoost = 3;
    public static boolean allowDoubleTap = false;
    public static boolean allowAllDirs = false;
    public static boolean disableModFunctionality = false;
	public static boolean showedToggleSneakWarning = false;
    public static boolean enableUpdateNotifications = true;
    
	public static boolean svFlyingBoost = false, svRunInAllDirs = false;
	public static boolean fromBs = false;
	public static boolean held = false;
    public static int stoptime = 0;
    
    public static boolean canRunInAllDirs(Minecraft mc){
    	return disableModFunctionality?false:(mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||svRunInAllDirs);
    }
    
    public static boolean canBoostFlying(Minecraft mc){
    	return disableModFunctionality?false:(mc.thePlayer==null&&mc.theWorld==null)||(mc.isSingleplayer()||mc.thePlayer.capabilities.isCreativeMode||svFlyingBoost);
    }
    
    private static int nbtInt(NBTTagCompound tag, String key, int def){ return tag.hasKey(key)?tag.getInteger(key):def; }
    private static boolean nbtBool(NBTTagCompound tag, String key, boolean def){ return tag.hasKey(key)?tag.getBoolean(key):def; }
    
    public static void loadSprint(Minecraft mc){
    	File file = new File(mc.mcDataDir,"sprint.nbt");
    	if (!file.exists())return;
    	
    	try{
    		NBTTagCompound tag=CompressedStreamTools.readCompressed(new FileInputStream(file)).getCompoundTag("Data");
    		if (tag==null)return;
    		
    		keyBindSprintMenu.setKeyCode(nbtInt(tag,"keyMenu",24));
    		keyBindSprint.setKeyCode(nbtInt(tag,"keySprint",29));
    		keyBindSprintToggle.setKeyCode(nbtInt(tag,"keySprintToggle",34));
    		keyBindSneakToggle.setKeyCode(nbtInt(tag,"keySneakToggle",21));
    		flyingBoost=nbtInt(tag,"flyBoost",3);
    		allowDoubleTap=nbtBool(tag,"doubleTap",false);
    		allowAllDirs=nbtBool(tag,"allDirs",false);
    		disableModFunctionality=nbtBool(tag,"disableMod",false);
    		showedToggleSneakWarning=nbtBool(tag,"showedWarn",false);
    		enableUpdateNotifications=nbtBool(tag,"updateNotifications",true);
    	}catch(Exception e){
    		e.printStackTrace();
    		Log.error("Error loading Better Sprinting settings!");
    	}
    }
    
    public static void saveSprint(Minecraft mc){
    	NBTTagCompound tag=new NBTTagCompound();
    	tag.setInteger("keyMenu",keyBindSprintMenu.getKeyCode());
    	tag.setInteger("keySprint",keyBindSprint.getKeyCode());
    	tag.setInteger("keySprintToggle",keyBindSprintToggle.getKeyCode());
    	tag.setInteger("keySneakToggle",keyBindSneakToggle.getKeyCode());
    	tag.setInteger("flyBoost",flyingBoost);
    	tag.setBoolean("doubleTap",allowDoubleTap);
    	tag.setBoolean("allDirs",allowAllDirs);
    	tag.setBoolean("disableMod",disableModFunctionality);
    	tag.setBoolean("showedWarn",showedToggleSneakWarning);
    	tag.setBoolean("updateNotifications",enableUpdateNotifications);
    	
    	NBTTagCompound fintag=new NBTTagCompound();
    	fintag.setTag("Data",tag);
    	
    	try{
    		CompressedStreamTools.writeCompressed(fintag,new FileOutputStream(new File(mc.mcDataDir,"sprint.nbt")));
    	}catch(Exception e){
    		e.printStackTrace();
    		Log.error("Error saving Better Sprinting settings!");
    	}
    }
}
