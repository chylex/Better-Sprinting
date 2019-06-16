package chylex.bettersprinting.system.core;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(BetterSprintingCore.supportedMinecraftVersion)
@IFMLLoadingPlugin.SortingIndex(1000)
public final class BetterSprintingCore implements IFMLLoadingPlugin{
	public static final String supportedMinecraftVersion = "1.11.2";
	
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
