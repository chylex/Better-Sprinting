package chylex.bettersprinting.client.update;
import java.util.Calendar;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.system.Log;
import com.google.common.base.Joiner;

public final class UpdateNotificationManager{
	public static String mcVersions = "?";
	public static String releaseDate = "?";
	
	public static synchronized void refreshUpdateData(VersionEntry version){
		mcVersions = Joiner.on(", ").join(version.mcVersions);
		releaseDate = version.releaseDate;
	}
	
	private static final Preferences globalData = Preferences.userRoot().node("chylex");
	private static final String prefKey = "bettersprint-lut";
	
	private static boolean hasRun; // assume nobody keeps Minecraft running for more than 24 hours
	
	public static void run(){
		if (hasRun)return;
		hasRun = true;
		
		if (ClientSettings.enableUpdateNotifications || ClientSettings.enableBuildCheck){
			long time = Calendar.getInstance().getTimeInMillis();
			
			if (time-globalData.getLong(prefKey,0L) > 86400000L){ // 24 hours
				globalData.putLong(prefKey,time);
				
				try{
					globalData.flush();
				}catch(BackingStoreException ex){
					Log.throwable(ex,"Could not update last update notification time, stopping the process to avoid excessive spamming.");
					return;
				}
				
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
	}
}
