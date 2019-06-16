package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import java.util.Set;

public class ModGuiFactory implements IModGuiFactory{
	@Override
	public void initialize(Minecraft minecraftInstance){}
	
	@Override
	public boolean hasConfigGui(){
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen){
		return new GuiGeneralConfig(parentScreen);
	}
	
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass(){
		return GuiGeneralConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){
		return null;
	}
}
