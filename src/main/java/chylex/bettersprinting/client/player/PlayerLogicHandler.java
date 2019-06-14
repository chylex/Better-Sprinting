package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.gui.GuiSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ForgeHooksClient;

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
		this.abilities = player.playerAbilities;
		this.movementInput = player.movementInput;
		this.movementController = new MovementController(movementInput);
	}
	
	public ClientPlayerEntity getPlayer(){
		return player;
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.2
	public void updateMovementInput(){
		wasSneaking = movementInput.sneak;
		wasMovingForward = player.func_223110_ee();
		movementController.update(player.func_213287_bg() || player.func_213300_bk(), player.isSpectator());
		
		ForgeHooksClient.onInputUpdate(player, movementInput);
		mc.getTutorial().handleMovement(movementInput);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.2
	public void updateLiving(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || abilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(Effects.field_76440_q);
		
		if (ClientModManager.isModDisabled()){
			if ((player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && player.func_223110_ee() && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprintHold.isKeyDown()){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
				}
			}
			
			if (!player.isSprinting() && (!player.isInWater() || player.canSwim()) && player.func_223110_ee() && enoughHunger && !isSprintBlocked && ClientModManager.keyBindSprintHold.isKeyDown()){
				player.setSprinting(true);
			}
		}
		else{
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
		
		postLogic();
	}
	
	private void postLogic(){
		if (ClientModManager.showDisableWarningWhenPossible){
			player.sendMessage(new StringTextComponent(ClientModManager.chatPrefix + I18n.format(ClientModManager.isModDisabledByServer() ? "bs.game.disabled" : "bs.game.reenabled")));
			ClientModManager.showDisableWarningWhenPossible = false;
		}
		
		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
	}
}
