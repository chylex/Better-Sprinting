package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.input.ToggleTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.MovementInput;

final class MovementController{
	private final Minecraft mc;
	private final MovementInput movementInput;
	
	private final ToggleTracker sprintToggle;
	private final ToggleTracker sneakToggle;
	private boolean restoreSneakToggle;
	
	public MovementController(MovementInput movementInput){
		this.mc = Minecraft.getInstance();
		this.movementInput = movementInput;
		
		this.sprintToggle = new ToggleTracker(ClientModManager.keyBindSprintToggle, ClientModManager.keyBindSprintHold);
		this.sneakToggle = new ToggleTracker(ClientModManager.keyBindSneakToggle, mc.gameSettings.keyBindSneak);
	}
	
	// UPDATE | Ensure replicated sneak modifications still match MovementInputFromOptions.updatePlayerMoveState | 1.13.2
	public void update(){
		sprintToggle.update();
		sneakToggle.update();
		
		if (movementInput.sneak && sneakToggle.isToggled && mc.currentScreen != null && !(mc.currentScreen instanceof GuiGameOver)){
			restoreSneakToggle = true;
			sneakToggle.isToggled = false;
		}
		
		if (restoreSneakToggle && mc.currentScreen == null){
			sneakToggle.isToggled = true;
			restoreSneakToggle = false;
		}
		
		movementInput.updatePlayerMoveState();
		
		if (!movementInput.sneak && sneakToggle.isToggled){
			movementInput.sneak = true;
			movementInput.moveStrafe *= 0.3F;
			movementInput.moveForward *= 0.3F;
		}
	}
	
	public boolean isSprintToggled(){
		return sprintToggle.isToggled;
	}
	
	public boolean isMovingFastForward(){
		return movementInput.moveForward >= 0.8F;
	}
	
	public boolean isMovingAnywhere(){
		return Math.abs(movementInput.moveForward) >= 1E-5F || Math.abs(movementInput.moveStrafe) >= 1E-5F;
	}
}
