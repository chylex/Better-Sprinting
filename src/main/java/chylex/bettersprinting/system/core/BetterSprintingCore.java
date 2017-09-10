package chylex.bettersprinting.system.core;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.1")
@IFMLLoadingPlugin.SortingIndex(1000)
public final class BetterSprintingCore implements IFMLLoadingPlugin{
	static boolean transformOnLivingUpdate;
	
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
		transformOnLivingUpdate = !((List)data.get("coremodList")).stream().anyMatch(o -> o.toString().startsWith("PlayerAPIPlugin"));
	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}
}
