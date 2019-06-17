package chylex.bettersprinting.client.gui;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import chylex.bettersprinting.BetterSprintingMod;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModGuiFactory implements IModGuiFactory{
	@Override
	public void initialize(Minecraft minecraftInstance){}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass(){
		return GuiGeneralConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public class GuiGeneralConfig extends GuiConfig{
		public GuiGeneralConfig(GuiScreen parent){
			super(parent, BetterSprintingMod.config.getClientGuiElements("client"), "BetterSprinting", false, false, GuiConfig.getAbridgedConfigPath(BetterSprintingMod.config.getFileName()));
		}
	}
}
