package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getInstance();

	private final ClientPlayerEntity player;
	private final PlayerAbilities abilities;
	private final MovementInput movementInput;
	private final MovementController movementController;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	
	private boolean isHeld = false;
	private int stopTimer = 0;
	
	public PlayerLogicHandler(ClientPlayerEntity player){
		this.player = player;
		this.abilities = player.abilities;
		this.movementInput = player.movementInput;
		this.movementController = new MovementController(movementInput);
	}
	
	public ClientPlayerEntity getPlayer(){
		return player;
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public void updateMovementInput(boolean slowMovement, boolean isSpectator){
		if (mc.playerController.isInCreativeMode() && abilities.isFlying && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get()){
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
		
		boolean prevHeld = isHeld;
		boolean sprint = movementController.sprint && !(movementInput.sneak && !abilities.isFlying);
		boolean dblTap = ClientSettings.enableDoubleTap.get();
		
		if ((!dblTap || !player.isSprinting()) && (player.onGround || player.canSwim()) && enoughHunger && !isSprintBlocked){
			player.setSprinting(sprint);
		}
		
		isHeld = sprint;
		
		if (dblTap && !isHeld && stopTimer == 0 && (player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && player.func_223110_ee() && !player.isSprinting() && enoughHunger && !isSprintBlocked){
			if (player.sprintToggleTimer <= 0){
				player.sprintToggleTimer = 7;
			}
			else{
				player.setSprinting(true);
				player.sprintToggleTimer = 0;
			}
		}
		
		if (dblTap){
			if (prevHeld && !isHeld){
				stopTimer = 1;
			}
			
			if (stopTimer > 0){
				stopTimer--;
				player.setSprinting(false);
			}
		}
		
		int flySpeedBoostMultiplier = ClientSettings.flySpeedBoost.get();
		
		if (flySpeedBoostMultiplier > 0){
			if (sprint && abilities.isFlying && ClientModManager.canBoostFlying()){
				abilities.setFlySpeed(0.05F + 0.075F * flySpeedBoostMultiplier);
			}
			else{
				abilities.setFlySpeed(0.05F);
			}
		}
		else if (abilities.getFlySpeed() > 0.05F){
			abilities.setFlySpeed(0.05F);
		}
		
		if (player.isSprinting()){
			boolean isSlow = (ClientModManager.canRunInAllDirs() && ClientSettings.enableAllDirs.get()) ? !movementController.isMovingAnywhere() : !movementInput.func_223135_b();
			
			boolean isSlowOrHungry = isSlow || !enoughHunger;
			boolean stopRunning = isSlowOrHungry || player.collidedHorizontally || player.isInWater() && !player.canSwim();
			
			if (player.isSwimming()){
				if (!player.onGround && !movementInput.sneak && isSlowOrHungry || !player.isInWater()){
					player.setSprinting(false);
				}
			}
			else if (stopRunning){
				player.setSprinting(false);
			}
		}
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public void updateFlight(){
		if (player.onGround && abilities.isFlying && !mc.playerController.isSpectatorMode()){
			boolean shouldFlyOnGround = mc.playerController.isInCreativeMode() && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get();
			
			if (!shouldFlyOnGround){
				abilities.isFlying = false;
				player.sendPlayerAbilities();
			}
		}
	}
}
