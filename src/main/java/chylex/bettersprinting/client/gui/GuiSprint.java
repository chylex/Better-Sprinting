package chylex.bettersprinting.client.gui;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSprint extends GuiScreen{
	private static final int idDone = 200;
	private static final int idDoubleTap = 199;
	private static final int idAllDirs = 198;
	private static final int idFlyBoost = 197;
	private static final int idFlyOnGround = 196;
	private static final int idDisableMod = 195;
	private static final int idControls = 194;
	
	private final GuiScreen parentScreen;
	
	private GuiButton btnDoubleTap, btnFlyBoost, btnFlyOnGround, btnAllDirs, btnDisableMod;
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
	
	private <T extends GuiButton> T addButton(T button){
		buttonList.add(button);
		return button;
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.displayString = I18n.format(ClientModManager.canBoostFlying() ? (ClientSettings.flySpeedBoost == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost + 1) + "x") : "gui.unavailable");
		btnFlyOnGround.displayString = I18n.format(ClientModManager.canFlyOnGround() ? (ClientSettings.flyOnGround ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnAllDirs.displayString = I18n.format(ClientModManager.canRunInAllDirs() ? (ClientSettings.enableAllDirs ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
	}
	
	private void onBindingClicked(GuiButtonInputBinding button){
		buttonId = button.id;
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
	public void handleKeyboardInput(){
		char chr = Keyboard.getEventCharacter();
		
		if (Keyboard.getEventKey() == 0 && chr >= ' ' || Keyboard.getEventKeyState()){
			keyTyped(chr, Keyboard.getEventKey());
		}
		
		int key = Keyboard.getEventKey() == 0 ? chr + 256 : Keyboard.getEventKey();
		
		if (key != 0 && !Keyboard.isRepeatEvent() && pressTime <= Minecraft.getSystemTime() - 20L && !Keyboard.getEventKeyState()){
			buttonId = -1;
		}
		
		mc.func_152348_aa();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button){
		if (!handleInput(button - 100)){
			super.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	protected void keyTyped(char keyChar, int keyCode){
		if (!handleInput(keyCode)){
			super.keyTyped(keyChar, keyCode);
		}
	}
	
	private boolean handleInput(int keyId){
		if (buttonId >= 0 && buttonId < 180){
			KeyBinding binding = ClientModManager.keyBindings[buttonId];
			
			if (keyId == Keyboard.KEY_ESCAPE){
				binding.setKeyCode(0);
			}
			else{
				binding.setKeyCode(keyId);
			}
			
			buttonId = -1;
			
			KeyBinding.resetKeyBindingArrayAndHash();
			
			ClientSettings.keyCodeSprintHold = ClientModManager.keyBindSprintHold.getKeyCode();
			ClientSettings.keyCodeSprintToggle = ClientModManager.keyBindSprintToggle.getKeyCode();
			ClientSettings.keyCodeSneakToggle = ClientModManager.keyBindSneakToggle.getKeyCode();
			ClientSettings.keyCodeOptionsMenu = ClientModManager.keyBindOptionsMenu.getKeyCode();
			
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
		drawCenteredString(fontRendererObj, "Better Sprinting", width / 2, 20, 16777215);
		
		super.drawScreen(mouseX, mouseY, partialTickTime);
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			KeyBinding binding = ClientModManager.keyBindings[a];
			
			boolean hasConflict = false;
			
			if (binding.getKeyCode() != 0){
				for(KeyBinding other:mc.gameSettings.keyBindings){
					if (binding != other && binding.getKeyCode() == other.getKeyCode()){
						hasConflict = true;
					}
				}
			}
			
			GuiButton button = (GuiButton)buttonList.get(a);
			
			if (buttonId == a){
				button.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + getKeyName(binding) + EnumChatFormatting.WHITE + " <";
			}
			else if (hasConflict){
				button.displayString = EnumChatFormatting.RED + getKeyName(binding);
			}
			else{
				button.displayString = getKeyName(binding);
			}
		}
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(GuiButton button:(List<GuiButton>)buttonList){
			if (button instanceof GuiButtonInputOption){
				drawButtonTitle(((GuiButtonInputOption)button).getTitle(), button, button.xPosition < middle ? maxWidthLeft : maxWidthRight);
				
				if (button.enabled && button.func_146115_a()){
					String[] spl = ((GuiButtonInputOption)button).getInfo();
					
					for(int line = 0; line < spl.length; line++){
						drawCenteredString(fontRendererObj, spl[line], middle, top + 148 + (10 * line - (fontRendererObj.FONT_HEIGHT * spl.length / 2)), -1);
					}
				}
			}
		}
	}
	
	private void drawButtonTitle(String title, GuiButton btn, int maxWidth){
		int lines = fontRendererObj.listFormattedStringToWidth(title, maxWidth).size();
		fontRendererObj.drawSplitString(title, btn.xPosition + 76, btn.yPosition + 7 - 5 * (lines - 1), maxWidth, -1);
	}
	
	public static String getKeyName(KeyBinding binding){
		return GameSettings.getKeyDisplayString(binding.getKeyCode());
	}
}