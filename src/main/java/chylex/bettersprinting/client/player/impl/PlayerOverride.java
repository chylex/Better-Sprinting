package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.player.PlayerLogicHandler;

@SideOnly(Side.CLIENT)
public class PlayerOverride extends EntityPlayerSP{
	private final PlayerLogicHandler logic;
	
	private int jumpTicks;
	
	public PlayerOverride(Minecraft mc, World world, NetHandlerPlayClient netHandler, StatFileWriter statWriter){
		super(mc,world,netHandler,statWriter);
		logic = new PlayerLogicHandler();
		logic.setPlayer(this);
		
		entityUniqueID = mc.thePlayer.getUniqueID();
	}
	
	@Override
	public void onLivingUpdate(){
		LivingUpdate.callPreSuper(this,mc,logic);
		onLivingUpdate$EntityPlayer();
		LivingUpdate.callPostSuper(this,mc,logic);
	}
	
	private void onLivingUpdate$EntityPlayer(){
		if (flyToggleTimer > 0)--flyToggleTimer;
		
		if (worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && worldObj.getGameRules().getBoolean("naturalRegeneration")){
			if (getHealth() < getMaxHealth() && ticksExisted%20 == 0){
				heal(1F);
			}
			
			if (foodStats.needFood() && ticksExisted%10 == 0){
				foodStats.setFoodLevel(foodStats.getFoodLevel()+1);
			}
		}

		inventory.decrementAnimations();
		prevCameraYaw = cameraYaw;
		
		onLivingUpdate$EntityLivingBase();
		
		IAttributeInstance speedAttr = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		if (!worldObj.isRemote)speedAttr.setBaseValue(capabilities.getWalkSpeed());

		jumpMovementFactor = speedInAir;
		if (isSprinting())jumpMovementFactor = (float)(jumpMovementFactor+speedInAir*0.3D);

		setAIMoveSpeed((float)speedAttr.getAttributeValue());
		float moveDist = MathHelper.sqrt_double(motionX*motionX+motionZ*motionZ);
		float motYFactor = (float)(Math.atan(-motionY*0.2D)*15D);

		if (moveDist > 0.1F)moveDist = 0.1F;

		if (!onGround || getHealth() <= 0F)moveDist = 0F;
		if (onGround || getHealth() <= 0F)motYFactor = 0F;

		cameraYaw += (moveDist-cameraYaw)*0.4F;
		cameraPitch += (motYFactor-cameraPitch)*0.8F;

		if (getHealth() > 0F && !isSpectator()){
			AxisAlignedBB aabb = null;

			if (ridingEntity != null && !ridingEntity.isDead)aabb = getEntityBoundingBox().union(ridingEntity.getEntityBoundingBox()).expand(1D,0D,1D);
			else aabb = getEntityBoundingBox().expand(1D,0.5D,1D);

			for(Entity entity:worldObj.getEntitiesWithinAABBExcludingEntity(this,aabb)){
				if (!entity.isDead)entity.onCollideWithPlayer(this);
			}
		}
	}
	
	private void onLivingUpdate$EntityLivingBase(){
		if (jumpTicks > 0)--jumpTicks;

		if (newPosRotationIncrements > 0){
			double setPosX = posX+(newPosX-posX)/newPosRotationIncrements;
			double setPosY = posY+(newPosY-posY)/newPosRotationIncrements;
			double setPosZ = posZ+(newPosZ-posZ)/newPosRotationIncrements;
			rotationYaw = (float)(rotationYaw+MathHelper.wrapAngleTo180_double(newRotationYaw-rotationYaw)/newPosRotationIncrements);
			rotationPitch = (float)(rotationPitch+(newRotationPitch-rotationPitch)/newPosRotationIncrements);
			--newPosRotationIncrements;
			setPosition(setPosX,setPosY,setPosZ);
			setRotation(rotationYaw,rotationPitch);
		}
		else if (!isServerWorld()){
			motionX *= 0.98D;
			motionY *= 0.98D;
			motionZ *= 0.98D;
		}

		if (Math.abs(motionX) < 0.005D)motionX = 0D;
		if (Math.abs(motionY) < 0.005D)motionY = 0D;
		if (Math.abs(motionZ) < 0.005D)motionZ = 0D;
		
		worldObj.theProfiler.startSection("ai");

		if (isMovementBlocked()){
			isJumping = false;
			moveStrafing = 0F;
			moveForward = 0F;
			randomYawVelocity = 0F;
		}
		else if (isServerWorld()){ // isAIEnabled is false
			worldObj.theProfiler.startSection("newAi");
            updateEntityActionState();
            worldObj.theProfiler.endSection();
		}

		worldObj.theProfiler.endSection();
		worldObj.theProfiler.startSection("jump");

		if (isJumping){
			if (isInWater()){
				updateAITick();
			}
			else if (isInLava()){
				handleJumpLava();
			}
			else if (onGround && jumpTicks == 0){
				jump();
				jumpTicks = 10;
			}
		}
		else jumpTicks = 0;

		worldObj.theProfiler.endSection();
		worldObj.theProfiler.startSection("travel");
		moveStrafing *= 0.98F;
		moveForward *= 0.98F;
		randomYawVelocity *= 0.9F;
		moveEntityWithHeading(moveStrafing,moveForward);
		
		worldObj.theProfiler.endSection();
		
		worldObj.theProfiler.startSection("push");
		if (!worldObj.isRemote)collideWithNearbyEntities();
		worldObj.theProfiler.endSection();
	}
}
