package chylex.bettersprinting.client.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonSprint extends GuiButton{
	public GuiButtonSprint(int id, int x, int y, int width, int height, String displayText){
		super(id, x, y, width, height, displayText);
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandler){
		super.playPressSound(soundHandler);
		Minecraft.getMinecraft().displayGuiScreen(new GuiSprint(Minecraft.getMinecraft().currentScreen));
	}
}
