package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import chylex.bettersprinting.client.ClientModManager;

public class CustomMovementInput{
	public boolean held = false;
	public int stoptime = 0;
	public boolean sprint, sprintToggle, sneakToggle, hasToggledSprint, hasToggledSneak;
	
	// UPDATE | MovementInputFromOptions.updatePlayerMoveState | 1.11.2
	public void update(Minecraft mc, MovementInputFromOptions $this){
		GameSettings settings = mc.gameSettings;
		
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
		
		// custom handling
		sprint = ClientModManager.keyBindSprintHold.isKeyDown();
		
		if (!sprint){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSprintToggle.isKeyDown()){
				if (!hasToggledSprint){
					sprintToggle = !sprintToggle;
					hasToggledSprint = true;
				}
			}
			else hasToggledSprint = false;

			sprint = sprintToggle;
		}
		else sprintToggle = false;
		
		$this.jump = settings.keyBindJump.isKeyDown();
		$this.sneak = settings.keyBindSneak.isKeyDown();
		
		if (!$this.sneak){
			if (!ClientModManager.isModDisabled() && ClientModManager.keyBindSneakToggle.isKeyDown()){
				if (!hasToggledSneak){
					sneakToggle = !sneakToggle;
					hasToggledSneak = true;
				}
			}
			else hasToggledSneak = false;
			
			$this.sneak = sneakToggle;
		}
		
		if ($this.sneak){
			$this.moveStrafe = $this.moveStrafe*0.3F;
			$this.field_192832_b = $this.field_192832_b*0.3F;
		}
		
		if (ClientModManager.isModDisabled()){
			sneakToggle = sprintToggle = false;
		}
	}
}
