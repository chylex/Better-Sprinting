package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlsCustom extends GuiControls{
	public GuiControlsCustom(GuiControls originalControlsGui){
		super(originalControlsGui.parentScreen,Minecraft.getMinecraft().gameSettings);
	}

	@Override
	public void initGui(){
		super.initGui();
		if (parentScreen == null || parentScreen.getClass() != GuiSprint.class)buttonList.add(0,new GuiButton(205,width/2+5,18+24,150,20,"Better Sprinting"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 205)mc.displayGuiScreen(new GuiSprint(this));
		else super.actionPerformed(button);
	}
}
