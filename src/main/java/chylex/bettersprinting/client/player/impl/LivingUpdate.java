package chylex.bettersprinting.client.player.impl;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.player.PlayerLogicHandler;

@SideOnly(Side.CLIENT)
final class LivingUpdate{
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.11
	public static void callPreSuper(EntityPlayerSP player, Minecraft mc, PlayerLogicHandler logic){
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
			if (player.timeInPortal > 0F)player.timeInPortal -= 0.05F;
			if (player.timeInPortal < 0F)player.timeInPortal = 0F;
		}
		
		if (player.timeUntilPortal > 0){
			--player.timeUntilPortal;
		}
		
		boolean wasJumping = player.movementInput.jump;
		logic.updateMovementInput();
		
		if (player.isHandActive() && !player.isRiding()){
			player.movementInput.moveStrafe *= 0.2F;
			player.movementInput.moveForward *= 0.2F;
			player.sprintToggleTimer = 0;
		}
		
		boolean hasAutoJumped = false;
		
		if (player.autoJumpTime > 0){
			--player.autoJumpTime;
			hasAutoJumped = true;
			player.movementInput.jump = true;
		}
		
		AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
		pushOutOfBlocks(player, player.posX-player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ+player.width*0.35D);
		pushOutOfBlocks(player, player.posX-player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ-player.width*0.35D);
		pushOutOfBlocks(player, player.posX+player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ-player.width*0.35D);
		pushOutOfBlocks(player, player.posX+player.width*0.35D, playerBoundingBox.minY+0.5D, player.posZ+player.width*0.35D);
		
		logic.updateLiving();
		
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
			
			if (chestIS.getItem() == Items.ELYTRA && ItemElytra.isBroken(chestIS)){
				player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_FALL_FLYING));
			}
		}
		
		player.wasFallFlying = player.isElytraFlying();

		if (player.capabilities.isFlying && mc.getRenderViewEntity() == player){ // uses isCurrentViewEntity but it is protected
			if (player.movementInput.sneak){
				player.movementInput.moveStrafe = player.movementInput.moveStrafe/0.3F;
				player.movementInput.moveForward = player.movementInput.moveForward/0.3F;
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
				player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_RIDING_JUMP, (int)(player.getHorseJumpPower()*100F))); // uses sendHorseJump but it is protected
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
	
	// UPDATE | EntityPlayerSP.onLivingUpdate | 1.11
	public static void callPostSuper(EntityPlayerSP player, Minecraft mc, PlayerLogicHandler logic){
		if (player.onGround && player.capabilities.isFlying && !mc.playerController.isSpectatorMode()){
			player.capabilities.isFlying = false;
			player.sendPlayerAbilities();
		}
	}
	
	// UPDATE | EntityPlayerSP.pushOutOfBlocks | 1.11
	protected static boolean pushOutOfBlocks(EntityPlayerSP player, double x, double y, double z){
		if (player.noClip){
			return false;
		}
		
		BlockPos pos = new BlockPos(x, y, z);
		double xDiff = x-pos.getX();
		double zDiff = z-pos.getZ();

		int entHeight = Math.max((int)Math.ceil(player.height), 1);
		
		if (!isHeadspaceFree(player, pos, entHeight)){
			int side = -1;
			double limit = 9999D;

			if (isHeadspaceFree(player, pos.west(), entHeight) && xDiff < limit){
				limit = xDiff;
				side = 0;
			}

			if (isHeadspaceFree(player, pos.east(), entHeight) && 1D-xDiff < limit){
				limit = 1D-xDiff;
				side = 1;
			}

			if (isHeadspaceFree(player, pos.north(), entHeight) && zDiff < limit){
				limit = zDiff;
				side = 4;
			}

			if (isHeadspaceFree(player, pos.south(), entHeight) && 1D-zDiff < limit){
				limit = 1D-zDiff;
				side = 5;
			}

			if (side == 0)player.motionX = -0.1D;
			else if (side == 1)player.motionX = 0.1D;
			else if (side == 4)player.motionZ = -0.1D;
			else if (side == 5)player.motionZ = 0.1D;
			// added 'else' to the statements
		}

		return false;
	}
	
	// UPDATE | EntityPlayerSP.isOpenBlockSpace | 1.11
	private static boolean isOpenBlockSpace(EntityPlayerSP player, BlockPos pos){
		return !player.world.getBlockState(pos).isNormalCube();
	}
	
	// UPDATE | EntityPlayerSP.isHeadspaceFree | 1.11
	private static boolean isHeadspaceFree(EntityPlayerSP player, BlockPos pos, int height){
		for(int y = 0; y < height; y++){
			if (!isOpenBlockSpace(player, pos.add(0, y, 0)))return false;
		}
		
		return true;
	}
	
	private LivingUpdate(){}
}
