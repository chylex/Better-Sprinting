package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingMod;

@SideOnly(Side.CLIENT)
public class GuiGeneralConfig extends GuiConfig{
	public GuiGeneralConfig(GuiScreen parent){
		super(parent,BetterSprintingMod.config.getClientGuiElements("client"),"BetterSprinting",false,false,GuiConfig.getAbridgedConfigPath(BetterSprintingMod.config.getFileName()));
	}
}
