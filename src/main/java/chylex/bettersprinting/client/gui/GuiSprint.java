package chylex.bettersprinting.client.gui;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private final GuiScreen parentScreen;
	private int buttonId = -1;
	
	private KeyBinding[] sprintBindings = new KeyBinding[]{
		ClientModManager.keyBindSprintHold,
		ClientModManager.keyBindSprintToggle,
		ClientModManager.keyBindSneakToggle,
		ClientModManager.keyBindOptionsMenu
	};
	
	private GuiButton btnDoubleTap, btnFlyBoost, btnAllDirs, btnDisableMod;
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		buttonList.clear();
		
	    int left = getLeftColumnX(), top = height/6;
	
	    for(int a = 0; a < sprintBindings.length; a++){
			GuiOptionButton btn = new GuiOptionButton(a,left+a%2*160,top+24*(a>>1),70,20,getKeyCodeString(a));
			buttonList.add(btn);
			if ((a == 1 || a == 2) && ClientModManager.isModDisabled())btn.enabled = false;
		}
	    
	    buttonList.add(btnDoubleTap = new GuiButton(199,left,top+72,70,20,""));
	    buttonList.add(btnAllDirs = new GuiButton(198,left+160,top+72,70,20,""));
	    buttonList.add(btnFlyBoost = new GuiButton(197,left,top+96,70,20,""));
	    buttonList.add(btnDisableMod = new GuiButton(196,left+160,top+96,70,20,""));
	    
	    if (ClientModManager.isModDisabled())btnDoubleTap.enabled = false;
	    if (!ClientModManager.canRunInAllDirs(mc))btnAllDirs.enabled = false;
	    if (!ClientModManager.canBoostFlying(mc))btnFlyBoost.enabled = false;
	    if (!(mc.thePlayer == null && mc.theWorld == null))btnDisableMod.enabled = false;
	    
	    buttonList.add(new GuiButton(200,width/2-100,top+168,parentScreen == null ? 98 : 200,20,I18n.format("gui.done")));
	    if (parentScreen == null)buttonList.add(new GuiButton(190,width/2+2,top+168,98,20,I18n.format("options.controls")));
	    updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString = ClientModManager.isModDisabled() ? "Unavailable" : (ClientSettings.enableDoubleTap ? "Enabled" : "Disabled");
		btnFlyBoost.displayString = ClientModManager.canBoostFlying(mc) ? (ClientSettings.flySpeedBoost == 0 ? "Disabled" : (ClientSettings.flySpeedBoost+1)+"x") : "Unavailable";
		btnAllDirs.displayString = ClientModManager.canRunInAllDirs(mc) ? (ClientSettings.enableAllDirs ? "Enabled" : "Disabled") : "Unavailable";
		btnDisableMod.displayString = ClientModManager.isModDisabled() ? "Yes" : "No";
	}
	
	@Override
	protected void actionPerformed(GuiButton btn){
		for(int a = 0; a < sprintBindings.length; a++)((GuiButton)buttonList.get(a)).displayString = getKeyCodeString(a);
	
		switch(btn.id){
			case 190:
				mc.displayGuiScreen(new GuiControls(this,mc.gameSettings));
				break;
				
			case 196:
				if (ClientModManager.inMenu(mc)){
					ClientSettings.disableMod = !ClientSettings.disableMod;
					initGui();
				}
				
				break;
				
			case 197:
				if (ClientModManager.canBoostFlying(mc) && ++ClientSettings.flySpeedBoost == 8)ClientSettings.flySpeedBoost = 0;
				break;
				
			case 198:
				if (ClientModManager.canRunInAllDirs(mc))ClientSettings.enableAllDirs = !ClientSettings.enableAllDirs;
				break;
				
			case 199:
				if (!ClientSettings.disableMod)ClientSettings.enableDoubleTap = !ClientSettings.enableDoubleTap;
				break;
				
			case 200:
				if (parentScreen == null)mc.setIngameFocus();
				else mc.displayGuiScreen(parentScreen);
				
				break;
				
			default:
				buttonId = btn.id;
				btn.displayString = "> "+GameSettings.getKeyDisplayString(mc.gameSettings.keyBindings[btn.id].getKeyCode())+" <";
		}

		ClientSettings.update(BetterSprintingMod.config);
		updateButtons();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException{
		if (!handleInput(button-100))super.mouseClicked(mouseX,mouseY,button);
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode) throws IOException{
		if (!handleInput(keyCode))super.keyTyped(keyChar,keyCode);
	}
	
	private boolean handleInput(int keyId){
		if (buttonId >= 0 && buttonId < 180){
			sprintBindings[buttonId].setKeyCode(keyId);
			((GuiButton)buttonList.get(buttonId)).displayString = getKeyCodeString(buttonId);
			buttonId = -1;
			KeyBinding.resetKeyBindingArrayAndHash();
			
			ClientSettings.keyCodeSprintHold = ClientModManager.keyBindSprintHold.getKeyCode();
			ClientSettings.keyCodeSprintToggle = ClientModManager.keyBindSprintToggle.getKeyCode();
			ClientSettings.keyCodeSneakToggle = ClientModManager.keyBindSneakToggle.getKeyCode();
			ClientSettings.keyCodeOptionsMenu = ClientModManager.keyBindOptionsMenu.getKeyCode();
			ClientSettings.update(BetterSprintingMod.config);
			return true;
		}
		else return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		drawCenteredString(fontRendererObj,"Better Sprinting",width/2,20,16777215);
	
		int left = getLeftColumnX(), top = height/6;
		
		for(int a = 0; a < sprintBindings.length;){
			boolean alreadyUsed = false;
			int b = 0;
	
			while(true){
				if (b < sprintBindings.length){
					if (b == a || sprintBindings[a].getKeyCode() != sprintBindings[b].getKeyCode()){
						++b;
						continue;
					}
					
					alreadyUsed = true;
				}
				
				for(KeyBinding binding:mc.gameSettings.keyBindings){
					if (sprintBindings[a].getKeyCode() == binding.getKeyCode()){
						alreadyUsed = true;
						break;
					}
				}
	
				if (buttonId == a)((GuiButton)buttonList.get(a)).displayString = "\u00a7f> \u00a7e??? \u00a7f<";
				else if (alreadyUsed)((GuiButton)buttonList.get(a)).displayString = "\u00a7c"+getKeyCodeString(a);
				else ((GuiButton)buttonList.get(a)).displayString = getKeyCodeString(a);
	
				drawString(fontRendererObj,sprintBindings[a].getKeyDescription(),left+a%2*160+70+6,top+24*(a>>1)+7,-1);
				a++;
				break;
			}
		}
	
		drawButtonTitle("Double tapping",btnDoubleTap);
		drawButtonTitle("Run in all directions",btnAllDirs);
		drawButtonTitle("Flying boost",btnFlyBoost);
		drawButtonTitle("Disable mod functionality",btnDisableMod);
		
		for(int a = 0; a < buttonList.size(); a++){
			GuiButton btn = (GuiButton)buttonList.get(a);
			
			if (mouseX >= btn.xPosition && mouseX <= btn.xPosition+btn.getButtonWidth() && mouseY >= btn.yPosition && mouseY <= btn.yPosition+20){
				String info="";
				
				switch(a){
					case 0: info="Hold to sprint."; break;
					case 1: info="Press once to start or stop sprinting."; break;
					case 2: info="Press once to start or stop sneaking.#You cannot open menus whilst sneaking."; break;
					case 3: info="Key to open this menu ingame."; break;
					case 4: info="Enable or disable sprinting by double-tapping the forward key."; break;
					case 5: info="Sprint in all directions.#You cannot use this in multiplayer unless the server allows it."; break;
					case 6: info="Press whilst flying in creative mode to fly faster.#Works in survival mode flying (modded game) if the server allows it."; break;
					case 7: info="Disables all non-vanilla functionality of Better Sprinting.#This option can be used if a server doesn't allow the mod."; break;
				}
				
				String[] spl = info.split("#");
				for(int line = 0; line < spl.length; line++)drawCenteredString(fontRendererObj,spl[line],width/2,top+143+10*line-(fontRendererObj.FONT_HEIGHT*spl.length/2),-1);
				break;
			}
		}
		
		super.drawScreen(mouseX,mouseY,partialTickTime);
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