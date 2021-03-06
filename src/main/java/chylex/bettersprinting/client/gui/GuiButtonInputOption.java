package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class GuiButtonInputOption extends GuiButtonCustomInput{
	private final Runnable onClick;
	
	public GuiButtonInputOption(final int x, final int y, final String titleKey, final Runnable onClick){
		super(x, y, titleKey);
		this.onClick = onClick;
	}
	
	@Override
	public void onPress(){
		onClick.run();
	}
}
