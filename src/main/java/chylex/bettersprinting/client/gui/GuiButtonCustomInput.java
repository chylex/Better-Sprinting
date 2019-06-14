package chylex.bettersprinting.client.gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonCustomInput extends GuiButtonExt{
	public final int id;
	private final String titleKey;
	
	public GuiButtonCustomInput(int id, int x, int y, String buttonText, String titleKey){
		super(x, y, 70, 20, buttonText, null);
		this.id = id;
		this.titleKey = titleKey;
	}
	
	@Override
	public void onPress(){}
	
	public String getTitle(){
		return I18n.format(titleKey);
	}
	
	public String[] getInfo(){
		return I18n.format(titleKey + ".info").split("#");
	}
}
