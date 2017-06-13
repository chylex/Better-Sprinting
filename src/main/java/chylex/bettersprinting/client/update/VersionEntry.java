package chylex.bettersprinting.client.update;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import chylex.bettersprinting.system.Log;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class VersionEntry implements Comparable<VersionEntry>{
	public final String versionIdentifier;
	public final String modVersionName;
	public final String modVersion;
	public final String[] mcVersions;
	public final String releaseDate;
	public final String buildId;
	private final Byte orderId;
	
	VersionEntry(String versionIdentifier, JsonObject node){
		this.versionIdentifier = versionIdentifier;
		
		modVersion = node.get("modVersion").getAsString();
		mcVersions = Streams.stream(node.get("mcVersions").getAsJsonArray()).map(JsonElement::getAsString).toArray(String[]::new);
		buildId = node.has("buildId") ? node.get("buildId").getAsString() : "";
		releaseDate = node.get("releaseDate").getAsString();
		
		byte i = 0;
		String tmp = modVersion;
		String[] idSplit = versionIdentifier.split(" - ");
		
		if (idSplit.length != 2){
			Log.error("Incorrect version identifier: $0", versionIdentifier);
		}
		else{
			tmp = idSplit[1];
			i = NumberUtils.toByte(idSplit[0], (byte)0);
			
			if (i == 0){
				Log.error("Incorrect version identifier: $0", versionIdentifier);
			}
		}
		
		orderId = Byte.valueOf(i);
		modVersionName = tmp;
	}
	
	public boolean isSupportedByMC(String mcVersion){
		return ArrayUtils.contains(mcVersions, mcVersion);
	}

	@Override
	public int compareTo(VersionEntry o){
		return o.orderId.compareTo(orderId);
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof VersionEntry && ((VersionEntry)obj).orderId.compareTo(orderId) == 0;
	}
	
	@Override
	public int hashCode(){
		return orderId;
	}
}
