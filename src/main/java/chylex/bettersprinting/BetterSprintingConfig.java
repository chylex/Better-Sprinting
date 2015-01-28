package chylex.bettersprinting;
import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		if (e.modID.equals("BetterSprinting"))reload();
	}
	
	private void reload(){
		BetterSprintingMod.proxy.loadSidedConfig(this);
	}
	
	public void update(){
		if (config.hasChanged())config.save();
	}
	
	@SideOnly(Side.CLIENT)
	public List<IConfigElement> getClientGuiElements(String category){
		return new ConfigElement(config.getCategory(category)).getChildElements();
	}
	
	public void setCategory(String newCategory){
		this.currentCategory = newCategory;
	}
	
	public Property getBool(String name, boolean defValue, String comment){
		return config.get(currentCategory,name,defValue,comment);
	}
	
	public Property getInt(String name, int defValue, String comment){
		return config.get(currentCategory,name,defValue,comment);
	}
	
	public void setBool(String name, boolean value){
		config.get(currentCategory,name,value).set(value);
	}
	
	public void setInt(String name, int value){
		config.get(currentCategory,name,value).set(value);
	}
	
	@Override
	public String toString(){
		return config.toString();
	}
}
