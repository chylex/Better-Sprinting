package chylex.bettersprinting.client.gui;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingMod;

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
	public static class GuiGeneralConfig extends GuiConfig{
		public GuiGeneralConfig(GuiScreen parent){
			super(parent, BetterSprintingMod.config.getClientGuiElements("client"), "BetterSprinting", false, false, GuiConfig.getAbridgedConfigPath(BetterSprintingMod.config.getFileName()));
		}
	}
}
