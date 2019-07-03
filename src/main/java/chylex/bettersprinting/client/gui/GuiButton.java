package chylex.bettersprinting.client.gui;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButton extends net.minecraft.client.gui.GuiButton{
	private final Runnable onClick;
	
	public GuiButton(int id, int x, int y, int width, String buttonText, Runnable onClick){
		super(id, x, y, width, 20, buttonText);
		this.onClick = onClick;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandler){
		super.playPressSound(soundHandler);
		onClick.run();
	}
	
	public void setMessage(String message){
		displayString = message;
	}
}
