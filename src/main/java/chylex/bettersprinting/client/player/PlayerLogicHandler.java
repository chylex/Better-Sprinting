package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.gui.GuiSprint;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getMinecraft();

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
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12
	public void updateMovementInput(){
		wasSneaking = player.movementInput.sneak;
		wasMovingForward = player.movementInput.field_192832_b >= 0.8F;
		customMovementInput.update(mc, player.movementInput);
		mc.func_193032_ao().func_193293_a(player.movementInput);
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12
	public void updateLiving(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || player.capabilities.allowFlying;
		boolean isSprintBlocked = player.isHandActive() || player.isPotionActive(MobEffects.BLINDNESS);
		
		if (ClientModManager.isModDisabled()){
			if (player.onGround && !wasSneaking && !wasMovingForward && player.movementInput.field_192832_b >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprintHold.isKeyDown()){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
				}
			}

			if (!player.isSprinting() && player.movementInput.field_192832_b >= 0.8F && enoughHunger && !isSprintBlocked && ClientModManager.keyBindSprintHold.isKeyDown()){
				player.setSprinting(true);
			}
		}
		else{
			updateSneakToggle();
			boolean prevHeld = customMovementInput.held;
			boolean sprint = customMovementInput.sprint;
			boolean dblTap = ClientSettings.enableDoubleTap;

			if (!player.capabilities.isFlying && ((MovementInputFromOptions)player.movementInput).sneak){
				sprint = false;
			}
			
			if (((dblTap && !player.isSprinting()) || !dblTap) && player.onGround && enoughHunger && !isSprintBlocked){
				player.setSprinting(sprint);
			}
			
			customMovementInput.held = sprint;

			if (dblTap && !customMovementInput.held && customMovementInput.stoptime == 0 && player.onGround && !wasSneaking && !wasMovingForward && player.movementInput.field_192832_b >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer == 0){
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
			
			if (ClientSettings.flySpeedBoost > 0){
				if (sprint && player.capabilities.isFlying && ClientModManager.canBoostFlying()){
					player.capabilities.setFlySpeed(0.05F*(1+ClientSettings.flySpeedBoost));
					
					if (player.movementInput.sneak){
						player.motionY -= 0.15D*ClientSettings.flySpeedBoost;
					}
					
					if (player.movementInput.jump){
						player.motionY += 0.15D*ClientSettings.flySpeedBoost;
					}
				}
				else{
					player.capabilities.setFlySpeed(0.05F);
				}
			}
			else if (player.capabilities.getFlySpeed() > 0.05F){
				player.capabilities.setFlySpeed(0.05F);
			}
		}

		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
		
		if (player.isSprinting() && player.isSneaking() && !player.capabilities.isFlying){
			player.setSprinting(false);
		}

		if (player.isSprinting() && (player.movementInput.field_192832_b < 0.8F || player.isCollidedHorizontally || !enoughHunger)){
			if ((ClientModManager.canRunInAllDirs() && ClientSettings.enableAllDirs) == false || (player.movementInput.field_192832_b == 0F && player.movementInput.moveStrafe == 0F)){
				player.setSprinting(false);
			}
		}
		
		if (ClientModManager.showDisableWarningWhenPossible){
			player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format(ClientModManager.isModDisabledByServer() ? "bs.game.disabled" : "bs.game.reenabled")));
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
