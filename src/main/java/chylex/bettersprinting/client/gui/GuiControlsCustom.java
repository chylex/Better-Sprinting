package chylex.bettersprinting.client.gui;
import java.io.IOException;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlsCustom extends GuiControls{
	private static GuiScreen getParentScreen(GuiControls gui){
		for(Field field:GuiControls.class.getDeclaredFields()){
			if (GuiScreen.class.isAssignableFrom(field.getType())){
				try{
					field.setAccessible(true);
					return (GuiScreen)field.get(gui);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public GuiControlsCustom(GuiControls originalControlsGui){
		super(getParentScreen(originalControlsGui),Minecraft.getMinecraft().gameSettings);
	}

	@Override
	public void initGui(){
		super.initGui();
		buttonList.add(0,new GuiButton(205,width/2+5,18+24,150,20,"Better Sprinting"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		if (button.id == 205)mc.displayGuiScreen(new GuiSprint(this));
		else super.actionPerformed(button);
	}
}
