package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import chylex.bettersprinting.client.ClientModManager;

public class CustomMovementInput{
	public boolean held = false;
	public int stoptime = 0;
	public boolean sprint, sprintToggle, sneakToggle, hasToggledSprint, hasToggledSneak;
	
	// UPDATE | MovementInputFromOptions.updatePlayerMoveState | 1.12
	public void update(Minecraft mc, MovementInput $this){
		GameSettings settings = mc.gameSettings;
		
		// VANILLA
		$this.moveStrafe = 0F;
		$this.field_192832_b = 0F;

		if (settings.keyBindForward.isKeyDown()){
			++$this.field_192832_b;
			$this.forwardKeyDown = true;
		}
		else{
			$this.forwardKeyDown = false;
		}
		
		if (settings.keyBindBack.isKeyDown()){
			--$this.field_192832_b;
			$this.backKeyDown = true;
		}
		else{
			$this.backKeyDown = false;
		}
		
		if (settings.keyBindLeft.isKeyDown()){
			++$this.moveStrafe;
			$this.leftKeyDown = true;
		}
		else{
			$this.leftKeyDown = false;
		}
		
		if (settings.keyBindRight.isKeyDown()){
			--$this.moveStrafe;
			$this.rightKeyDown = true;
		}
		else{
			$this.rightKeyDown = false;
		}
		
		// CUSTOM
		sprint = ClientModManager.keyBindSprintHold.isKeyDown();
		
		if (!sprint){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSprintToggle.isKeyDown()){
				if (!hasToggledSprint){
					sprintToggle = !sprintToggle;
					hasToggledSprint = true;
				}
			}
			else{
				hasToggledSprint = false;
			}
			
			sprint = sprintToggle;
		}
		else{
			sprintToggle = false;
		}
		
		// VANILLA
		$this.jump = settings.keyBindJump.isKeyDown();
		$this.sneak = settings.keyBindSneak.isKeyDown();
		
		// CUSTOM
		if (!$this.sneak){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSneakToggle.isKeyDown()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else{
				hasToggledSneak = false;
			}
			
			$this.sneak = sneakToggle;
		}
		
		// VANILLA
		if ($this.sneak){
			$this.moveStrafe = $this.moveStrafe*0.3F;
			$this.field_192832_b = $this.field_192832_b*0.3F;
		}
		
		// CUSTOM
		if (ClientModManager.isModDisabled()){
			sneakToggle = sprintToggle = false;
		}
	}
}
