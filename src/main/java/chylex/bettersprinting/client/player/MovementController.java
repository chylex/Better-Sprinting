package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.input.ToggleTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.MovementInput;

final class MovementController{
	private static final Minecraft mc = Minecraft.getInstance();
	
	private final MovementInput movementInput;
	private final ToggleTracker sprintToggle;
	private final ToggleTracker sneakToggle;
	private boolean restoreSneakToggle;
	
	public MovementController(MovementInput movementInput){
		this.movementInput = movementInput;
		this.sprintToggle = new ToggleTracker(ClientModManager.keyBindSprintToggle, ClientModManager.keyBindSprintHold);
		this.sneakToggle = new ToggleTracker(ClientModManager.keyBindSneakToggle, mc.gameSettings.field_228046_af_);
	}
	
	// UPDATE | Ensure first parameter of MovementInputFromOptions.func_217607_a still behaves like forced sneak | 1.15.1
	public void update(boolean slowMovement){
		sprintToggle.update();
		sneakToggle.update();
		
		if (movementInput.field_228350_h_ && sneakToggle.isToggled && mc.currentScreen != null && !(mc.currentScreen instanceof DeathScreen)){
			restoreSneakToggle = true;
			sneakToggle.isToggled = false;
		}
		
		if (restoreSneakToggle && mc.currentScreen == null){
			sneakToggle.isToggled = true;
			restoreSneakToggle = false;
		}
		
		movementInput.func_225607_a_(slowMovement || sneakToggle.isToggled);
		movementInput.field_228350_h_ |= sneakToggle.isToggled;
	}
	
	public boolean isSprintToggled(){
		return sprintToggle.isToggled;
	}
	
	public boolean isMovingAnywhere(){
		return Math.abs(movementInput.moveForward) >= 1E-5F || Math.abs(movementInput.moveStrafe) >= 1E-5F;
	}
}
