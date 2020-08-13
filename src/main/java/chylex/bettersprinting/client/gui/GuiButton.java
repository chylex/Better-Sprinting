package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButton extends Button{
	private final Runnable onClick;
	
	public GuiButton(final int x, final int y, final int width, final String buttonText, final Runnable onClick){
		super(x, y, width, 20, new StringTextComponent(buttonText), null);
		this.onClick = onClick;
	}
	
	@Override
	public void onPress(){
		onClick.run();
	}
}
