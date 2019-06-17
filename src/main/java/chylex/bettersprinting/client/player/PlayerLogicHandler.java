package chylex.bettersprinting.client.player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovementInput;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import chylex.bettersprinting.client.gui.GuiSprint;

final class PlayerLogicHandler{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private final EntityPlayerSP player;
	private final MovementInput movementInput;
	private final MovementController movementController;
	
	private boolean wasMovingForward;
	private boolean wasSneaking;
	
	private boolean isHeld = false;
	private int stopTimer = 0;
	
	public PlayerLogicHandler(EntityPlayerSP player){
		this.player = player;
		this.movementInput = player.movementInput;
		this.movementController = new MovementController(movementInput);
	}
	
	public EntityPlayerSP getPlayer(){
		return player;
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.8.9
	public void updateMovementInput(){
		wasSneaking = movementInput.sneak;
		wasMovingForward = movementInput.moveForward >= 0.8F;
		movementController.update();
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.8.9
	public void updateLiving(){
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || player.capabilities.allowFlying;
		boolean isSprintBlocked = player.isUsingItem() || player.isPotionActive(Potion.blindness);
		
		if (ClientModManager.isModDisabled()){
			if (player.onGround && !wasSneaking && !wasMovingForward && movementInput.moveForward >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprintHold.isKeyDown()){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
				}
			}

			if (!player.isSprinting() && movementInput.moveForward >= 0.8F && enoughHunger && !isSprintBlocked && ClientModManager.keyBindSprintHold.isKeyDown()){
				player.setSprinting(true);
			}
		}
		else{
			boolean prevHeld = isHeld;
			boolean sprint = movementController.sprint && !(movementInput.sneak && !player.capabilities.isFlying);
			boolean dblTap = ClientSettings.enableDoubleTap;

			if ((!dblTap || !player.isSprinting()) && player.onGround && enoughHunger && !isSprintBlocked){
				player.setSprinting(sprint);
			}
			
			isHeld = sprint;

			if (dblTap && !isHeld && stopTimer == 0 && player.onGround && !wasSneaking && !wasMovingForward && movementInput.moveForward >= 0.8F && !player.isSprinting() && enoughHunger && !isSprintBlocked){
				if (player.sprintToggleTimer == 0){
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

			if (ClientSettings.flySpeedBoost > 0){
				if (sprint && player.capabilities.isFlying && ClientModManager.canBoostFlying()){
					player.capabilities.setFlySpeed(0.05F + 0.075F * ClientSettings.flySpeedBoost);
				}
				else{
					player.capabilities.setFlySpeed(0.05F);
				}
			}
			else if (player.capabilities.getFlySpeed() > 0.05F){
				player.capabilities.setFlySpeed(0.05F);
			}
		}
		
		if (player.isSprinting()){
			boolean isSlow = (ClientModManager.canRunInAllDirs() && ClientSettings.enableAllDirs) ? !movementController.isMovingAnywhere() : movementInput.moveForward < 0.8F;
			
			if (isSlow || !enoughHunger || player.isCollidedHorizontally){
				player.setSprinting(false);
			}
		}
	
		postLogic();
	}
	
	private void postLogic(){
		if (ClientModManager.showDisableWarningWhenPossible){
			player.addChatMessage(new ChatComponentText(ClientModManager.chatPrefix + I18n.format(ClientModManager.isModDisabledByServer() ? "bs.game.disabled" : "bs.game.reenabled")));
			ClientModManager.showDisableWarningWhenPossible = false;
		}
		
		if (ClientModManager.keyBindOptionsMenu.isKeyDown()){
			mc.displayGuiScreen(new GuiSprint(null));
		}
	}
}
