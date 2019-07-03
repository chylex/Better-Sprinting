package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientModManager.Feature;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.input.SprintKeyMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private static final int idDoubleTap = 199;
	private static final int idAllDirs = 198;
	private static final int idFlyBoost = 197;
	private static final int idFlyOnGround = 196;
	private static final int idDisableMod = 195;
	private static final int idAutoJump = 194;
	
	private static final int idKeyBindStart = 200;
	private static final int idSprintMode = 193;
	private static final int idDone = 192;
	private static final int idControls = 191;
	
	private final Minecraft mc = Minecraft.getMinecraft();
	private final GuiScreen parentScreen;
	
	private GuiButton btnSprintMode;
	private GuiButtonInputOption btnDoubleTap, btnAutoJump, btnFlyBoost, btnFlyOnGround, btnAllDirs, btnDisableMod;
	private GuiButtonInputBinding selectedBinding;
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		buttonList.clear();
		
		int left = (width / 2) - 155;
		int top = height / 6;
		
		for(int index = 0; index < ClientModManager.keyBindings.length; index++){
			addButton(new GuiButtonInputBinding(idKeyBindStart + index, left + 160 * (index % 2), top + 24 * (index / 2), ClientModManager.keyBindings[index], this::onBindingClicked));
		}
		
		btnSprintMode = addButton(new GuiButton(idSprintMode, left - 50, top, 48, "", this::onClickedSprintMode));
		btnDoubleTap = addButton(new GuiButtonInputOption(idDoubleTap, left, top + 60, "bs.doubleTapping", this::onButtonClicked));
		btnAllDirs = addButton(new GuiButtonInputOption(idAllDirs, left + 160, top + 60, "bs.runAllDirs", this::onButtonClicked));
		btnFlyBoost = addButton(new GuiButtonInputOption(idFlyBoost, left, top + 84, "bs.flyBoost", this::onButtonClicked));
		btnFlyOnGround = addButton(new GuiButtonInputOption(idFlyOnGround, left + 160, top + 84, "bs.flyOnGround", this::onButtonClicked));
		btnDisableMod = addButton(new GuiButtonInputOption(idDisableMod, left + 160, top + 108, "bs.disableMod", this::onButtonClicked));
		btnAutoJump = addButton(new GuiButtonInputOption(idAutoJump, left, top + 108, "bs.autoJump", this::onButtonClicked));
		
		addButton(new GuiButton(idDone, (width / 2) - 100, top + 168, parentScreen == null ? 98 : 200, I18n.format("gui.done"), this::onClickedDone));
		
		if (parentScreen == null){
			addButton(new GuiButton(idControls, (width / 2) + 2, top + 168, 98, I18n.format("options.controls"), this::onClickedControls));
		}
		
		updateButtonState();
		updateButtonText();
	}
	
	private void updateButtonState(){
		for(net.minecraft.client.gui.GuiButton button:buttonList){
			if (button instanceof GuiButtonInputBinding){
				KeyBinding binding = ((GuiButtonInputBinding)button).binding;
				
				if (binding == ClientModManager.keyBindSprintToggle || binding == ClientModManager.keyBindSneakToggle){
					button.enabled = !ClientModManager.isModDisabled();
				}
			}
		}
		
		btnSprintMode.enabled = !ClientModManager.isModDisabled();
		btnDoubleTap.enabled = !ClientModManager.isModDisabled();
		btnAllDirs.enabled = Feature.RUN_IN_ALL_DIRS.isAvailable();
		btnFlyBoost.enabled = Feature.FLY_BOOST.isAvailable();
		btnFlyOnGround.enabled = Feature.FLY_ON_GROUND.isAvailable();
		btnDisableMod.enabled = ClientModManager.canManuallyEnableMod();
	}
	
	private void updateButtonText(){
		btnSprintMode.setMessage(I18n.format((ClientModManager.isModDisabled() ? SprintKeyMode.TAP : ClientSettings.sprintKeyMode).translationKey));
		btnDoubleTap.setTitleKey(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.setTitleKey(Feature.FLY_BOOST.isAvailable() ? (ClientSettings.flySpeedBoost == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost + 1) + "x") : "gui.unavailable");
		btnFlyOnGround.setTitleKey(Feature.FLY_ON_GROUND.isAvailable() ? (ClientSettings.flyOnGround ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnAllDirs.setTitleKey(Feature.RUN_IN_ALL_DIRS.isAvailable() ? (ClientSettings.enableAllDirs ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.setTitleKey(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.setTitleKey(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	private void onClickedControls(){
		mc.displayGuiScreen(new GuiControls(this, mc.gameSettings));
		ClientSettings.update(BetterSprintingMod.config);
	}
	
	private void onClickedDone(){
		mc.displayGuiScreen(parentScreen);
		ClientSettings.update(BetterSprintingMod.config);
	}
	
	private void onClickedSprintMode(){
		ClientSettings.sprintKeyMode = ClientSettings.sprintKeyMode.next();
		ClientSettings.update(BetterSprintingMod.config);
		updateButtonText();
	}
	
	private void onBindingClicked(GuiButtonInputBinding binding){
		if (selectedBinding != null){
			selectedBinding.setSelected(false);
		}
		
		selectedBinding = binding;
		selectedBinding.setSelected(true);
	}
	
	private void onButtonClicked(int id){
		switch(id){
			case idAutoJump:
				mc.gameSettings.autoJump = !mc.gameSettings.autoJump;
				mc.gameSettings.saveOptions();
				break;
				
			case idDisableMod:
				if (ClientModManager.canManuallyEnableMod()){
					ClientSettings.disableMod = !ClientSettings.disableMod;
					updateButtonState();
					updateButtonText();
				} break;
				
			case idFlyBoost:
				if (Feature.FLY_BOOST.isAvailable()){
					ClientSettings.flySpeedBoost = (ClientSettings.flySpeedBoost + 1) % 8;
				} break;
			
			case idFlyOnGround:
				if (Feature.FLY_ON_GROUND.isAvailable()){
					ClientSettings.flyOnGround = !ClientSettings.flyOnGround;
				} break;
				
			case idAllDirs:
				if (Feature.RUN_IN_ALL_DIRS.isAvailable()){
					ClientSettings.enableAllDirs = !ClientSettings.enableAllDirs;
				} break;
				
			case idDoubleTap:
				if (!ClientSettings.disableMod){
					ClientSettings.enableDoubleTap = !ClientSettings.enableDoubleTap;
				} break;
				
			default:
				return;
		}
		
		ClientSettings.update(BetterSprintingMod.config);
		updateButtonText();
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		if (selectedBinding != null){
			selectedBinding.setBinding(-100 + mouseButton);
			onSelectedBindingUpdated();
		}
		else{
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void handleKeyboardInput() throws IOException{
		if (!Keyboard.getEventKeyState() && selectedBinding != null){
			selectedBinding.setBinding(KeyModifier.NONE, Keyboard.getEventKey());
			onSelectedBindingUpdated();
		}
		
		super.handleKeyboardInput();
	}
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if (selectedBinding != null){
			if (keyCode == 1){
				selectedBinding.setBinding(KeyModifier.NONE, 0);
			}
			else if (keyCode != 0){
				selectedBinding.setBinding(KeyModifier.getActiveModifier(), keyCode);
			}
			else if (typedChar > 0){
				selectedBinding.setBinding(KeyModifier.getActiveModifier(), typedChar + 256);
			}
			
			onSelectedBindingUpdated();
		}
		else{
			super.keyTyped(typedChar, keyCode);
		}
	}
	
	private void onSelectedBindingUpdated(){
		if (!selectedBinding.isSelected()){
			selectedBinding = null;
		}
		
		for(net.minecraft.client.gui.GuiButton button:buttonList){
			if (button instanceof GuiButtonInputBinding){
				((GuiButtonInputBinding)button).updateKeyBindingText();
			}
		}
		
		ClientSettings.keyInfoSprintHold.readFrom(ClientModManager.keyBindSprintHold);
		ClientSettings.keyInfoSprintToggle.readFrom(ClientModManager.keyBindSprintToggle);
		ClientSettings.keyInfoSneakToggle.readFrom(ClientModManager.keyBindSneakToggle);
		ClientSettings.keyInfoOptionsMenu.readFrom(ClientModManager.keyBindOptionsMenu);
		KeyBinding.resetKeyBindingArrayAndHash();
		
		mc.gameSettings.saveOptions();
		ClientSettings.update(BetterSprintingMod.config);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		final int top = height / 6;
		final int middle = width / 2;
		
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "Better Sprinting", middle, 20, 16777215);
		
		super.drawScreen(mouseX, mouseY, partialTickTime);
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(net.minecraft.client.gui.GuiButton button:buttonList){
			if (button instanceof GuiButtonCustomInput){
				drawButtonTitle(((GuiButtonCustomInput)button).getTitle(), button, button.x < middle ? maxWidthLeft : maxWidthRight);
				
				if (button.isMouseOver()){
					String[] spl = ((GuiButtonCustomInput)button).getInfo();
					
					for(int line = 0; line < spl.length; line++){
						drawCenteredString(fontRenderer, spl[line], middle, top + 148 + (10 * line - (fontRenderer.FONT_HEIGHT * spl.length / 2)), -1);
					}
				}
			}
		}
	}
	
	private void drawButtonTitle(String title, net.minecraft.client.gui.GuiButton btn, int maxWidth){
		int lines = fontRenderer.listFormattedStringToWidth(title, maxWidth).size();
		fontRenderer.drawSplitString(title, btn.x + 76, btn.y + 7 - 5 * (lines - 1), maxWidth, -1);
	}
}
