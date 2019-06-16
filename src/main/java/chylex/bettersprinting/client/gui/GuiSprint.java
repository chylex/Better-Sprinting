package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private static final int idDone = 200;
	private static final int idDoubleTap = 199;
	private static final int idAllDirs = 198;
	private static final int idFlyBoost = 197;
	private static final int idFlyOnGround = 196;
	private static final int idDisableMod = 195;
	private static final int idAutoJump = 194;
	private static final int idControls = 193;
	
	private final GuiScreen parentScreen;
	
	private GuiButton btnDoubleTap, btnAutoJump, btnFlyBoost, btnFlyOnGround, btnAllDirs, btnDisableMod;
	private int buttonId = -1;
	private long pressTime;
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		buttonList.clear();
		
		int left = (width / 2) - 155;
		int top = height / 6;
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			GuiButtonInputBinding btn = addButton(new GuiButtonInputBinding(a, left + 160 * (a % 2), top + 24 * (a / 2), ClientModManager.keyBindings[a], this::onBindingClicked));
			
			if ((a == 1 || a == 2) && ClientModManager.isModDisabled()){
				btn.enabled = false;
			}
		}
		
		btnDoubleTap = addButton(new GuiButtonInputOption(idDoubleTap, left, top + 60, "bs.doubleTapping"));
		btnAllDirs = addButton(new GuiButtonInputOption(idAllDirs, left + 160, top + 60, "bs.runAllDirs"));
		btnFlyBoost = addButton(new GuiButtonInputOption(idFlyBoost, left, top + 84, "bs.flyBoost"));
		btnFlyOnGround = addButton(new GuiButtonInputOption(idFlyOnGround, left + 160, top + 84, "bs.flyOnGround"));
		btnDisableMod = addButton(new GuiButtonInputOption(idDisableMod, left + 160, top + 108, "bs.disableMod"));
		btnAutoJump = addButton(new GuiButtonInputOption(idAutoJump, left, top + 108, "bs.autoJump"));
		
		if (ClientModManager.isModDisabled())btnDoubleTap.enabled = false;
		if (!ClientModManager.canRunInAllDirs())btnAllDirs.enabled = false;
		if (!ClientModManager.canBoostFlying())btnFlyBoost.enabled = false;
		if (!ClientModManager.canFlyOnGround())btnFlyOnGround.enabled = false;
		if (!ClientModManager.inMenu())btnDisableMod.enabled = false;
		
		addButton(new GuiButtonInteractive(idDone, width / 2 - 100, top + 168, parentScreen == null ? 98 : 200, 20, I18n.format("gui.done"), this::onClickedDone));
		
		if (parentScreen == null){
			addButton(new GuiButtonInteractive(idControls, width / 2 + 2 , top + 168, 98, 20, I18n.format("options.controls"), this::onClickedControls));
		}
		
		updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.displayString = I18n.format(ClientModManager.canBoostFlying() ? (ClientSettings.flySpeedBoost == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost + 1) + "x") : "gui.unavailable");
		btnFlyOnGround.displayString = I18n.format(ClientModManager.canFlyOnGround() ? (ClientSettings.flyOnGround ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnAllDirs.displayString = I18n.format(ClientModManager.canRunInAllDirs() ? (ClientSettings.enableAllDirs ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.displayString = I18n.format(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	private void onBindingClicked(GuiButtonInputBinding button){
		buttonId = button.id;
		button.displayString = "> " + GameSettings.getKeyDisplayString(mc.gameSettings.keyBindings[button.id].getKeyCode()) + " <";
	}
	
	private void onClickedControls(@SuppressWarnings("unused") GuiButtonInteractive button){
		mc.displayGuiScreen(new GuiControls(this, mc.gameSettings));
		ClientSettings.update(BetterSprintingMod.config);
	}
	
	private void onClickedDone(@SuppressWarnings("unused") GuiButtonInteractive button){
		if (parentScreen == null){
			mc.setIngameFocus();
		}
		else{
			mc.displayGuiScreen(parentScreen);
		}
		
		ClientSettings.update(BetterSprintingMod.config);
	}
	
	@Override
	protected void actionPerformed(GuiButton btn){
		switch(btn.id){
			case idAutoJump:
				mc.gameSettings.autoJump = !mc.gameSettings.autoJump;
				mc.gameSettings.saveOptions();
				break;
			
			case idDisableMod:
				if (ClientModManager.inMenu()){
					ClientSettings.disableMod = !ClientSettings.disableMod;
					initGui();
				}
				
				break;
			
			case idFlyBoost:
				if (ClientModManager.canBoostFlying() && ++ClientSettings.flySpeedBoost == 8){
					ClientSettings.flySpeedBoost = 0;
				}
				
				break;
			
			case idFlyOnGround:
				if (ClientModManager.canFlyOnGround()){
					ClientSettings.flyOnGround = !ClientSettings.flyOnGround;
				}
				
				break;
			
			case idAllDirs:
				if (ClientModManager.canRunInAllDirs()){
					ClientSettings.enableAllDirs = !ClientSettings.enableAllDirs;
				}
				
				break;
			
			case idDoubleTap:
				if (!ClientSettings.disableMod){
					ClientSettings.enableDoubleTap = !ClientSettings.enableDoubleTap;
				}
				
				break;
			
			default:
				return;
		}
		
		ClientSettings.update(BetterSprintingMod.config);
		updateButtons();
	}
	
	@Override
	public void handleKeyboardInput() throws IOException{
		char chr = Keyboard.getEventCharacter();
		
		if (Keyboard.getEventKey() == 0 && chr >= ' ' || Keyboard.getEventKeyState()){
			keyTyped(chr, Keyboard.getEventKey());
		}
		
		int key = Keyboard.getEventKey() == 0 ? chr + 256 : Keyboard.getEventKey();
		
		if (key != 0 && !Keyboard.isRepeatEvent() && pressTime <= Minecraft.getSystemTime() - 20L && !Keyboard.getEventKeyState()){
			buttonId = -1;
		}
		
		mc.dispatchKeypresses();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException{
		if (!handleInput(button - 100)){
			super.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode) throws IOException{
		if (!handleInput(keyCode)){
			super.keyTyped(keyChar, keyCode);
		}
	}
	
	private boolean handleInput(int keyId){
		if (buttonId >= 0 && buttonId < 180){
			KeyBinding binding = ClientModManager.keyBindings[buttonId];
			
			if (keyId == Keyboard.KEY_ESCAPE){
				binding.setKeyModifierAndCode(KeyModifier.NONE, 0);
			}
			else{
				binding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), keyId);
			}
			
			buttonList.get(buttonId).displayString = binding.getDisplayName();
			
			if (!KeyModifier.isKeyCodeModifier(keyId)){
				buttonId = -1;
			}
			
			KeyBinding.resetKeyBindingArrayAndHash();
			
			ClientSettings.keyCodeSprintHold = ClientModManager.keyBindSprintHold.getKeyCode();
			ClientSettings.keyCodeSprintToggle = ClientModManager.keyBindSprintToggle.getKeyCode();
			ClientSettings.keyCodeSneakToggle = ClientModManager.keyBindSneakToggle.getKeyCode();
			ClientSettings.keyCodeOptionsMenu = ClientModManager.keyBindOptionsMenu.getKeyCode();
			
			ClientSettings.keyModSprintHold = ClientModManager.keyBindSprintHold.getKeyModifier();
			ClientSettings.keyModSprintToggle = ClientModManager.keyBindSprintToggle.getKeyModifier();
			ClientSettings.keyModSneakToggle = ClientModManager.keyBindSneakToggle.getKeyModifier();
			ClientSettings.keyModOptionsMenu = ClientModManager.keyBindOptionsMenu.getKeyModifier();
			
			ClientSettings.update(BetterSprintingMod.config);
			
			pressTime = Minecraft.getSystemTime();
			return true;
		}
		
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		final int top = height / 6;
		final int middle = width / 2;
		
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "Better Sprinting", width / 2, 20, 16777215);
		
		super.drawScreen(mouseX, mouseY, partialTickTime);
		
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
				buttonList.get(a).displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + binding.getDisplayName() + TextFormatting.WHITE + " <";
			}
			else if (hasConflict){
				buttonList.get(a).displayString = (hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + binding.getDisplayName();
			}
			else{
				buttonList.get(a).displayString = binding.getDisplayName();
			}
		}
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(GuiButton button:buttonList){
			if (button instanceof GuiButtonInputOption){
				drawButtonTitle(((GuiButtonInputOption)button).getTitle(), button, button.x < middle ? maxWidthLeft : maxWidthRight);
				
				if (button.isMouseOver()){
					String[] spl = ((GuiButtonInputOption)button).getInfo();
					
					for(int line = 0; line < spl.length; line++){
						drawCenteredString(fontRenderer, spl[line], middle, top + 148 + (10 * line - (fontRenderer.FONT_HEIGHT * spl.length / 2)), -1);
					}
				}
			}
		}
	}
	
	private void drawButtonTitle(String title, GuiButton btn, int maxWidth){
		int lines = fontRenderer.listFormattedStringToWidth(title, maxWidth).size();
		fontRenderer.drawSplitString(title, btn.x + 76, btn.y + 7 - 5 * (lines - 1), maxWidth, -1);
	}
}
