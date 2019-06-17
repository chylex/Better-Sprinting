package chylex.bettersprinting.system.core;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(BetterSprintingCore.supportedMinecraftVersion)
@IFMLLoadingPlugin.SortingIndex(1000)
public final class BetterSprintingCore implements IFMLLoadingPlugin{
	public static final String supportedMinecraftVersion = "1.7.10";
	
	private static boolean wasInitialized;
	
	public static boolean wasInitialized(){
		return wasInitialized;
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
	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}
}
