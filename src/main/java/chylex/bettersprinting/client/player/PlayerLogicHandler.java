package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.gui.GuiSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ForgeHooksClient;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getInstance();

	private final EntityPlayerSP player;
	private final MovementInputHandler customMovementInput;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	private boolean shouldRestoreSneakToggle;
	
	public PlayerLogicHandler(EntityPlayerSP player){
		this.player = player;
		this.customMovementInput = new MovementInputHandler();
	}
	
	public EntityPlayerSP getPlayer(){
		return player;
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public void updateMovementInput(){
		wasSneaking = player.movementInput.sneak;
		wasMovingForward = player.movementInput.moveForward >= 0.8F;
		customMovementInput.update(mc, player.movementInput);
		
		ForgeHooksClient.onInputUpdate(player, player.movementInput);
		mc.getTutorial().handleMovement(player.movementInput);
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public void updateLiving(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || player.abilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(MobEffects.BLINDNESS);
		
		if (ClientModManager.isModDisabled()){
			if ((player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && player.movementInput.moveForward >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprintHold.isKeyDown()){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
				}
			}

			if (!player.isSprinting() && (!player.isInWater() || player.canSwim()) && player.movementInput.moveForward >= 0.8F && enoughHunger && !isSprintBlocked && ClientModManager.keyBindSprintHold.isKeyDown()){
				player.setSprinting(true);
			}
		}
		else{
			updateSneakToggle();
			boolean prevHeld = customMovementInput.held;
			boolean sprint = customMovementInput.sprint;
			boolean dblTap = ClientSettings.enableDoubleTap.get();

			if (!player.abilities.isFlying && ((MovementInputFromOptions)player.movementInput).sneak){
				sprint = false;
			}
			
			if ((!dblTap || !player.isSprinting()) && (player.onGround || player.canSwim()) && enoughHunger && !isSprintBlocked){
				player.setSprinting(sprint);
			}
			
			customMovementInput.held = sprint;

			if (dblTap && !customMovementInput.held && customMovementInput.stoptime == 0 && (player.onGround || player.canSwim()) && !wasSneaking && !wasMovingForward && player.movementInput.moveForward >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer <= 0){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
					player.sprintToggleTimer = 0;
				}
			}
			
			if (dblTap){
				if (prevHeld && !customMovementInput.held){
					customMovementInput.stoptime = 1;
				}
				
				if (customMovementInput.stoptime > 0){
					customMovementInput.stoptime--;
					player.setSprinting(false);
				}
			}
			
			int flySpeedBoostMultiplier = ClientSettings.flySpeedBoost.get();
			
			if (flySpeedBoostMultiplier > 0){
				if (sprint && player.abilities.isFlying && ClientModManager.canBoostFlying()){
					player.abilities.setFlySpeed(0.05F + 0.075F * flySpeedBoostMultiplier);
				}
				else{
					player.abilities.setFlySpeed(0.05F);
				}
			}
			else if (player.abilities.getFlySpeed() > 0.05F){
				player.abilities.setFlySpeed(0.05F);
			}
		}

		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
		
		if (player.isSprinting() && player.isSneaking() && !player.abilities.isFlying){
			player.setSprinting(false);
		}
		
		if (player.isSprinting()){
			MovementInput movementInput = player.movementInput;
			boolean isSlow = movementInput.moveForward < 0.8F && ((ClientModManager.canRunInAllDirs() && ClientSettings.enableAllDirs.get()) == false || (movementInput.moveForward == 0F && movementInput.moveStrafe == 0F));
			
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
		
		if (ClientModManager.showDisableWarningWhenPossible){
			player.sendMessage(new TextComponentString(ClientModManager.chatPrefix + I18n.format(ClientModManager.isModDisabledByServer() ? "bs.game.disabled" : "bs.game.reenabled")));
			ClientModManager.showDisableWarningWhenPossible = false;
		}
	}
	
	private void updateSneakToggle(){
		if (mc.currentScreen != null && player != null && player.isSneaking()){
			if (customMovementInput.sneakToggle && !(mc.currentScreen instanceof GuiGameOver)){
				shouldRestoreSneakToggle = true;
				customMovementInput.sneakToggle = false;
			}
		}
		
		if (shouldRestoreSneakToggle && mc.currentScreen == null){
			customMovementInput.sneakToggle = true;
			shouldRestoreSneakToggle = false;
		}
	}
}
