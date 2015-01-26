package chylex.bettersprinting.client.player;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.gui.GuiSprint;

public class PlayerBase extends ClientPlayerBase{
	private static boolean shouldRestoreSneakToggle = false;
	private static String connectedServer = "";
	private static byte behaviorCheckTimer = 10;
	
	private Minecraft mc;
	private CustomMovementInput customMovementInput;
	
	public PlayerBase(ClientPlayerAPI api){
		super(api);
		mc=Minecraft.getMinecraft();
		customMovementInput=new CustomMovementInput();
	}
	
	@Override
	public void onLivingUpdate(){
		if (player.sprintingTicksLeft > 0 && --player.sprintingTicksLeft == 0)player.setSprinting(false);
		if (player.sprintToggleTimer > 0)--player.sprintToggleTimer;
		
		player.prevTimeInPortal=player.timeInPortal;
		
		if (player.inPortal){
			if (mc.currentScreen != null && !mc.currentScreen.doesGuiPauseGame())mc.displayGuiScreen(null);
			
			if (player.timeInPortal == 0F)mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"),player.getRNG().nextFloat()*0.4F+0.8F));
			
			player.timeInPortal += 0.0125F;

			if (player.timeInPortal >= 1F){
				player.timeInPortal = 1F;
			}
			
			player.inPortal = false;
		}
		else if (player.isPotionActive(Potion.confusion) && player.getActivePotionEffect(Potion.confusion).getDuration() > 60){
			player.timeInPortal += 0.006666667F;
			if (player.timeInPortal > 1F)player.timeInPortal = 1F;
		}
		else{
			if (player.timeInPortal > 0F)player.timeInPortal -= 0.05F;
			if (player.timeInPortal < 0F)player.timeInPortal = 0F;
		}
		
		if (player.timeUntilPortal > 0)--player.timeUntilPortal;
		
		float minSpeed = 0.8F;
		boolean wasJumping = player.movementInput.jump;
		boolean wasSneaking = player.movementInput.sneak;
		boolean isMovingForward = player.movementInput.moveForward >= minSpeed;
		customMovementInput.update(mc,(MovementInputFromOptions)player.movementInput); // CHANGED LINE

		if (player.isUsingItem() && !player.isRiding()){
			player.movementInput.moveStrafe *= 0.2F;
			player.movementInput.moveForward *= 0.2F;
			player.sprintToggleTimer = 0;
		}
		
		playerAPI.localPushOutOfBlocks(player.posX-player.width*0.35D,player.getEntityBoundingBox().minY+0.5D,player.posZ+player.width*0.35D);
		playerAPI.localPushOutOfBlocks(player.posX-player.width*0.35D,player.getEntityBoundingBox().minY+0.5D,player.posZ-player.width*0.35D);
		playerAPI.localPushOutOfBlocks(player.posX+player.width*0.35D,player.getEntityBoundingBox().minY+0.5D,player.posZ-player.width*0.35D);
		playerAPI.localPushOutOfBlocks(player.posX+player.width*0.35D,player.getEntityBoundingBox().minY+0.5D,player.posZ+player.width*0.35D);
		boolean enoughHunger = player.getFoodStats().getFoodLevel() > 6F || player.capabilities.allowFlying;
		
		// CHANGE
		if (ClientModManager.disableModFunctionality){
			if (player.onGround && !isMovingForward && player.movementInput.moveForward >= minSpeed && !player.isSprinting() && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
				if (player.sprintToggleTimer <= 0 && !ClientModManager.keyBindSprint.isKeyDown())player.sprintToggleTimer = 7;
				else player.setSprinting(true);
			}

			if (!player.isSprinting() && player.movementInput.moveForward >= minSpeed && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness) && ClientModManager.keyBindSprint.isKeyDown()){
				player.setSprinting(true);
			}
		}
		else{
			updateBehavior();
			boolean lastheld = ClientModManager.held;
			boolean state = customMovementInput.sprint;
			boolean doubletap = ClientModManager.allowDoubleTap || ClientModManager.disableModFunctionality;

			if (!player.capabilities.isFlying && ((MovementInputFromOptions)player.movementInput).sneak)state = false;
			
			if (((doubletap && !player.isSprinting()) || !doubletap) && player.onGround && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
				player.setSprinting(state);
			}
			
			ClientModManager.held = state;

			if (doubletap && !ClientModManager.held && ClientModManager.stoptime == 0 && player.onGround && !isMovingForward && player.movementInput.moveForward >= minSpeed && !player.isSprinting() && enoughHunger && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)){
				if (player.sprintToggleTimer == 0){
					player.sprintToggleTimer = 7;
				}
				else{
					player.setSprinting(true);
					player.sprintToggleTimer = 0;
				}
			}
			
			if (doubletap){
				if (lastheld && !ClientModManager.held)ClientModManager.stoptime=1;
				if (ClientModManager.stoptime > 0){
					ClientModManager.stoptime--;
					player.setSprinting(false);
				}
			}

