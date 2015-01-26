package chylex.bettersprinting.client.compatibility;
import java.io.File;
import java.io.FileInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.system.Log;

public class OldClientConfig{
	public static boolean loadAndDeleteOldConfig(){
		File file = new File(Minecraft.getMinecraft().mcDataDir,"sprint.nbt");
    	if (!file.exists())return false;
    	
    	try{
    		NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(file)).getCompoundTag("Data");
    		
    		ClientSettings.keyCodeOptionsMenu = tag.getInteger("keyMenu");
    		ClientSettings.keyCodeSprintHold = tag.getInteger("keySprint");
    		ClientSettings.keyCodeSprintToggle = tag.getInteger("keySprintToggle");
    		ClientSettings.keyCodeSneakToggle = tag.getInteger("keySneakToggle");
    		ClientSettings.flySpeedBoost = tag.getByte("flyBoost");
    		ClientSettings.enableDoubleTap = tag.getBoolean("doubleTap");
    		ClientSettings.enableAllDirs = tag.getBoolean("allDirs");
    		ClientSettings.disableMod = tag.getBoolean("disableMod");
    		ClientSettings.showedSneakWarning = tag.getBoolean("showedWarn");
    		ClientSettings.enableUpdateNotifications = tag.getBoolean("updateNotifications");
    		
    		return file.delete();
    	}catch(Exception e){
    		e.printStackTrace();
    		Log.error("Error loading old Better Sprinting settings!");
    		return false;
    	}
	}
}
