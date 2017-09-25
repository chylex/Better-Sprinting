package chylex.bettersprinting.system.core;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(BetterSprintingCore.supportedMinecraftVersion)
@IFMLLoadingPlugin.SortingIndex(1000)
public final class BetterSprintingCore implements IFMLLoadingPlugin{
	public static final String supportedMinecraftVersion = "1.12.2";
	
	static boolean wasInitialized;
	static boolean transformOnLivingUpdate;
	
	public static boolean wasInitialized(){
		return wasInitialized;
	}
	
	public static boolean usePlayerAPI(){
		return !transformOnLivingUpdate;
	}
	
	@Override
	public String[] getASMTransformerClass(){
		return new String[]{ TransformerEntityPlayerSP.class.getName() };
	}

	@Override
	public String getModContainerClass(){
		return null;
	}

	@Override
	public String getSetupClass(){
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data){
		wasInitialized = true;
		transformOnLivingUpdate = !((List)data.get("coremodList")).stream().anyMatch(o -> o.toString().startsWith("PlayerAPIPlugin"));
	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}
}
