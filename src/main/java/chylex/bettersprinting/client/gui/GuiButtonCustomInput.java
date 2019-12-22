package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
abstract class GuiButtonCustomInput extends Button{
	private final String titleKey;
	
	public GuiButtonCustomInput(int x, int y, String buttonText, String titleKey){
		super(x, y, 70, 20, buttonText, null);
		this.titleKey = titleKey;
	}
	
	@Override
	public abstract void onPress();
	
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
