package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingMod;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class GuiSprint extends Screen{
	private static final int idDoubleTap = 199;
	private static final int idAllDirs = 198;
	private static final int idFlyBoost = 197;
	private static final int idFlyOnGround = 196;
	private static final int idDisableMod = 195;
	private static final int idAutoJump = 194;
	
	private final Minecraft mc = Minecraft.getInstance();
	private final Screen parentScreen;
	
	private GuiButtonInputOption btnDoubleTap, btnAutoJump, btnFlyBoost, btnFlyOnGround, btnAllDirs, btnDisableMod;
	private GuiButtonInputBinding selectedBinding;
	
	public GuiSprint(Screen parentScreen){
		super(new StringTextComponent("Better Sprinting"));
		this.parentScreen = parentScreen;
	}
	
	@Override
	protected void init(){
		buttons.clear();
		
		int left = (width / 2) - 155;
		int top = height / 6;
		
		for(int a = 0; a < ClientModManager.keyBindings.length; a++){
			KeyBinding binding = ClientModManager.keyBindings[a];
			Button btn = addButton(new GuiButtonInputBinding(left + 160 * (a % 2), top + 24 * (a / 2), binding, this::onBindingClicked));
			
			if ((binding == ClientModManager.keyBindSprintToggle || binding == ClientModManager.keyBindSneakToggle) && ClientModManager.isModDisabled()){
				btn.active = false;
			}
		}
		
		btnDoubleTap = addButton(new GuiButtonInputOption(idDoubleTap, left, top + 60, "bs.doubleTapping", this::onButtonClicked));
		btnAllDirs = addButton(new GuiButtonInputOption(idAllDirs, left + 160, top + 60, "bs.runAllDirs", this::onButtonClicked));
		btnFlyBoost = addButton(new GuiButtonInputOption(idFlyBoost, left, top + 84, "bs.flyBoost", this::onButtonClicked));
		btnFlyOnGround = addButton(new GuiButtonInputOption(idFlyOnGround, left + 160, top + 84, "bs.flyOnGround", this::onButtonClicked));
		btnDisableMod = addButton(new GuiButtonInputOption(idDisableMod, left + 160, top + 108, "bs.disableMod", this::onButtonClicked));
		btnAutoJump = addButton(new GuiButtonInputOption(idAutoJump, left, top + 108, "bs.autoJump", this::onButtonClicked));
		
		btnDoubleTap.active = !ClientModManager.isModDisabled();
		btnAllDirs.active = ClientModManager.canRunInAllDirs();
		btnFlyBoost.active = ClientModManager.canBoostFlying();
		btnFlyOnGround.active = ClientModManager.canFlyOnGround();
		btnDisableMod.active = ClientModManager.canEnableMod();
		
		addButton(new Button((width / 2) - 100, top + 168, parentScreen == null ? 98 : 200, 20, I18n.format("gui.done"), this::onClickedDone));
		
		if (parentScreen == null){
			addButton(new Button((width / 2) + 2, top + 168, 98, 20, I18n.format("options.controls"), this::onClickedControls));
		}
		
		updateButtons();
	}
	
	private void updateButtons(){
		btnDoubleTap.setTitleKey(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap.get() ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.setTitleKey(ClientModManager.canBoostFlying() ? (ClientSettings.flySpeedBoost.get() == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost.get() + 1) + "x") : "gui.unavailable");
		btnFlyOnGround.setTitleKey(ClientModManager.canFlyOnGround() ? (ClientSettings.flyOnGround.get() ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnAllDirs.setTitleKey(ClientModManager.canRunInAllDirs() ? (ClientSettings.enableAllDirs.get() ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.setTitleKey(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.setTitleKey(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	private void onClickedControls(@SuppressWarnings("unused") Button button){
		mc.displayGuiScreen(new ControlsScreen(this, mc.gameSettings));
		BetterSprintingMod.config.save();
	}
	
	private void onClickedDone(@SuppressWarnings("unused") Button button){
		mc.displayGuiScreen(parentScreen);
		BetterSprintingMod.config.save();
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
				if (ClientModManager.canEnableMod()){
					BetterSprintingMod.config.update(ClientSettings.disableMod, value -> !value);
					init();
				} break;
				
			case idFlyBoost:
				if (ClientModManager.canBoostFlying()){
					BetterSprintingMod.config.update(ClientSettings.flySpeedBoost, value -> (value + 1) % 8);
				} break;
			
			case idFlyOnGround:
				if (ClientModManager.canFlyOnGround()){
					BetterSprintingMod.config.update(ClientSettings.flyOnGround, value -> !value);
				} break;
				
			case idAllDirs:
				if (ClientModManager.canRunInAllDirs()){
					BetterSprintingMod.config.update(ClientSettings.enableAllDirs, value -> !value);
				} break;
				
			case idDoubleTap:
				if (!ClientSettings.disableMod.get()){
					BetterSprintingMod.config.update(ClientSettings.enableDoubleTap, value -> !value);
				} break;
				
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
			selectedBinding.setBinding(InputMappings.Type.MOUSE.getOrMakeInput(button));
			onSelectedBindingUpdated();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers){
		if (selectedBinding != null){
			if (keyCode == GLFW.GLFW_KEY_ESCAPE){
				selectedBinding.setBinding(KeyModifier.NONE, InputMappings.INPUT_INVALID);
			}
			else{
				selectedBinding.setBinding(KeyModifier.getActiveModifier(), InputMappings.getInputByCode(keyCode, scanCode));
			}
			
			onSelectedBindingUpdated();
			return true;
		}
		else{
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
	
	@Override
	public boolean func_223281_a_(int keyCode, int scanCode, int modifiers){
		if (selectedBinding != null){
			selectedBinding.setBinding(KeyModifier.NONE, InputMappings.getInputByCode(keyCode, scanCode));
			onSelectedBindingUpdated();
			return true;
		}
		else{
			return super.func_223281_a_(keyCode, scanCode, modifiers);
		}
	}
	
	private void onSelectedBindingUpdated(){
		if (!selectedBinding.isSelected()){
			selectedBinding = null;
		}
		
		BetterSprintingMod.config.set(ClientSettings.keyCodeSprintHold, ClientModManager.keyBindSprintHold.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeSprintToggle, ClientModManager.keyBindSprintToggle.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeSneakToggle, ClientModManager.keyBindSneakToggle.getKey().getKeyCode());
		BetterSprintingMod.config.set(ClientSettings.keyCodeOptionsMenu, ClientModManager.keyBindOptionsMenu.getKey().getKeyCode());
		
		BetterSprintingMod.config.set(ClientSettings.keyModSprintHold, ClientModManager.keyBindSprintHold.getKeyModifier());
		BetterSprintingMod.config.set(ClientSettings.keyModSprintToggle, ClientModManager.keyBindSprintToggle.getKeyModifier());
		BetterSprintingMod.config.set(ClientSettings.keyModSneakToggle, ClientModManager.keyBindSneakToggle.getKeyModifier());
		BetterSprintingMod.config.set(ClientSettings.keyModOptionsMenu, ClientModManager.keyBindOptionsMenu.getKeyModifier());
		
		BetterSprintingMod.config.set(ClientSettings.keyTypeSprintHold, ClientModManager.keyBindSprintHold.getKey().getType());
		BetterSprintingMod.config.set(ClientSettings.keyTypeSprintToggle, ClientModManager.keyBindSprintToggle.getKey().getType());
		BetterSprintingMod.config.set(ClientSettings.keyTypeSneakToggle, ClientModManager.keyBindSneakToggle.getKey().getType());
		BetterSprintingMod.config.set(ClientSettings.keyTypeOptionsMenu, ClientModManager.keyBindOptionsMenu.getKey().getType());
		
		BetterSprintingMod.config.save();
		ClientSettings.updateKeyBindings();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTickTime){
		final int top = height / 6;
		final int middle = width / 2;
		
		renderBackground();
		drawCenteredString(font, "Better Sprinting", middle, 20, 16777215);
		
		super.render(mouseX, mouseY, partialTickTime);
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(Widget button:buttons){
			if (button instanceof GuiButtonCustomInput){
				drawButtonTitle(((GuiButtonCustomInput)button).getTitle(), button, button.x < middle ? maxWidthLeft : maxWidthRight);
				
				if (button.isMouseOver(mouseX, mouseY)){
					String[] spl = ((GuiButtonCustomInput)button).getInfo();
					
					for(int line = 0; line < spl.length; line++){
						drawCenteredString(font, spl[line], middle, top + 148 + (10 * line - (font.FONT_HEIGHT * spl.length / 2)), -1);
					}
				}
			}
		}
	}
	
	private void drawButtonTitle(String title, Widget btn, int maxWidth){
		int lines = font.listFormattedStringToWidth(title, maxWidth).size();
		font.drawSplitString(title, btn.x + 76, btn.y + 7 - 5 * (lines - 1), maxWidth, -1);
	}
}
