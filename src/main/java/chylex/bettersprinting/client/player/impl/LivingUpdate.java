package chylex.bettersprinting.client.player.impl;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.player.PlayerLogicHandler;

@SideOnly(Side.CLIENT)
public final class LivingUpdate{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static PlayerLogicHandler currentHandler; // TODO rewrite
	
	private static final MethodHandle mPushOutOfBlocks;
	
	static{
		try{
			mPushOutOfBlocks = MethodHandles.lookup().unreflect(EntityPlayerSP.class.getMethod("_bsm_pushOutOfBlocks", double.class, double.class, double.class));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static void callPreSuper(EntityPlayerSP player){
		if (currentHandler == null || currentHandler.getPlayer() != player){
			currentHandler = new PlayerLogicHandler();
			currentHandler.setPlayer(player);
		}
		
		callPreSuper(player, currentHandler);
	}
	
	public static void callPostSuper(EntityPlayerSP player){
		callPostSuper(player, currentHandler);
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12
	public static void callPreSuper(EntityPlayerSP player, PlayerLogicHandler logic){
		// VANILLA
		++player.sprintingTicksLeft;
		
		if (player.sprintToggleTimer > 0){
			--player.sprintToggleTimer;
		}
		
		player.prevTimeInPortal = player.timeInPortal;
		
		if (player.inPortal){
			if (mc.currentScreen != null && !mc.currentScreen.doesGuiPauseGame()){
				mc.displayGuiScreen(null);
			}
			
			if (player.timeInPortal == 0F){
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRIGGER, player.getRNG().nextFloat()*0.4F+0.8F));
			}
			
			player.timeInPortal += 0.0125F;
			
			if (player.timeInPortal >= 1F){
				player.timeInPortal = 1F;
			}
			
			player.inPortal = false;
		}
		else if (player.isPotionActive(MobEffects.NAUSEA) && player.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60){
			player.timeInPortal += 0.006666667F;
			
			if (player.timeInPortal > 1F){
				player.timeInPortal = 1F;
			}
		}
		else{
			if (player.timeInPortal > 0F){
				player.timeInPortal -= 0.05F;
			}
			
			if (player.timeInPortal < 0F){
				player.timeInPortal = 0F;
			}
		}
		
		if (player.timeUntilPortal > 0){
			--player.timeUntilPortal;
		}
		
		boolean wasJumping = player.movementInput.jump;
		logic.updateMovementInput();
		
		if (player.isHandActive() && !player.isRiding()){
			player.movementInput.moveStrafe *= 0.2F;
			player.movementInput.field_192832_b *= 0.2F;
			player.sprintToggleTimer = 0;
		}
		
		boolean hasAutoJumped = false;
		
		if (player.autoJumpTime > 0){
			--player.autoJumpTime;
			hasAutoJumped = true;
			player.movementInput.jump = true;
		}
		
		AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
		
		try{
			mPushOutOfBlocks.invokeExact(player, player.posX-player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ+player.width*0.35D);
			mPushOutOfBlocks.invokeExact(player, player.posX-player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ-player.width*0.35D);
			mPushOutOfBlocks.invokeExact(player, player.posX+player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ-player.width*0.35D);
			mPushOutOfBlocks.invokeExact(player, player.posX+player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ+player.width*0.35D);
		}catch(Throwable e){
			throw new RuntimeException(e);
		}
		
		// CUSTOM
		logic.updateLiving();
		
		// VANILLA
		if (player.capabilities.allowFlying){
			if (mc.playerController.isSpectatorMode()){
				if (!player.capabilities.isFlying){
					player.capabilities.isFlying = true;
					player.sendPlayerAbilities();
				}
			}
			else if (!wasJumping && player.movementInput.jump && !hasAutoJumped){
				if (player.flyToggleTimer == 0){
					player.flyToggleTimer = 7;
				}
				else{
					player.capabilities.isFlying = !player.capabilities.isFlying;
					player.sendPlayerAbilities();
					player.flyToggleTimer = 0;
				}
			}
		}
		
		if (player.movementInput.jump && !wasJumping && !player.onGround && player.motionY < 0D && !player.isElytraFlying() && !player.capabilities.isFlying){
			ItemStack chestIS = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			
			if (chestIS.getItem() == Items.ELYTRA && ItemElytra.isUsable(chestIS)){
				player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_FALL_FLYING));
			}
		}
		
		player.wasFallFlying = player.isElytraFlying();

		if (player.capabilities.isFlying && callIsCurrentViewEntity(player)){
			if (player.movementInput.sneak){
				player.movementInput.moveStrafe = player.movementInput.moveStrafe/0.3F;
				player.movementInput.field_192832_b = player.movementInput.field_192832_b/0.3F;
				player.motionY -= 0.15D; // ignore capabilities.getFlySpeed()
			}

			if (player.movementInput.jump){
				player.motionY += 0.15D; // ignore capabilities.getFlySpeed()
			}
		}

		if (player.isRidingHorse()){
			IJumpingMount mount = (IJumpingMount)player.getRidingEntity();
			
			if (player.horseJumpPowerCounter < 0 && ++player.horseJumpPowerCounter == 0){
				player.horseJumpPower = 0F;
			}

			if (wasJumping && !player.movementInput.jump){
				player.horseJumpPowerCounter = -10;
				mount.setJumpPower(MathHelper.floor(player.getHorseJumpPower()*100F));
				callSendHorseJump(player);
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
		else{
			player.horseJumpPower = 0F;
		}
	}
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.12
	public static void callPostSuper(EntityPlayerSP player, PlayerLogicHandler logic){
		if (player.onGround && player.capabilities.isFlying && !mc.playerController.isSpectatorMode()){
			player.capabilities.isFlying = false;
			player.sendPlayerAbilities();
		}
	}
	
	// UPDATE | EntityPlayerSP.isCurrentViewEntity | 1.12
	private static boolean callIsCurrentViewEntity(EntityPlayerSP player){
		return mc.getRenderViewEntity() == player;
	}
	
	// UPDATE | EntityPlayerSP.sendHorseJump | 1.12
	private static void callSendHorseJump(EntityPlayerSP player){
		player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_RIDING_JUMP, (int)(player.getHorseJumpPower()*100F)));
	}
	
	private LivingUpdate(){}
}
