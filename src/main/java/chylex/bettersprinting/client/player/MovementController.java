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
	
	public MovementController(final MovementInput movementInput){
		this.movementInput = movementInput;
		this.sprintToggle = new ToggleTracker(ClientModManager.keyBindSprintToggle, ClientModManager.keyBindSprintHold);
		this.sneakToggle = new ToggleTracker(ClientModManager.keyBindSneakToggle, mc.gameSettings.keyBindSneak);
	}
	
	// UPDATE | Ensure first parameter of MovementInputFromOptions.tickMovement still behaves like forced sneak | 1.16.2
	public void update(final boolean slowMovement){
		sprintToggle.update();
		sneakToggle.update();
		
		if (movementInput.sneaking && sneakToggle.isToggled && mc.currentScreen != null && !(mc.currentScreen instanceof DeathScreen)){
			restoreSneakToggle = true;
			sneakToggle.isToggled = false;
		}
		
		if (restoreSneakToggle && mc.currentScreen == null){
			sneakToggle.isToggled = true;
			restoreSneakToggle = false;
		}
		
		movementInput.tickMovement(slowMovement || sneakToggle.isToggled);
		movementInput.sneaking |= sneakToggle.isToggled;
	}
	
	public boolean isSprintToggled(){
		return sprintToggle.isToggled;
	}
	
	public boolean isMovingAnywhere(){
		return Math.abs(movementInput.moveForward) >= 1E-5F || Math.abs(movementInput.moveStrafe) >= 1E-5F;
	}
}
