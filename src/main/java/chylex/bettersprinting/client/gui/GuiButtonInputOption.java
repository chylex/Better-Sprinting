package chylex.bettersprinting.client.gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class GuiButtonInputOption extends GuiButtonCustomInput<Integer>{
	private final int id;
	
	public GuiButtonInputOption(int id, int x, int y, String titleKey, Consumer<Integer> onClick){
		super(x, y, "", titleKey, onClick);
		this.id = id;
	}
	
	@Override
	protected Integer getContext(){
		return id;
	}
}
