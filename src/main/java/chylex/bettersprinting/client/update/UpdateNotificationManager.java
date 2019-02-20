package chylex.bettersprinting.client.update;
import java.util.Calendar;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.system.Log;

public final class UpdateNotificationManager{
	private static final Preferences globalData = Preferences.userRoot().node("chylex");
	private static final String prefKey = "bettersprint-lut";
	
	private static boolean hasRun; // assume nobody keeps Minecraft running for more than 24 hours
	
	public static void run(){
		if (hasRun)return;
		hasRun = true;
		
		if (ClientSettings.enableUpdateNotifications.get() || ClientSettings.enableBuildCheck.get()){
			long time = Calendar.getInstance().getTimeInMillis();
			
			if (time-globalData.getLong(prefKey, 0L) > 86400000L){ // 24 hours
				globalData.putLong(prefKey, time);
				
				try{
					globalData.flush();
				}catch(BackingStoreException ex){
					Log.throwable(ex, "Could not update last update notification time, stopping the process to avoid excessive spamming.");
					return;
				}
				
				new UpdateThread(BetterSprintingMod.modVersion).start();
			}
		}
	}
}
