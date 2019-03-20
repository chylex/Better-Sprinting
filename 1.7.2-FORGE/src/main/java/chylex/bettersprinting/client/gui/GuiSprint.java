package chylex.bettersprinting.client.gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import chylex.bettersprinting.client.ClientModManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private GuiScreen parentScreen;
	private int buttonId = -1;
	private GuiButton btnDoubleTap,btnFlyBoost,btnAllDirs,btnDisableMod,btnUpdateNotifications;
	protected KeyBinding[] sprintBindings=new KeyBinding[]{
		ClientModManager.keyBindSprint, ClientModManager.keyBindSprintToggle, ClientModManager.keyBindSneakToggle, ClientModManager.keyBindSprintMenu
	};
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen=parentScreen;
	}
	
	@Override
	public void initGui(){
		buttonList.clear();
		
		ClientModManager.fromBs = false;
	    int left=getLeftColumnX(), ypos=0;
	
	    for(int a=0; a<sprintBindings.length; ++a){
			ypos=height/6+24*(a>>1);
			GuiOptionButton btn=new GuiOptionButton(a,left+a%2*160,ypos,70,20,getKeyCodeString(a));
			buttonList.add(btn);
			if ((a==1||a==2)&&ClientModManager.disableModFunctionality)btn.enabled=false;
		}
	    
	    ypos+=48;
	    btnDoubleTap=new GuiButton(199,left,ypos,70,20,""); buttonList.add(btnDoubleTap);
	    if (ClientModManager.disableModFunctionality)btnDoubleTap.enabled=false;
	    
	    btnAllDirs=new GuiButton(198,left+160,ypos,70,20,""); buttonList.add(btnAllDirs);
	    if (!ClientModManager.canRunInAllDirs(mc))btnAllDirs.enabled=false;
	    
	    ypos+=24;
	    btnFlyBoost=new GuiButton(197,left,ypos,70,20,""); buttonList.add(btnFlyBoost);
	    if (!ClientModManager.canBoostFlying(mc))btnFlyBoost.enabled=false;
	    
	    btnDisableMod=new GuiButton(196,left+160,ypos,70,20,""); buttonList.add(btnDisableMod);
	    if (!(mc.thePlayer==null&&mc.theWorld==null))btnDisableMod.enabled=false;
	    
	    ypos+=24;
	    btnUpdateNotifications=new GuiButton(195,left,ypos,70,20,""); buttonList.add(btnUpdateNotifications);
	    
	    buttonList.add(new GuiButton(200,width/2-100,height/6+168,parentScreen==null?98:200,20,I18n.format("gui.done")));
	    if (parentScreen==null)buttonList.add(new GuiButton(190,width/2+2,height/6+168,98,20,I18n.format("options.controls")));
	    updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString=ClientModManager.disableModFunctionality?"Unavailable":(ClientModManager.allowDoubleTap?"Enabled":"Disabled");
		btnFlyBoost.displayString=ClientModManager.canBoostFlying(mc)?(ClientModManager.flyingBoost==0?"Disabled":(ClientModManager.flyingBoost+1)+"x"):"Unavailable";
		btnAllDirs.displayString=ClientModManager.canRunInAllDirs(mc)?(ClientModManager.allowAllDirs?"Enabled":"Disabled"):"Unavailable";
		btnDisableMod.displayString=ClientModManager.disableModFunctionality?"Yes":"No";
		btnUpdateNotifications.displayString=ClientModManager.enableUpdateNotifications?"Yes":"No";
	}
	
	@Override
	protected void actionPerformed(GuiButton btn){
		for(int var2=0; var2<sprintBindings.length; ++var2){
			((GuiButton)buttonList.get(var2)).displayString=getKeyCodeString(var2);
		}
	
		switch(btn.id){
			case 190:
				ClientModManager.fromBs=true;
				mc.displayGuiScreen(new GuiControls(this,mc.gameSettings));
				break;
			
			case 195:
				ClientModManager.enableUpdateNotifications=!ClientModManager.enableUpdateNotifications;
				break;
				
			case 196:
				if (mc.thePlayer==null&&mc.theWorld==null){
					ClientModManager.disableModFunctionality=!ClientModManager.disableModFunctionality;
					initGui();
				}
				break;
				
			case 197:
				if (ClientModManager.canBoostFlying(mc)&&++ClientModManager.flyingBoost==8)ClientModManager.flyingBoost=0;
				break;
				
			case 198:
				if (ClientModManager.canRunInAllDirs(mc))ClientModManager.allowAllDirs=!ClientModManager.allowAllDirs;
				break;
				
			case 199:
				if (!ClientModManager.disableModFunctionality)ClientModManager.allowDoubleTap=!ClientModManager.allowDoubleTap;
				break;
				
			case 200:
				if (parentScreen==null){
					mc.displayGuiScreen((GuiScreen)null);
	                mc.setIngameFocus();
				}
				else mc.displayGuiScreen(parentScreen);
				break;
				
			default:
				buttonId=btn.id;
				btn.displayString="> "+GameSettings.getKeyDisplayString(mc.gameSettings.keyBindings[btn.id].getKeyCode())+" <";
		}
		
		ClientModManager.saveSprint(mc);
		updateButtons();
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3){
		if (!handleInput(-100+par3))super.mouseClicked(par1,par2,par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2){
		if (!handleInput(par2))super.keyTyped(par1,par2);
	}
	
	private boolean handleInput(int keyId){
		if (buttonId>=0&&buttonId<180){
			sprintBindings[buttonId].setKeyCode(keyId);
			((GuiButton)buttonList.get(buttonId)).displayString=getKeyCodeString(buttonId);
			buttonId=-1;
			KeyBinding.resetKeyBindingArrayAndHash();
			return true;
		}
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3){
		drawDefaultBackground();
		drawCenteredString(fontRendererObj,"Better Sprinting",width/2,20,16777215);
	
		int left=getLeftColumnX(),a=0;
		while(a<sprintBindings.length){
			boolean alreadyUsed=false;
			int b=0;
	
			while(true){
				if (b<sprintBindings.length){
					if (b==a||sprintBindings[a].getKeyCode()!=sprintBindings[b].getKeyCode()){
						++b;
						continue;
					}
					alreadyUsed=true;
				}
				
				for(int i=0; i<mc.gameSettings.keyBindings.length; i++){
					if (sprintBindings[a].getKeyCode()==mc.gameSettings.keyBindings[i].getKeyCode()){
						alreadyUsed=true;
						break;
					}
				}
	
				if (buttonId==a)((GuiButton)buttonList.get(a)).displayString="\u00a7f> \u00a7e??? \u00a7f<";
				else if (alreadyUsed)((GuiButton)buttonList.get(a)).displayString="\u00a7c"+getKeyCodeString(a);
				else ((GuiButton)buttonList.get(a)).displayString=getKeyCodeString(a);
	
				drawString(fontRendererObj,sprintBindings[a].getKeyDescription(),left+a%2*160+70+6,height/6+24*(a>>1)+7,-1);
				++a;
				break;
			}
		}
	
		drawButtonTitle("Double tapping",btnDoubleTap);
		drawButtonTitle("Run in all directions",btnAllDirs);
		drawButtonTitle("Flying boost",btnFlyBoost);
		drawButtonTitle("Disable mod functionality",btnDisableMod);
		drawButtonTitle("Update notifications",btnUpdateNotifications);
		
		for(int i=0; i<buttonList.size(); i++){
			GuiButton btn=(GuiButton)buttonList.get(i);
			if (mouseX>=btn.xPosition&&mouseX<=btn.xPosition+btn.getButtonWidth()&&
				mouseY>=btn.yPosition&&mouseY<=btn.yPosition+20){
				String info="";
				
				switch(i){
					case 0: info="Hold to sprint."; break;
					case 1: info="Press once to start or stop sprinting."; break;
					case 2: info="Press once to start or stop sneaking.#You cannot open menus whilst sneaking."; break;
					case 3: info="Key to open this menu ingame."; break;
					case 4: info="Enable or disable sprinting by double-tapping the forward key."; break;
					case 5: info="Sprint in all directions.#You cannot use this in multiplayer unless the server allows it."; break;
					case 6: info="Press whilst flying in creative mode to fly faster.#Works in survival mode flying (modded game) if the server allows it."; break;
					case 7: info="Disables all non-vanilla functionality of Better Sprinting.#This option can be used if a server doesn't allow the mod."; break;
					case 8: info="Toggles update notifications."; break;
				}
				
				String[] spl=info.split("#");
				drawCenteredString(fontRendererObj,spl[0],width/2,height/6+146,-1);
				if (spl.length==2)drawCenteredString(fontRendererObj,spl[1],width/2,height/6+156,-1);
				break;
			}
		}
		
		super.drawScreen(mouseX,mouseY,par3);
	}
	
	private int getLeftColumnX(){
	    return width/2-155;
	}
	
	private String getKeyCodeString(int i){
		return GameSettings.getKeyDisplayString(sprintBindings[i].getKeyCode());
	}
	
	private void drawButtonTitle(String title, GuiButton btn){
		drawString(fontRendererObj,title,btn.xPosition+70+6,btn.yPosition+7,-1);
	}
}