package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonCustomInput<T> extends Button{
	private final String titleKey;
	private final Consumer<T> onClick;
	
	public GuiButtonCustomInput(int x, int y, String buttonText, String titleKey, Consumer<T> onClick){
		super(x, y, 70, 20, buttonText, null);
		this.titleKey = titleKey;
		this.onClick = onClick;
	}
	
	protected abstract T getContext();
	
	@Override
	public void onPress(){
		if (active){
			onClick.accept(getContext());
		}
	}
	
	public void setTitleKey(String translationKey){
		setMessage(I18n.format(translationKey));
	}
	
	public String getTitle(){
		return I18n.format(titleKey);
	}
	
	public String[] getInfo(){
		return I18n.format(titleKey + ".info").split("#");
	}
}
