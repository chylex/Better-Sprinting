package chylex.bettersprinting;
import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BetterSprintingConfig{
	private final Configuration config;
	private String currentCategory = "unknown";
	
	BetterSprintingConfig(File file){
		FMLCommonHandler.instance().bus().register(this);
		config = new Configuration(file);
		reload();
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent e){
		if (e.modID.equals(BetterSprintingMod.modId)){
			reload();
		}
	}
	
	private void reload(){
		BetterSprintingMod.proxy.loadSidedConfig(this);
	}
	
	public void update(){
		if (config.hasChanged()){
			config.save();
		}
	}
	
	public String getFileName(){
		return config.toString();
	}
	
	@SideOnly(Side.CLIENT)
	public List<IConfigElement> getClientGuiElements(String category){
		return new ConfigElement(config.getCategory(category)).getChildElements();
	}
	
	public void setCategory(String newCategory){
		this.currentCategory = newCategory;
	}
	
	public Property get(String name, boolean defValue){
		return config.get(currentCategory, name, defValue, "");
	}
	
	public Property get(String name, boolean defValue, String comment){
		return config.get(currentCategory, name, defValue, comment);
	}
	
	public Property get(String name, int defValue){
		return config.get(currentCategory, name, defValue, "");
	}
	
	public Property get(String name, int defValue, String comment){
		return config.get(currentCategory, name, defValue, comment);
	}
	
	public Property get(String name, String defValue){
		return config.get(currentCategory, name, defValue, "");
	}
	
	public Property get(String name, String defValue, String comment){
		return config.get(currentCategory, name, defValue, comment);
	}
	
	public void set(String name, boolean value){
		config.get(currentCategory, name, value).set(value);
	}
	
	public void set(String name, int value){
		config.get(currentCategory, name, value).set(value);
	}
	
	public void set(String name, String value){
		config.get(currentCategory, name, value).set(value);
	}
}

