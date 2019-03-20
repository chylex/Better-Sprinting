package chylex.bettersprinting.client.update;
import chylex.bettersprinting.BetterSprintingMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class VersionEntry implements Comparable<VersionEntry>{
	public final String versionIdentifier;
	public final String modVersionName;
	public final String modVersion;
	public final String[] mcVersions;
	public final String releaseDate;
	private final Short orderId;
	
	public VersionEntry(String versionIdentifier, JsonObject node){
		this.versionIdentifier=versionIdentifier;
		modVersion=node.get("modVersion").getAsString();
		
		JsonArray array=node.get("mcVersions").getAsJsonArray();
		mcVersions=new String[array.size()];
		int a=-1;
		for(JsonElement mcVersionNode:array)mcVersions[++a]=mcVersionNode.getAsString();
		
		releaseDate=node.get("releaseDate").getAsString();
		
		short i=0;
		String tmp=modVersion;
		String[] idSplit=versionIdentifier.split(" - ");
		
		if (idSplit.length!=2)BetterSprintingMod.logger.warn("Incorrect version identifier: "+versionIdentifier);
		else{
			tmp=idSplit[1];
			
			try{
				i=Short.parseShort(idSplit[0]);
			}catch(NumberFormatException e){
				BetterSprintingMod.logger.warn("Incorrect version identifier: "+versionIdentifier);
			}
		}
		orderId=i;
		modVersionName=tmp;
	}
	
	public boolean isSupportedByMC(String mcVersion){
		for(String version:mcVersions){
			if (version.equals(mcVersion))return true;
		}
		return false;
	}

	@Override
	public int compareTo(VersionEntry o){
		return o.orderId.compareTo(orderId);
	}
}
