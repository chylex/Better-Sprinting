package chylex.bettersprinting.client.gui;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private static final int idDone = 200;
	private static final int idDoubleTap = 199;
	private static final int idAllDirs = 198;
	private static final int idFlyBoost = 197;
	private static final int idDisableMod = 196;
	private static final int idAutoJump = 195;
	private static final int idControls = 194;
	
	private final String[] buttonTitles = new String[]{
		"bs.sprint.hold.info",
		"bs.sprint.toggle.info",
		"bs.sneak.toggle.info",
		"bs.menu.info",
		"bs.doubleTapping.info",
		"bs.runAllDirs.info",
		"bs.flyBoost.info",
		"bs.disableMod.info",
		"bs.autoJump.info"
	};
	
	private final GuiScreen parentScreen;
	
	private GuiButton btnDoubleTap, btnAutoJump, btnFlyBoost, btnAllDirs, btnDisableMod;
	private int buttonId = -1;
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		buttonList.clear();
		
	    int left = getLeftColumnX(), top = height/6;
	
	    for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			GuiButton btn = new GuiButton(a, left+160*(a%2), top+24*(a/2), 70, 20, getKeyCodeString(a));
			buttonList.add(btn);
			
			if ((a == 1 || a == 2) && ClientModManager.isModDisabled()){
				btn.enabled = false;
			}
		}
	    
	    buttonList.add(btnDoubleTap = new GuiButton(idDoubleTap, left, top+60, 70, 20, ""));
	    buttonList.add(btnAllDirs = new GuiButton(idAllDirs, left+160, top+60, 70, 20, ""));
	    buttonList.add(btnFlyBoost = new GuiButton(idFlyBoost, left, top+84, 70, 20, ""));
	    buttonList.add(btnDisableMod = new GuiButton(idDisableMod, left+160, top+84, 70, 20, ""));
	    buttonList.add(btnAutoJump = new GuiButton(idAutoJump, left, top+108, 70, 20, ""));
	    
	    if (ClientModManager.isModDisabled())btnDoubleTap.enabled = false;
	    if (!ClientModManager.canRunInAllDirs(mc))btnAllDirs.enabled = false;
	    if (!ClientModManager.canBoostFlying(mc))btnFlyBoost.enabled = false;
	    if (!(mc.player == null && mc.world == null))btnDisableMod.enabled = false;
	    
	    buttonList.add(new GuiButton(idDone, width/2-100, top+168, parentScreen == null ? 98 : 200, 20, I18n.format("gui.done")));
	    
	    if (parentScreen == null){
	    	buttonList.add(new GuiButton(idControls, width/2+2, top+168, 98, 20, I18n.format("options.controls")));
	    }
	    
	    updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.displayString = I18n.format(ClientModManager.canBoostFlying(mc) ? (ClientSettings.flySpeedBoost == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost+1)+"x") : "gui.unavailable");
		btnAllDirs.displayString = I18n.format(ClientModManager.canRunInAllDirs(mc) ? (ClientSettings.enableAllDirs ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.displayString = I18n.format(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	@Override
	protected void actionPerformed(GuiButton btn){
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			buttonList.get(a).displayString = getKeyCodeString(a);
		}
	
		switch(btn.id){
			case idControls:
				mc.displayGuiScreen(new GuiControls(this, mc.gameSettings));
				break;
				
			case idAutoJump:
				mc.gameSettings.autoJump = !mc.gameSettings.autoJump;
				mc.gameSettings.saveOptions();
				break;
				
			case idDisableMod:
				if (ClientModManager.inMenu(mc)){
					ClientSettings.disableMod = !ClientSettings.disableMod;
					initGui();
				}
				
				break;
				
			case idFlyBoost:
				if (ClientModManager.canBoostFlying(mc) && ++ClientSettings.flySpeedBoost == 8){
					ClientSettings.flySpeedBoost = 0;
				}
				
				break;
				
			case idAllDirs:
				if (ClientModManager.canRunInAllDirs(mc)){
					ClientSettings.enableAllDirs = !ClientSettings.enableAllDirs;
				}
				
				break;
				
			case idDoubleTap:
				if (!ClientSettings.disableMod){
					ClientSettings.enableDoubleTap = !ClientSettings.enableDoubleTap;
				}
				
				break;
				
			case idDone:
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
		if (!handleInput(button-100))super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode) throws IOException{
		if (!handleInput(keyCode))super.keyTyped(keyChar, keyCode);
	}
	
	private boolean handleInput(int keyId){
		if (buttonId >= 0 && buttonId < 180){
			ClientModManager.keyBindings[buttonId].setKeyCode(keyId == Keyboard.KEY_ESCAPE ? 0 : keyId);
			buttonList.get(buttonId).displayString = getKeyCodeString(buttonId);
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
		drawCenteredString(fontRenderer, "Better Sprinting", width/2, 20, 16777215);
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			KeyBinding binding = ClientModManager.keyBindings[a];
			
			boolean hasConflict = false;
			boolean hasOnlyModifierConflict = true;
			
			if (binding.getKeyCode() != 0){
				for(KeyBinding other:mc.gameSettings.keyBindings){
					if (binding != other && binding.conflicts(other)){
						hasConflict = true;
						hasOnlyModifierConflict &= binding.hasKeyCodeModifierConflict(other);
					}
				}
			}
			
			if (buttonId == a){
				buttonList.get(a).displayString = TextFormatting.WHITE+"> "+TextFormatting.YELLOW+"??? "+TextFormatting.WHITE+"<";
			}
			else if (hasConflict){
				buttonList.get(a).displayString = (hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED)+getKeyCodeString(a);
			}
			else{
				buttonList.get(a).displayString = getKeyCodeString(a);
			}
			
			String desc = (binding == mc.gameSettings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
			drawButtonTitle(I18n.format(desc), buttonList.get(a), a%2 == 0 ? maxWidthLeft : maxWidthRight);
		}
	
		drawButtonTitle(I18n.format("bs.doubleTapping"), btnDoubleTap, maxWidthLeft);
		drawButtonTitle(I18n.format("bs.runAllDirs"), btnAllDirs, maxWidthRight);
		drawButtonTitle(I18n.format("bs.flyBoost"), btnFlyBoost, maxWidthLeft);
		drawButtonTitle(I18n.format("bs.disableMod"), btnDisableMod, maxWidthRight);
		drawButtonTitle(I18n.format("bs.autoJump"), btnAutoJump, maxWidthLeft);
		
		for(int a = 0, top = height/6; a < buttonList.size(); a++){
			GuiButton btn = buttonList.get(a);
			
			if (mouseX >= btn.x && mouseX < btn.x+btn.width && mouseY >= btn.y && mouseY < btn.y+20){
				String info = a < buttonTitles.length ? buttonTitles[a] : "";
				String[] spl = I18n.format(info).split("#");
				
				for(int line = 0; line < spl.length; line++){
					drawCenteredString(fontRenderer, spl[line], width/2, top+148+10*line-(fontRenderer.FONT_HEIGHT*spl.length/2), -1);
				}
				
				break;
			}
		}
		
		super.drawScreen(mouseX, mouseY, partialTickTime);
	}
	
	private int getLeftColumnX(){
	    return width/2-155;
	}
	
	private String getKeyCodeString(int i){
		return GameSettings.getKeyDisplayString(ClientModManager.keyBindings[i].getKeyCode());
	}
	
	private void drawButtonTitle(String title, GuiButton btn, int maxWidth){
		int lines = fontRenderer.listFormattedStringToWidth(title, maxWidth).size();
		fontRenderer.drawSplitString(title, btn.x+76, btn.y+7-5*(lines-1), maxWidth, -1);
	}
}