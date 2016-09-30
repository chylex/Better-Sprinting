package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingMod;

@SideOnly(Side.CLIENT)
public class GuiGeneralConfig extends GuiConfig{
	public GuiGeneralConfig(GuiScreen parent){
		super(parent, BetterSprintingMod.config.getClientGuiElements("client"), "BetterSprinting", false, false, GuiConfig.getAbridgedConfigPath(BetterSprintingMod.config.getFileName()));
	}
}
