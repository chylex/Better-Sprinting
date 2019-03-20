package net.minecraft.src;

public class GuiSprint extends GuiScreen{
	private GuiScreen parentScreen;
	private int buttonId = -1;
	
	private GuiButton btnDoubleTap,btnFlyBoost,btnAllDirs;
	
	protected KeyBinding[] sprintBindings=new KeyBinding[]{
		GuiScreen.keyBindSprint, GuiScreen.keyBindSprintToggle, GuiScreen.keyBindSneakToggle, GuiScreen.keyBindSprintMenu
	};
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen=parentScreen;
	}
	
	private int getLeftColumnX(){
        return width/2-155;
    }
	
	private String getKeyCodeString(int i){
		return GameSettings.getKeyDisplayString(sprintBindings[i].keyCode);
	}
	
	public void initGui(){
		GuiScreen.fromBs = false;
        StringTranslate var1 = StringTranslate.getInstance();
        int var2 = getLeftColumnX();
        int ypos = 0;

        for(int var3=0; var3<sprintBindings.length; ++var3){
        	ypos=height/6+24*(var3>>1);
			controlList.add(new GuiSmallButton(var3, var2+var3%2*160, ypos, 70, 20, getKeyCodeString(var3)));
		}
        
        ypos+=48;
        btnDoubleTap=new GuiButton(199,var2,ypos,70,20,""); controlList.add(btnDoubleTap);
        btnAllDirs=new GuiButton(198,var2+160,ypos,70,20,""); controlList.add(btnAllDirs);
        if (!GuiScreen.canRunInAllDirs(mc))btnAllDirs.enabled=false;
        ypos+=24;
        btnFlyBoost=new GuiButton(197,var2,ypos,70,20,""); controlList.add(btnFlyBoost);
        
        controlList.add(new GuiButton(200,width/2-100,height/6+168,parentScreen==null?98:200,20,var1.translateKey("gui.done")));
        if (parentScreen==null)controlList.add(new GuiButton(190,width/2+2,height/6+168,98,20,var1.translateKey("options.controls")));
        updateButtons();
    }
	
	protected void actionPerformed(GuiButton btn){
		saveSprint();
		
		for(int var2=0; var2<sprintBindings.length; ++var2){
			((GuiButton)controlList.get(var2)).displayString=getKeyCodeString(var2);
		}

		switch(btn.id){
			case 190:
				GuiScreen.fromBs=true;
				mc.displayGuiScreen(new GuiControls(this,mc.gameSettings));
				break;
				
			case 197:
				GuiScreen.flyingBoost++;
				if (GuiScreen.flyingBoost==8)GuiScreen.flyingBoost=0;
				break;
				
			case 198:
				if (GuiScreen.canRunInAllDirs(mc))GuiScreen.allowAllDirs=!GuiScreen.allowAllDirs;
				break;
				
			case 199:
				GuiScreen.allowDoubleTap=!GuiScreen.allowDoubleTap;
				break;
				
			case 200:
				if (parentScreen==null){
					mc.displayGuiScreen((GuiScreen)null);
	                mc.setIngameFocus();
	                mc.sndManager.resumeAllSounds();
				}
				else{
					mc.displayGuiScreen(parentScreen);
					parentScreen.prevWidth=0;
				}
				break;
				
			default:
				buttonId=btn.id;
				btn.displayString="> "+mc.gameSettings.getOptionDisplayString(btn.id)+" <";
		}
		updateButtons();
	}

	protected void mouseClicked(int par1, int par2, int par3){
		if (!mouseClickedOrKeyTyped(-100+par3))super.mouseClicked(par1,par2,par3);
	}

	protected void keyTyped(char par1, int par2){
		if (!mouseClickedOrKeyTyped(par2))super.keyTyped(par1,par2);
	}
	
	private boolean mouseClickedOrKeyTyped(int par1){
		if (buttonId>=0&&buttonId<180){
			sprintBindings[buttonId].keyCode=par1;
			((GuiButton)controlList.get(buttonId)).displayString=getKeyCodeString(buttonId);
			buttonId=-1;
			KeyBinding.resetKeyBindingArrayAndHash();
			return true;
		}
		return false;
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString=GuiScreen.allowDoubleTap?"Enabled":"Disabled";
		btnFlyBoost.displayString=GuiScreen.flyingBoost==0?"Disabled":(GuiScreen.flyingBoost+1)+"x";
		btnAllDirs.displayString=GuiScreen.canRunInAllDirs(mc)?(GuiScreen.allowAllDirs?"Enabled":"Disabled"):"Unavailable";
	}
	
	public void drawScreen(int par1, int par2, float par3){
        drawDefaultBackground();
		drawCenteredString(fontRenderer,"Better Sprinting",width/2,20,16777215);

		int var4=getLeftColumnX();
		int var5=0;
		while(var5<sprintBindings.length){
			boolean var6=false;
			int var7=0;

			while(true){
				if (var7<sprintBindings.length){
					if (var7==var5||sprintBindings[var5].keyCode!=sprintBindings[var7].keyCode){
						++var7;
						continue;
					}
					var6=true;
				}
				
				for(int i=0; i<mc.gameSettings.keyBindings.length; i++){
					if (sprintBindings[var5].keyCode==mc.gameSettings.keyBindings[i].keyCode){
						var6=true;
						break;
					}
				}

				if (buttonId==var5)((GuiButton)controlList.get(var5)).displayString="\u00a7f> \u00a7e??? \u00a7f<";
				else if (var6)((GuiButton)controlList.get(var5)).displayString="\u00a7c"+getKeyCodeString(var5);
				else ((GuiButton)controlList.get(var5)).displayString=getKeyCodeString(var5);

				drawString(fontRenderer,sprintBindings[var5].keyDescription,var4+var5%2*160+70+6,height/6+24*(var5>>1)+7,-1);
				++var5;
				break;
			}
		}

		drawButtonTitle("Double tapping",btnDoubleTap);
		drawButtonTitle("Run in all directions",btnAllDirs);
		drawButtonTitle("Flying boost",btnFlyBoost);

		super.drawScreen(par1,par2,par3);
    }
	
	private void drawButtonTitle(String title, GuiButton btn){
		drawString(fontRenderer,title,btn.xPosition+70+6,btn.yPosition+7,-1);
	}
}
// CHANGED (NEW) CLASS