			if (ClientModManager.flyingBoost > 0){
				if (state && player.capabilities.isFlying && ClientModManager.canBoostFlying(mc)){
					player.capabilities.setFlySpeed(0.05F*(1+ClientModManager.flyingBoost));
					if (player.movementInput.sneak){
						player.motionY -= 0.15D*ClientModManager.flyingBoost;
					}

					if (player.movementInput.jump){
						player.motionY += 0.15D*ClientModManager.flyingBoost;
					}
				}
				else player.capabilities.setFlySpeed(0.05F);
			}
			else if (player.capabilities.getFlySpeed() > 0.05F)player.capabilities.setFlySpeed(0.05F);
		}

		if (ClientModManager.keyBindSprintMenu.isKeyDown())mc.displayGuiScreen(new GuiSprint(null));
		if (player.isSprinting() && player.isSneaking() && !player.capabilities.isFlying)player.setSprinting(false);
		// END

		if (player.isSprinting() && (player.movementInput.moveForward < minSpeed || player.isCollidedHorizontally || !enoughHunger)){
			if ((ClientModManager.canRunInAllDirs(mc) && ClientModManager.allowAllDirs) == false || (player.movementInput.moveForward == 0F && player.movementInput.moveStrafe == 0F))player.setSprinting(false); // CHANGED LINE
		}
		
		if (player.capabilities.allowFlying){
			if (mc.playerController.isSpectatorMode()){
				if (!player.capabilities.isFlying){
					player.capabilities.isFlying = true;
					player.sendPlayerAbilities();
				}
			}
			else if (!wasJumping && player.movementInput.jump){
				if (player.flyToggleTimer == 0)player.flyToggleTimer = 7;
				else{
					player.capabilities.isFlying = !player.capabilities.isFlying;
					player.sendPlayerAbilities();
					player.flyToggleTimer = 0;
				}
			}
		}

		if (player.capabilities.isFlying){
			if (player.movementInput.sneak){
				player.motionY -= player.capabilities.getFlySpeed()*3F;
			}

			if (player.movementInput.jump){
				player.motionY += player.capabilities.getFlySpeed()*3F;
			}
		}

		if (player.isRidingHorse()){
			if (player.horseJumpPowerCounter < 0 && ++player.horseJumpPowerCounter == 0){
				player.horseJumpPower = 0F;
			}

			if (wasJumping && !player.movementInput.jump){
				player.horseJumpPowerCounter = -10;
				player.sendQueue.addToSendQueue(new C0BPacketEntityAction(player,C0BPacketEntityAction.Action.RIDING_JUMP,(int)(player.getHorseJumpPower()*100F)));
			}
			else if (!wasJumping && player.movementInput.jump){
				player.horseJumpPowerCounter = 0;
				player.horseJumpPower = 0F;
			}
			else if (wasJumping){
				if (++player.horseJumpPowerCounter < 10){
					player.horseJumpPower = player.horseJumpPowerCounter*0.1F;
				}
				else{
					player.horseJumpPower = 0.8F+2.0F/(player.horseJumpPowerCounter-9)*0.1F;
				}
			}
		}
		else player.horseJumpPower = 0F;

		playerAPI.superOnLivingUpdate();

		if (player.onGround && player.capabilities.isFlying){
			player.capabilities.isFlying=false;
			player.sendPlayerAbilities();
		}
	}
	
	private void updateBehavior(){
		if (mc.currentScreen != null && player != null && player.isSneaking()){
			if (customMovementInput.sneakToggle && !(mc.currentScreen instanceof GuiGameOver)){
				if (!ClientModManager.showedToggleSneakWarning){
					player.addChatMessage(new ChatComponentText("First-time warning: You can open inventories and menus while sneaking, however you will not be sneaking for the time it is open. Once you close the menu, sneaking will be restored."));
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					ClientModManager.showedToggleSneakWarning = true;
					ClientModManager.saveSprint(mc);
				}
				else{
					shouldRestoreSneakToggle = true;
					customMovementInput.sneakToggle = false;
				}
			}
		}
		
		if (shouldRestoreSneakToggle && mc.currentScreen == null){
			customMovementInput.sneakToggle = true;
			shouldRestoreSneakToggle = false;
		}

		if (behaviorCheckTimer > 0){
			--behaviorCheckTimer;
			return;
		}
		
		behaviorCheckTimer = 10;
		
		if (mc.thePlayer == null){
			connectedServer = "";
			ClientModManager.svFlyingBoost = ClientModManager.svRunInAllDirs = false;
		}
		else if (!mc.isIntegratedServerRunning() && mc.getCurrentServerData() != null && !ClientModManager.disableModFunctionality){
			String serverIP = mc.getCurrentServerData().serverIP;
			
			if (!connectedServer.equals(serverIP)){
				ClientModManager.svFlyingBoost = ClientModManager.svRunInAllDirs = false;
				connectedServer = new String(serverIP);
				
				if ((connectedServer.startsWith("127.0.0.1") || connectedServer.equals("localhost")) == false){
					PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
					buffer.writeByte(4);
					mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("BSprint",buffer));
				}
				else ClientModManager.svFlyingBoost = ClientModManager.svRunInAllDirs = true;
			}
		}
	}
}
