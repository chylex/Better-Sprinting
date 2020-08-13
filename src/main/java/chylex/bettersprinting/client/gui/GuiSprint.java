package chylex.bettersprinting.client.gui;
import chylex.bettersprinting.BetterSprintingConfig;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientModManager.Feature;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.input.SprintKeyMode;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class GuiSprint extends Screen{
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static boolean openedControlsFromSprintMenu;
	
	private final Screen parentScreen;
	private Button btnSprintMode;
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
		
		for(int index = 0; index < ClientModManager.keyBindings.length; index++){
			addButton(new GuiButtonInputBinding(left + 160 * (index % 2), top + 24 * (index / 2), ClientModManager.keyBindings[index], this::onBindingClicked));
		}
		
		btnSprintMode = addButton(new GuiButton(left - 50, top, 48, "", onSettingClicked(() -> {
			BetterSprintingConfig.update(ClientSettings.sprintKeyMode, SprintKeyMode::next);
		})));
		
		btnDoubleTap = addButton(new GuiButtonInputOption(left, top + 60, "bs.doubleTapping", onSettingClicked(() -> {
			if (!ClientSettings.disableMod.get()){
				BetterSprintingConfig.update(ClientSettings.enableDoubleTap, value -> !value);
			}
		})));
		
		btnAllDirs = addButton(new GuiButtonInputOption(left + 160, top + 60, "bs.runAllDirs", onSettingClicked(() -> {
			if (Feature.RUN_IN_ALL_DIRS.isAvailable()){
				BetterSprintingConfig.update(ClientSettings.enableAllDirs, value -> !value);
			}
		})));
		
		btnFlyBoost = addButton(new GuiButtonInputOption(left, top + 84, "bs.flyBoost", onSettingClicked(() -> {
			if (Feature.FLY_BOOST.isAvailable()){
				BetterSprintingConfig.update(ClientSettings.flySpeedBoost, value -> (value + 1) % 8);
			}
		})));
		
		btnFlyOnGround = addButton(new GuiButtonInputOption(left + 160, top + 84, "bs.flyOnGround", onSettingClicked(() -> {
			if (Feature.FLY_ON_GROUND.isAvailable()){
				BetterSprintingConfig.update(ClientSettings.flyOnGround, value -> !value);
			}
		})));
		
		btnDisableMod = addButton(new GuiButtonInputOption(left + 160, top + 108, "bs.disableMod", onSettingClicked(() -> {
			if (ClientModManager.canManuallyEnableMod()){
				BetterSprintingConfig.update(ClientSettings.disableMod, value -> !value);
				updateButtonState();
			}
		})));
		
		btnAutoJump = addButton(new GuiButtonInputOption(left, top + 108, "bs.autoJump", onSettingClicked(() -> {
			mc.gameSettings.autoJump = !mc.gameSettings.autoJump;
			mc.gameSettings.saveOptions();
		})));
		
		addButton(new GuiButton((width / 2) - 100, top + 168, parentScreen == null ? 98 : 200, I18n.format("gui.done"), this::onClickedDone));
		
		if (parentScreen == null){
			addButton(new GuiButton((width / 2) + 2, top + 168, 98, I18n.format("options.controls"), this::onClickedControls));
		}
		
		updateButtonState();
		updateButtonText();
	}
	
	private void updateButtonState(){
		for(Widget button:buttons){
			if (button instanceof GuiButtonInputBinding){
				KeyBinding binding = ((GuiButtonInputBinding)button).binding;
				
				if (binding == ClientModManager.keyBindSprintToggle || binding == ClientModManager.keyBindSneakToggle){
					button.active = !ClientModManager.isModDisabled();
				}
			}
		}
		
		btnSprintMode.active = !ClientModManager.isModDisabled();
		btnDoubleTap.active = !ClientModManager.isModDisabled();
		btnAllDirs.active = Feature.RUN_IN_ALL_DIRS.isAvailable();
		btnFlyBoost.active = Feature.FLY_BOOST.isAvailable();
		btnFlyOnGround.active = Feature.FLY_ON_GROUND.isAvailable();
		btnDisableMod.active = ClientModManager.canManuallyEnableMod();
	}
	
	private void updateButtonText(){
		btnSprintMode.setMessage((ClientModManager.isModDisabled() ? SprintKeyMode.TAP : ClientSettings.sprintKeyMode.get()).translationKey);
		btnDoubleTap.setTitleKey(ClientModManager.isModDisabled() ? "gui.unavailable" : (ClientSettings.enableDoubleTap.get() ? "gui.enabled" : "gui.disabled"));
		btnFlyBoost.setTitleKey(Feature.FLY_BOOST.isAvailable() ? (ClientSettings.flySpeedBoost.get() == 0 ? "gui.disabled" : (ClientSettings.flySpeedBoost.get() + 1) + "x") : "gui.unavailable");
		btnFlyOnGround.setTitleKey(Feature.FLY_ON_GROUND.isAvailable() ? (ClientSettings.flyOnGround.get() ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnAllDirs.setTitleKey(Feature.RUN_IN_ALL_DIRS.isAvailable() ? (ClientSettings.enableAllDirs.get() ? "gui.enabled" : "gui.disabled") : "gui.unavailable");
		btnDisableMod.setTitleKey(ClientModManager.isModDisabled() ? "gui.yes" : "gui.no");
		btnAutoJump.setTitleKey(mc.gameSettings.autoJump ? "gui.yes" : "gui.no");
	}
	
	private void onClickedControls(){
		openedControlsFromSprintMenu = true;
		mc.displayGuiScreen(new ControlsScreen(this, mc.gameSettings));
		openedControlsFromSprintMenu = false;
		BetterSprintingConfig.save();
	}
	
	private void onClickedDone(){
		mc.displayGuiScreen(parentScreen);
		BetterSprintingConfig.save();
	}
	
	private Runnable onSettingClicked(Runnable callback){
		return () -> {
			callback.run();
			BetterSprintingConfig.save();
			updateButtonText();
		};
	}
	
	private void onBindingClicked(GuiButtonInputBinding binding){
		if (selectedBinding != null){
			selectedBinding.setSelected(false);
		}
		
		selectedBinding = binding;
		selectedBinding.setSelected(true);
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
	public boolean keyReleased(int keyCode, int scanCode, int modifiers){
		if (selectedBinding != null){
			selectedBinding.setBinding(KeyModifier.NONE, InputMappings.getInputByCode(keyCode, scanCode));
			onSelectedBindingUpdated();
			return true;
		}
		else{
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
	}
	
	private void onSelectedBindingUpdated(){
		if (!selectedBinding.isSelected()){
			selectedBinding = null;
		}
		
		for(Widget button:buttons){
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
		BetterSprintingConfig.save();
	}
	
	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTickTime){
		final int top = height / 6;
		final int middle = width / 2;
		
		renderBackground(matrix);
		drawCenteredString(matrix, font, title, middle, 20, 16777215);
		
		super.render(matrix, mouseX, mouseY, partialTickTime);
		
		final int maxWidthLeft = 82;
		final int maxWidthRight = 124;
		
		for(Widget button:buttons){
			if (button instanceof GuiButtonCustomInput){
				GuiButtonCustomInput input = (GuiButtonCustomInput)button;
				drawButtonTitle(input, input.x < middle ? maxWidthLeft : maxWidthRight);
				
				if (input.isMouseOver(mouseX, mouseY)){
					ITextComponent[] spl = input.getInfo();
					
					for(int line = 0; line < spl.length; line++){
						drawCenteredString(matrix, font, spl[line], middle, top + 148 + (10 * line - (font.FONT_HEIGHT * spl.length / 2)), -1);
					}
				}
			}
		}
	}
	
	private void drawButtonTitle(GuiButtonCustomInput btn, int maxWidth){
		ITextComponent title = btn.getTitle();
		int lines = font.func_238425_b_(title, maxWidth).size(); // RENAME listFormattedStringToWidth
		font.func_238418_a_(title, btn.x + 76, btn.y + 7 - 5 * (lines - 1), maxWidth, -1); // RENAME drawSplitString
	}
}
