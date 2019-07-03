package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonCustomInput<T> extends GuiButton{
	private final String titleKey;
	private final Consumer<T> onClick;
	
	public GuiButtonCustomInput(int id, int x, int y, String buttonText, String titleKey, Consumer<T> onClick){
		super(id, x, y, 70, 20, buttonText);
		this.titleKey = titleKey;
		this.onClick = onClick;
	}
	
	protected abstract T getContext();
	
	@Override
	public void onClick(double mouseX, double mouseY){
		onClick.accept(getContext());
	}
	
	public void setMessage(String message){
		displayString = message;
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
