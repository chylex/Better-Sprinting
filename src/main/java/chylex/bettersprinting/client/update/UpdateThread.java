package chylex.bettersprinting.client.update;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.system.Log;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class UpdateThread extends Thread{
	private static final String url = "https://raw.githubusercontent.com/chylex/Better-Sprinting/master/UpdateNotificationData.txt";
	
	private final String modVersion;
	private final String mcVersion;
	
	public UpdateThread(String modVersion){
		this.modVersion = modVersion;
		this.mcVersion = MinecraftForge.MC_VERSION;
		setPriority(MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(1337L);
			
			JsonElement root = new JsonParser().parse(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
			
			List<VersionEntry> versionList = Lists.newArrayList();
			VersionEntry newestVersionForCurrentMC = null;
			int counter = -1;
			String buildId = "";
			boolean isInDev = true;
			
			String downloadURL = "https://bsprint.chylex.com/";
			
			Log.debug("Detecting Better Sprinting updates...");
			
			for(Entry<String, JsonElement> entry:root.getAsJsonObject().entrySet()){
				if (entry.getKey().charAt(0) == '~'){
					if (entry.getKey().substring(1).equals("URL")){
						downloadURL = entry.getValue().getAsString();
					}
				}
				else versionList.add(new VersionEntry(entry.getKey(), entry.getValue().getAsJsonObject()));
			}
			
			Collections.sort(versionList);
			
			for(VersionEntry version:versionList){
				Log.debug("Reading update data: $0", version.versionIdentifier);
				
				if (version.isSupportedByMC(mcVersion)){
					if (newestVersionForCurrentMC == null)newestVersionForCurrentMC = version;
					++counter;
				}
				
				if (version.modVersion.equals(modVersion)){
					isInDev = false;
					buildId = version.buildId;
					break;
				}
			}
			
			if (isInDev){
				Log.debug("In-dev version used, notifications disabled.");
				return;
			}
			else Log.debug("Done.");
			
			StringBuilder message = null;
			
			if (!buildId.isEmpty() && !buildId.equals(BetterSprintingMod.buildId)){
				message = new StringBuilder()
					.append(TextFormatting.GREEN).append(" [Better Sprinting ").append(modVersion).append("]").append(TextFormatting.RESET)
					.append("\n Caution, you are using a broken build that can cause critical crashes! Please, redownload or update the mod.");
			}
			else if (counter > 0 && newestVersionForCurrentMC != null && ClientSettings.enableUpdateNotifications){
				message = new StringBuilder()
					.append(TextFormatting.GREEN).append(" [Better Sprinting ").append(modVersion).append("]").append(TextFormatting.RESET)
					.append("\n Found update ").append(TextFormatting.GREEN).append(newestVersionForCurrentMC.modVersionName).append(TextFormatting.RESET)
					.append(" for MC ").append(mcVersion).append(", released ").append(newestVersionForCurrentMC.releaseDate)
					.append(".\n You are currently ").append(counter).append(" version").append(counter == 1 ? "" : "s").append(" behind.");
			}
			
			if (message != null){
				message.append("\n ").append(TextFormatting.GOLD).append("Click to download: ").append(downloadURL);
				
				for(String s:message.toString().split("\n")){
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ForgeHooks.newChatWithLinks(s));
				}
			}
		}
		catch(UnknownHostException e){}
		catch(Exception e){
			Log.throwable(e, "Error detecting updates!");
		}
	}
}