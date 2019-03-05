package chylex.bettersprinting.client.gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonCustomInput extends GuiButtonExt{
	private final String titleKey;
	
	public GuiButtonCustomInput(int id, int x, int y, String buttonText, String titleKey){
		super(id, x, y, 70, 20, buttonText);
		this.titleKey = titleKey;
	}
	
	public String getTitle(){
		return I18n.format(titleKey);
	}
	
	public String[] getInfo(){
		return I18n.format(titleKey + ".info").split("#");
	}
}
