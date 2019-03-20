package chylex.bettersprinting.forge;
import java.io.File;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.6.4") // CHANGE << don't forget to update!
@IFMLLoadingPlugin.TransformerExclusions({"chylex.bettersprinting.forge"})
public class BetterSprintingCoremod implements IFMLLoadingPlugin{
	public static File modFile;
	public static String[] classes = new String[]{ "MovementInputFromOptions","EntityPlayerSP","GuiScreen" };
	
	@Override
	public String[] getLibraryRequestClass(){
		return null;
	}

	@Override
	public String[] getASMTransformerClass(){
		return new String[]{ "chylex.bettersprinting.forge.BetterSprintingTransformer" };
	}

	@Override
	public String getModContainerClass(){
		return "chylex.bettersprinting.forge.BetterSprintingContainer";
	}

	@Override
	public String getSetupClass(){
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data){
		Object o=data.get("coremodLocation");
		modFile=o instanceof File?(File)o:null;
	}
}
