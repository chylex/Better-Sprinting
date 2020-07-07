package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientModManager.Feature;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.input.SprintState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;
import static chylex.bettersprinting.client.input.SprintState.DOUBLE_TAPPED_FORWARD;
import static chylex.bettersprinting.client.input.SprintState.HOLDING_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.INACTIVE;
import static chylex.bettersprinting.client.input.SprintState.TAPPED_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.TAPPING_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.TOGGLED;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static final float flySpeedBase = 0.05F;

	private final ClientPlayerEntity player;
	private final PlayerAbilities abilities;
	private final MovementInput movementInput;
	private final MovementController movementController;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	
	private SprintState sprinting;
	
	public PlayerLogicHandler(ClientPlayerEntity player){
		this.player = player;
		this.abilities = player.abilities;
		this.movementInput = player.movementInput;
		this.movementController = new MovementController(movementInput);
		
		this.sprinting = player.isSprinting() ? DOUBLE_TAPPED_FORWARD : INACTIVE;
	}
	
	public ClientPlayerEntity getPlayer(){
		return player;
	}
	
	public void resetState(){
		abilities.setFlySpeed(flySpeedBase);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.16.1
	public void updateMovementInput(boolean slowMovement){
		if (Feature.FLY_ON_GROUND.isEnabled()){
			player.func_230245_c_(false); // RENAME onGround
		}
		
		wasSneaking = movementInput.sneaking;
		wasMovingForward = player.func_223110_ee();
		movementController.update(slowMovement);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.16.1
	public void updateSprinting(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || abilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(Effects.BLINDNESS);
		
		boolean isSprintHeld = ClientModManager.keyBindSprintHold.isKeyDown();
		boolean isNotSneaking = !(movementInput.sneaking && !abilities.isFlying && !player.isSwimming());
		
		// Double tapping
		
		if (movementInput.sneaking){
			player.sprintToggleTimer = 0;
		}
		
		if (ClientSettings.enableDoubleTap.get() && (player.func_233570_aj_() /* RENAME onGround */ || player.canSwim()) && !wasSneaking && !wasMovingForward && player.func_223110_ee() && !sprinting.active() && enoughHunger && !isSprintBlocked){
			if (player.sprintToggleTimer <= 0 && !isSprintHeld){
				player.sprintToggleTimer = 7;
			}
			else{
				sprinting = DOUBLE_TAPPED_FORWARD;
			}
		}
		
		// Sprint key
		
		if (!sprinting.active() && (!player.isInWater() || player.canSwim()) && player.func_223110_ee() && enoughHunger && !isSprintBlocked && isSprintHeld){
			sprinting = ClientSettings.sprintKeyMode.get().sprintState;
		}
		
		// Sprint state
		
		if (movementController.isSprintToggled()){
			sprinting = TOGGLED;
		}
		else if (sprinting == TOGGLED){
			sprinting = INACTIVE;
		}
		
		if (isSprintHeld){
			if (sprinting != TAPPING_SPRINT_KEY && sprinting != TOGGLED){
				sprinting = HOLDING_SPRINT_KEY;
			}
		}
		else if (sprinting == TAPPING_SPRINT_KEY){
			sprinting = TAPPED_SPRINT_KEY;
		}
		else if (sprinting == HOLDING_SPRINT_KEY){
			sprinting = INACTIVE;
		}
		
		// Stop conditions
		
		if (sprinting.active()){
			boolean isSlow = Feature.RUN_IN_ALL_DIRS.isEnabled() ? !movementController.isMovingAnywhere() : !movementInput.func_223135_b();
			
			boolean isSlowOrHungry = isSlow || !enoughHunger;
			boolean stopRunning = isSlowOrHungry || player.collidedHorizontally || player.isInWater() && !player.canSwim();
			
			if (player.isSwimming()){
				if (!player.func_233570_aj_() /* RENAME onGround */ && !movementInput.sneaking && isSlowOrHungry || !player.isInWater()){
					sprinting = INACTIVE;
				}
			}
			else if (stopRunning){
				sprinting = INACTIVE;
			}
		}
		
		// Update state
		
		boolean shouldSprint = sprinting.active() && isNotSneaking && !isSprintBlocked && !abilities.isFlying; // TODO fixes https://bugs.mojang.com/browse/MC-99848 (adding blindness while sprinting does not stop the sprint)
		
		if (player.isSprinting() != shouldSprint){
			player.setSprinting(shouldSprint);
			
			if (!shouldSprint && sprinting == TAPPED_SPRINT_KEY){
				sprinting = INACTIVE;
			}
		}
		
		// Fly boost
		
		int flySpeedBoostMultiplier = ClientSettings.flySpeedBoost.get();
		
		if (flySpeedBoostMultiplier > 0){
			if (Feature.FLY_BOOST.isEnabled() && (isSprintHeld || movementController.isSprintToggled())){
				abilities.setFlySpeed(flySpeedBase + 0.075F * flySpeedBoostMultiplier);
			}
			else{
				abilities.setFlySpeed(flySpeedBase);
			}
		}
		else if (abilities.getFlySpeed() > flySpeedBase){
			abilities.setFlySpeed(flySpeedBase);
		}
	}
	
	public boolean shouldPreventCancelingFlight(){
		return Feature.FLY_ON_GROUND.isEnabled();
	}
}
