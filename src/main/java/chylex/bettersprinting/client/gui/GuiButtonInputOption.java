package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class GuiButtonInputOption extends GuiButtonCustomInput{
	private final Consumer<GuiButtonInputOption> onClicked;
	
	public GuiButtonInputOption(int id, int x, int y, String titleKey, Consumer<GuiButtonInputOption> onClick){
		super(id, x, y, "", titleKey);
		this.onClicked = onClick;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY){
		super.onClick(mouseX, mouseY);
		onClicked.accept(this);
	}
}
