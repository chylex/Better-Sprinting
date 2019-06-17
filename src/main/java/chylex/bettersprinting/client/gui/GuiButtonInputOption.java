package chylex.bettersprinting.client.gui;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonInputOption extends GuiButtonExt{
	private final String titleKey;
	
	public GuiButtonInputOption(int id, int x, int y, String titleKey){
		super(id, x, y, 70, 20, "");
		this.id = id;
		this.titleKey = titleKey;
	}
	
	public String getTitle(){
		return I18n.format(titleKey);
	}
	
	public String[] getInfo(){
		return I18n.format(titleKey + ".info").split("#");
	}
}
