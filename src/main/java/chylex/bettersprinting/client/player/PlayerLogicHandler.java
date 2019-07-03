package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientModManager.Feature;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.input.SprintState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import static chylex.bettersprinting.client.input.SprintState.DOUBLE_TAPPED_FORWARD;
import static chylex.bettersprinting.client.input.SprintState.HOLDING_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.INACTIVE;
import static chylex.bettersprinting.client.input.SprintState.TAPPED_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.TAPPING_SPRINT_KEY;
import static chylex.bettersprinting.client.input.SprintState.TOGGLED;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getInstance();

	private final EntityPlayerSP player;
	private final PlayerCapabilities abilities;
	private final MovementInput movementInput;
	private final MovementController movementController;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	
	private SprintState sprinting;
	
	public PlayerLogicHandler(EntityPlayerSP player){
		this.player = player;
		this.abilities = player.abilities;
		this.movementInput = player.movementInput;
		this.movementController = new MovementController(movementInput);
		
		this.sprinting = player.isSprinting() ? DOUBLE_TAPPED_FORWARD : INACTIVE;
	}
	
	public EntityPlayerSP getPlayer(){
		return player;
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public void updateMovementInput(){
		if (Feature.FLY_ON_GROUND.isTriggered()){
			player.onGround = false;
		}
		
		wasSneaking = movementInput.sneak;
		wasMovingForward = movementController.isMovingFastForward();
		movementController.update();
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public void updateSprinting(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || abilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(MobEffects.BLINDNESS);
		
		boolean isSprintHeld = ClientModManager.keyBindSprintHold.isKeyDown();
		boolean isNotSneaking = !(movementInput.sneak && !abilities.isFlying && !player.isSwimming());
		
		// Double tapping
		
		if (ClientSettings.enableDoubleTap.get() && (player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && movementController.isMovingFastForward() && !sprinting.active() && enoughHunger && !isSprintBlocked){
			if (player.sprintToggleTimer <= 0 && !isSprintHeld){
				player.sprintToggleTimer = 7;
			}
			else{
				sprinting = DOUBLE_TAPPED_FORWARD;
			}
		}
		
		// Sprint key
		
		if (!sprinting.active() && (!player.isInWater() || player.canSwim()) && movementController.isMovingFastForward() && enoughHunger && !isSprintBlocked && isSprintHeld){
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
			boolean isSlow = Feature.RUN_IN_ALL_DIRS.isTriggered() ? !movementController.isMovingAnywhere() : !movementController.isMovingFastForward();
			
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
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public void updateFlight(){
		if (player.onGround && abilities.isFlying && !mc.playerController.isSpectatorMode() && !Feature.FLY_ON_GROUND.isTriggered()){
			abilities.isFlying = false;
			player.sendPlayerAbilities();
		}
	}
}
