package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import chylex.bettersprinting.client.ClientModManager;

public class CustomMovementInput{
	public boolean held = false;
	public int stoptime = 0;
	public boolean sprint, sprintToggle, sneakToggle, hasToggledSprint, hasToggledSneak;
	
	public void update(Minecraft mc, MovementInputFromOptions options){
		options.moveStrafe = 0F;
		options.moveForward = 0F;
		
		GameSettings settings = mc.gameSettings;

		if (settings.keyBindForward.getIsKeyPressed())++options.moveForward;
		if (settings.keyBindBack.getIsKeyPressed())--options.moveForward;
		if (settings.keyBindLeft.getIsKeyPressed())++options.moveStrafe;
		if (settings.keyBindRight.getIsKeyPressed())--options.moveStrafe;
		
		sprint = ClientModManager.keyBindSprintHold.getIsKeyPressed();
		
		if (!sprint){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSprintToggle.getIsKeyPressed()){
				if (!hasToggledSprint){
					sprintToggle = !sprintToggle;
					hasToggledSprint = true;
				}
			}
			else hasToggledSprint = false;

			sprint = sprintToggle;
		}
		else sprintToggle = false;
		
		options.jump = settings.keyBindJump.getIsKeyPressed();
		options.sneak = settings.keyBindSneak.getIsKeyPressed();
		
		if (!options.sneak){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSneakToggle.getIsKeyPressed()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else hasToggledSneak = false;
			
			options.sneak = sneakToggle;
		}
		
		if (options.sneak){
			options.moveStrafe = options.moveStrafe*0.3F;
			options.moveForward = options.moveForward*0.3F;
		}
		
		if (ClientModManager.isModDisabled()){
			sneakToggle = sprintToggle = false;
		}
	}
}
