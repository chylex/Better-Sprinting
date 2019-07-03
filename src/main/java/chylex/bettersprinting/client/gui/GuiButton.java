package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public class GuiButton extends GuiButtonExt{
	private final Runnable onClick;
	
	public GuiButton(int id, int x, int y, int width, String buttonText, Runnable onClick){
		super(id, x, y, width, 20, buttonText);
		this.onClick = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		onClick.run();
	}
	
	public void setMessage(String message){
		displayString = message;
	}
}
