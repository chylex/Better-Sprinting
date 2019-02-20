package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
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
	private KeyBinding selectedBinding;
	
	public GuiSprint(GuiScreen parentScreen){
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui(){
		buttons.clear();
		
		int left = (width / 2) - 155;
		int top = height / 6;
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			GuiButton btn = new GuiButtonInputBinding(a, left + 160 * (a % 2), top + 24 * (a / 2), 70, 20, ClientModManager.keyBindings[a], this::onBindingClicked);
			addButton(btn);
			
			if ((a == 1 || a == 2) && ClientModManager.isModDisabled()){
				btn.enabled = false;
			}
		}
		
		btnDoubleTap = addButton(new GuiButtonCustom(idDoubleTap, left, top + 60, 70, 20, "", this::onButtonClicked));
		btnAllDirs = addButton(new GuiButtonCustom(idAllDirs, left + 160, top + 60, 70, 20, "", this::onButtonClicked));
		btnFlyBoost = addButton(new GuiButtonCustom(idFlyBoost, left, top + 84, 70, 20, "", this::onButtonClicked));
		btnDisableMod = addButton(new GuiButtonCustom(idDisableMod, left + 160, top + 84, 70, 20, "", this::onButtonClicked));
		btnAutoJump = addButton(new GuiButtonCustom(idAutoJump, left, top + 108, 70, 20, "", this::onButtonClicked));
		
		if (ClientModManager.isModDisabled())btnDoubleTap.enabled = false;
		if (!ClientModManager.canRunInAllDirs())btnAllDirs.enabled = false;
		if (!ClientModManager.canBoostFlying())btnFlyBoost.enabled = false;
		if (!ClientModManager.inMenu())btnDisableMod.enabled = false;
		
		addButton(new GuiButtonCustom(idDone, width / 2 - 100, top + 168, parentScreen == null ? 98 : 200, 20, I18n.format("gui.done"), this::onButtonClicked));
		
		if (parentScreen == null){
			addButton(new GuiButtonCustom(idControls, width / 2 + 2, top + 168, 98, 20, I18n.format("options.controls"), this::onButtonClicked));
		}
		
		updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap.get() ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.displayString = I18n.format(ClientModManager.canBoostFlying() ? (ClientSettings.flySpeedBoost.get() == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost.get() + 1) + "x") : "gui.unavailable");
		btnAllDirs.displayString = I18n.format(ClientModManager.canRunInAllDirs() ? (ClientSettings.enableAllDirs.get() ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.displayString = I18n.format(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.displayString = I18n.format(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	private void onBindingClicked(KeyBinding binding){
		selectedBinding = binding;
	}
	
	private void onButtonClicked(GuiButton btn){
		switch(btn.id){
			case idControls:
				mc.displayGuiScreen(new GuiControls(this, mc.gameSettings));
				break;
				
			case idAutoJump:
				mc.gameSettings.autoJump = !mc.gameSettings.autoJump;
				mc.gameSettings.saveOptions();
				break;
				
			case idDisableMod:
				if (ClientModManager.inMenu()){
					BetterSprintingMod.config.update(ClientSettings.disableMod, value -> !value);
					initGui();
				}
				
				break;
				
			case idFlyBoost:
				if (ClientModManager.canBoostFlying()){
					BetterSprintingMod.config.update(ClientSettings.flySpeedBoost, value -> (value + 1) % 8);
				}
				
				break;
				
			case idAllDirs:
				if (ClientModManager.canRunInAllDirs()){
					BetterSprintingMod.config.update(ClientSettings.enableAllDirs, value -> !value);
				}
				
				break;
				
			case idDoubleTap:
				if (!ClientSettings.disableMod.get()){
					BetterSprintingMod.config.update(ClientSettings.enableDoubleTap, value -> !value);
				}
				
				break;
				
			case idDone:
				mc.displayGuiScreen(parentScreen);
				break;
				
			default:
				return;
		}
		
		BetterSprintingMod.config.save();
		updateButtons();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if (super.mouseClicked(mouseX, mouseY, button)){
			return true;
		}
		else if (selectedBinding != null){
			selectedBinding.bind(InputMappings.Type.MOUSE.getOrMakeInput(button));
			selectedBinding = null;
			
			onKeyBindingUpdated();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers){
		if (selectedBinding != null){
			if (keyCode == GLFW.GLFW_KEY_ESCAPE){
				selectedBinding.setKeyModifierAndCode(KeyModifier.NONE, InputMappings.INPUT_INVALID);
			}
			else{
				selectedBinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputMappings.getInputByCode(keyCode, scanCode));
			}
			
			if (!KeyModifier.isKeyCodeModifier(selectedBinding.getKey())){
				selectedBinding = null;
			}
			
			onKeyBindingUpdated();
			return true;
		}
		else{
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers){
		if (selectedBinding != null){
			selectedBinding.setKeyModifierAndCode(KeyModifier.NONE, InputMappings.getInputByCode(keyCode, scanCode));
			selectedBinding = null;
			
			onKeyBindingUpdated();
			return true;
		}
		else{
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
	}
	
	private void onKeyBindingUpdated(){
		BetterSprintingMod.config.set(ClientSettings.keyCodeSprintHold, ClientModManager.keyBindSprintHold.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeSprintToggle, ClientModManager.keyBindSprintToggle.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeSneakToggle, ClientModManager.keyBindSneakToggle.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeOptionsMenu, ClientModManager.keyBindOptionsMenu.getKey().getKeyCode());
		
		BetterSprintingMod.config.set(ClientSettings.keyModSprintHold, ClientModManager.keyBindSprintHold.getKeyModifier().name());
		BetterSprintingMod.config.set(ClientSettings.keyModSprintToggle, ClientModManager.keyBindSprintToggle.getKeyModifier().name());
		BetterSprintingMod.config.set(ClientSettings.keyModSneakToggle, ClientModManager.keyBindSneakToggle.getKeyModifier().name());
		BetterSprintingMod.config.set(ClientSettings.keyModOptionsMenu, ClientModManager.keyBindOptionsMenu.getKeyModifier().name());
		
		BetterSprintingMod.config.set(ClientSettings.keyTypeSprintHold, ClientModManager.keyBindSprintHold.getKey().getType().name());
		BetterSprintingMod.config.set(ClientSettings.keyTypeSprintToggle, ClientModManager.keyBindSprintToggle.getKey().getType().name());
		BetterSprintingMod.config.set(ClientSettings.keyTypeSneakToggle, ClientModManager.keyBindSneakToggle.getKey().getType().name());
		BetterSprintingMod.config.set(ClientSettings.keyTypeOptionsMenu, ClientModManager.keyBindOptionsMenu.getKey().getType().name());
		
		BetterSprintingMod.config.save();
		ClientSettings.updateKeyBindings();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		drawCenteredString(fontRenderer, "Better Sprinting", width / 2, 20, 16777215);
		
		super.render(mouseX, mouseY, partialTickTime);
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			KeyBinding binding = ClientModManager.keyBindings[a];
			
			boolean hasConflict = false;
			boolean hasOnlyModifierConflict = true;
			
			if (!binding.isInvalid()){
				for(KeyBinding other:mc.gameSettings.keyBindings){
					if (binding != other && binding.func_197983_b(other)){
						hasConflict = true;
						hasOnlyModifierConflict &= binding.hasKeyCodeModifierConflict(other);
					}
				}
			}
			
			if (binding == selectedBinding){
				buttons.get(a).displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + binding.func_197978_k() + TextFormatting.WHITE + " <";
			}
			else if (hasConflict){
				buttons.get(a).displayString = (hasOnlyModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + binding.func_197978_k();
			}
			else{
				buttons.get(a).displayString = binding.func_197978_k();
			}
			
			String desc = (binding == mc.gameSettings.keyBindSprint ? "bs.sprint.hold" : binding.getKeyDescription());
			drawButtonTitle(I18n.format(desc), buttons.get(a), a % 2 == 0 ? maxWidthLeft : maxWidthRight);
		}
	
		drawButtonTitle(I18n.format("bs.doubleTapping"), btnDoubleTap, maxWidthLeft);
		drawButtonTitle(I18n.format("bs.runAllDirs"), btnAllDirs, maxWidthRight);
		drawButtonTitle(I18n.format("bs.flyBoost"), btnFlyBoost, maxWidthLeft);
		drawButtonTitle(I18n.format("bs.disableMod"), btnDisableMod, maxWidthRight);
		drawButtonTitle(I18n.format("bs.autoJump"), btnAutoJump, maxWidthLeft);
		
		for(int a = 0, top = height / 6; a < buttons.size(); a++){
			if (buttons.get(a).isMouseOver()){
				String info = a < buttonTitles.length ? buttonTitles[a] : "";
				String[] spl = I18n.format(info).split("#");
				
				for(int line = 0; line < spl.length; line++){
					drawCenteredString(fontRenderer, spl[line], width / 2, top + 148 + (10 * line - (fontRenderer.FONT_HEIGHT * spl.length / 2)), -1);
				}
				
				break;
			}
		}
	}
	
	private void drawButtonTitle(String title, GuiButton btn, int maxWidth){
		int lines = fontRenderer.listFormattedStringToWidth(title, maxWidth).size();
		fontRenderer.drawSplitString(title, btn.x + 76, btn.y + 7 - 5 * (lines - 1), maxWidth, -1);
	}
}
