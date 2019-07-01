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
import static chylex.bettersprinting.client.input.SprintState.TOGGLED_WHILE_HOLDING_SPRINT_KEY;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getInstance();

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
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public void updateMovementInput(boolean slowMovement, boolean isSpectator){
		if (Feature.FLY_ON_GROUND.isTriggered()){
			player.onGround = false;
		}
		
		wasSneaking = movementInput.sneak;
		wasMovingForward = player.func_223110_ee();
		movementController.update(slowMovement, isSpectator);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public void updateSprinting(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || abilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(Effects.BLINDNESS);
		
		boolean isSprintHeld = ClientModManager.keyBindSprintHold.isKeyDown();
		boolean isNotSneaking = !(movementInput.sneak && !abilities.isFlying && !player.isSwimming());
		
		// Double tapping
		
		if (ClientSettings.enableDoubleTap.get() && (player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && player.func_223110_ee() && !sprinting.active() && enoughHunger && !isSprintBlocked){
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
		
		if (movementController.sprintToggle){
			if (!sprinting.toggled()){
				sprinting = isSprintHeld ? TOGGLED_WHILE_HOLDING_SPRINT_KEY : TOGGLED; // allow releasing sprint key
			}
		}
		else if (sprinting.toggled()){
			sprinting = INACTIVE;
		}
		
		if (isSprintHeld){
			if (sprinting != TAPPING_SPRINT_KEY && sprinting != TOGGLED_WHILE_HOLDING_SPRINT_KEY){
				sprinting = HOLDING_SPRINT_KEY; // reset locked sprint after tapping the sprint key
				movementController.sprintToggle = false;
			}
		}
		else if (sprinting == TOGGLED_WHILE_HOLDING_SPRINT_KEY){
			sprinting = TOGGLED;
		}
		else if (sprinting == TAPPING_SPRINT_KEY){
			sprinting = TAPPED_SPRINT_KEY;
		}
		else if (sprinting == HOLDING_SPRINT_KEY){
			sprinting = INACTIVE;
		}
		
		// Stop conditions
		
		if (sprinting.active()){
			boolean isSlow = Feature.RUN_IN_ALL_DIRS.isTriggered() ? !movementController.isMovingAnywhere() : !movementInput.func_223135_b();
			
			boolean isSlowOrHungry = isSlow || !enoughHunger;
			boolean stopRunning = isSlowOrHungry || player.collidedHorizontally || player.isInWater() && !player.canSwim();
			
			if (player.isSwimming()){
				if (!player.onGround && !movementInput.sneak && isSlowOrHungry || !player.isInWater()){
					sprinting = INACTIVE;
				}
			}
			else if (stopRunning){
				sprinting = INACTIVE;
			}
		}
		
		// Update state
		
		boolean shouldSprint = sprinting.active() && isNotSneaking && !isSprintBlocked; // TODO fixes https://bugs.mojang.com/browse/MC-99848 (adding blindness while sprinting does not stop the sprint)
		
		if (player.isSprinting() != shouldSprint){
			player.setSprinting(shouldSprint);
			
			if (!shouldSprint && sprinting == TAPPED_SPRINT_KEY){
				sprinting = INACTIVE;
			}
		}
		
		// Fly boost
		
		float flySpeedBase = 0.05F;
		int flySpeedBoostMultiplier = ClientSettings.flySpeedBoost.get();
		
		if (flySpeedBoostMultiplier > 0){
			if (Feature.FLY_BOOST.isTriggered()){
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
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public void updateFlight(){
		if (player.onGround && abilities.isFlying && !mc.playerController.isSpectatorMode() && !Feature.FLY_ON_GROUND.isTriggered()){
			abilities.isFlying = false;
			player.sendPlayerAbilities();
		}
	}
}
