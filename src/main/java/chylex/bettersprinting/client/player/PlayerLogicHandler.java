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

public class PlayerLogicHandler{
	public static boolean showDisableWarningWhenPossible;
	
	private final Minecraft mc;
	private final CustomMovementInput customMovementInput;
	private EntityPlayerSP player;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	private boolean shouldRestoreSneakToggle;
	
	public PlayerLogicHandler(){
		mc = Minecraft.getMinecraft();
		customMovementInput = new CustomMovementInput();
	}
	
	public void setPlayer(EntityPlayerSP player){
		this.player = player;
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.11.2
	public void updateMovementInput(){
		wasMovingForward = player.movementInput.field_192832_b >= 0.8F;
		wasSneaking = player.movementInput.sneak;
		customMovementInput.update(mc, (MovementInputFromOptions)player.movementInput);
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.11.2
	public void updateLiving(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || player.capabilities.allowFlying;
		
		if (ClientModManager.isModDisabled()){
			if (player.onGround && !wasSneaking && !wasMovingForward && player.movementInput.field_192832_b >= 0.8F && !player.isSprinting() && enoughHunger && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS)){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprintHold.isKeyDown()){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
				}
			}

			if (!player.isSprinting() && player.movementInput.field_192832_b >= 0.8F && enoughHunger && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS) && ClientModManager.keyBindSprintHold.isKeyDown()){
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
			
			if (((dblTap && !player.isSprinting()) || !dblTap) && player.onGround && enoughHunger && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS)){
				player.setSprinting(sprint);
			}
			
			customMovementInput.held = sprint;

			if (dblTap && !customMovementInput.held && customMovementInput.stoptime == 0 && player.onGround && !wasSneaking && !wasMovingForward && player.movementInput.field_192832_b >= 0.8F && !player.isSprinting() && enoughHunger && !player.isHandActive() && !player.isPotionActive(MobEffects.BLINDNESS)){
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
				if (sprint && player.capabilities.isFlying && ClientModManager.canBoostFlying(mc)){
					player.capabilities.setFlySpeed(0.05F*(1+ClientSettings.flySpeedBoost));
					
					if (player.movementInput.sneak){
						player.motionY -= 0.15D*ClientSettings.flySpeedBoost;
					}

					if (player.movementInput.jump){
						player.motionY += 0.15D*ClientSettings.flySpeedBoost;
					}
				}
				else player.capabilities.setFlySpeed(0.05F);
			}
			else if (player.capabilities.getFlySpeed() > 0.05F)player.capabilities.setFlySpeed(0.05F);
		}

		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
		
		if (player.isSprinting() && player.isSneaking() && !player.capabilities.isFlying){
			player.setSprinting(false);
		}

		if (player.isSprinting() && (player.movementInput.field_192832_b < 0.8F || player.isCollidedHorizontally || !enoughHunger)){
			if ((ClientModManager.canRunInAllDirs(mc) && ClientSettings.enableAllDirs) == false || (player.movementInput.field_192832_b == 0F && player.movementInput.moveStrafe == 0F)){
				player.setSprinting(false);
			}
		}
		
		if (showDisableWarningWhenPossible){
			player.sendMessage(new TextComponentString(ClientModManager.chatPrefix+I18n.format(ClientModManager.isModDisabledByServer() ? "bs.game.disabled" : "bs.game.reenabled")));
			showDisableWarningWhenPossible = false;
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
