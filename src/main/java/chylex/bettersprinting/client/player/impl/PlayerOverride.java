package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
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
	
	// UPDATE | EntityLivingBase.jumpTicks | Check if still only used in onLivingUpdate | 1.11
	private int jumpTicks;
	
	public PlayerOverride(Minecraft mc, World world, NetHandlerPlayClient netHandler, StatisticsManager statFile, RecipeBook recipeBook){
		super(mc, world, netHandler, statFile, recipeBook);
		logic = new PlayerLogicHandler();
		logic.setPlayer(this);
		
		entityUniqueID = mc.player.getUniqueID();
	}
	
	@Override
	public void onLivingUpdate(){
		LivingUpdate.callPreSuper(this, mc, logic);
		onLivingUpdate$EntityPlayer();
		LivingUpdate.callPostSuper(this, mc, logic);
	}
	
	// UPDATE | EntityPlayer.onLivingUpdate | 1.11.2
	private void onLivingUpdate$EntityPlayer(){
		if (flyToggleTimer > 0){
			--flyToggleTimer;
		}
		
		if (world.getDifficulty() == EnumDifficulty.PEACEFUL && world.getGameRules().getBoolean("naturalRegeneration")){
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

		if (!world.isRemote){
			speedAttr.setBaseValue(capabilities.getWalkSpeed());
		}

		jumpMovementFactor = speedInAir;
		
		if (isSprinting()){
			jumpMovementFactor = (float)(jumpMovementFactor+speedInAir*0.3D);
		}

		setAIMoveSpeed((float)speedAttr.getAttributeValue());
		float moveDist = MathHelper.sqrt(motionX*motionX+motionZ*motionZ);
		float motYFactor = (float)(Math.atan(-motionY*0.2D)*15D);

		if (moveDist > 0.1F){
			moveDist = 0.1F;
		}

		if (!onGround || getHealth() <= 0F)moveDist = 0F;
		if (onGround || getHealth() <= 0F)motYFactor = 0F;

		cameraYaw += (moveDist-cameraYaw)*0.4F;
		cameraPitch += (motYFactor-cameraPitch)*0.8F;

		if (getHealth() > 0F && !isSpectator()){
			AxisAlignedBB aabb = null;

			if (isRiding() && !getRidingEntity().isDead){
				aabb = getEntityBoundingBox().union(getRidingEntity().getEntityBoundingBox()).expand(1D, 0D, 1D);
			}
			else{
				aabb = getEntityBoundingBox().expand(1D, 0.5D, 1D);
			}

			for(Entity entity:world.getEntitiesWithinAABBExcludingEntity(this, aabb)){
				if (!entity.isDead){
					entity.onCollideWithPlayer(this); // uses collideWithPlayer but it's private
				}
			}
		}
	}
	
	// UPDATE | EntityLivingBase.onLivingUpdate | 1.11
	private void onLivingUpdate$EntityLivingBase(){
		if (jumpTicks > 0){
			--jumpTicks;
		}

		if (newPosRotationIncrements > 0 && !canPassengerSteer()){
			double setPosX = posX+(interpTargetX-posX)/newPosRotationIncrements;
			double setPosY = posY+(interpTargetY-posY)/newPosRotationIncrements;
			double setPosZ = posZ+(interpTargetZ-posZ)/newPosRotationIncrements;
			rotationYaw = (float)(rotationYaw+MathHelper.wrapDegrees(interpTargetYaw-rotationYaw)/newPosRotationIncrements);
			rotationPitch = (float)(rotationPitch+(interpTargetPitch-rotationPitch)/newPosRotationIncrements);
			--newPosRotationIncrements;
			setPosition(setPosX, setPosY, setPosZ);
			setRotation(rotationYaw, rotationPitch);
		}
		else if (!isServerWorld()){
			motionX *= 0.98D;
			motionY *= 0.98D;
			motionZ *= 0.98D;
		}

		if (Math.abs(motionX) < 0.003D)motionX = 0D;
		if (Math.abs(motionY) < 0.003D)motionY = 0D;
		if (Math.abs(motionZ) < 0.003D)motionZ = 0D;
		
		world.profiler.startSection("ai");

		if (isMovementBlocked()){
			isJumping = false;
			moveStrafing = 0F;
			moveForward = 0F;
			randomYawVelocity = 0F;
		}
		else if (isServerWorld()){ // isAIEnabled is false
			world.profiler.startSection("newAi");
            updateEntityActionState();
            world.profiler.endSection();
		}

		world.profiler.endSection();
		world.profiler.startSection("jump");

		if (isJumping){
			if (isInWater()){
				handleJumpWater();
			}
			else if (isInLava()){
				handleJumpLava();
			}
			else if (onGround && jumpTicks == 0){
				jump();
				jumpTicks = 10;
			}
		}
		else{
			jumpTicks = 0;
		}

		world.profiler.endSection();
		world.profiler.startSection("travel");
		
		moveStrafing *= 0.98F;
		moveForward *= 0.98F;
		randomYawVelocity *= 0.9F;
		updateElytra$EntityLivingBase();
		func_191986_a(moveStrafing, moveForward, field_191988_bg);
		
		world.profiler.endSection();
		world.profiler.startSection("push");
		
		collideWithNearbyEntities();
		
		world.profiler.endSection();
	}
	
	// UPDATE | EntityLivingBase.updateElytra | 1.11
	private void updateElytra$EntityLivingBase(){
		boolean flag = getFlag(7);
		
		if (flag && !onGround && !isRiding()){
			ItemStack is = getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			
			if (is.getItem() == Items.ELYTRA && ItemElytra.isUsable(is)){
				flag = true;
				
				if (!world.isRemote && (ticksElytraFlying+1)%20 == 0){
					is.damageItem(1, this);
				}
			}
			else{
				flag = false;
			}
		}
		else{
			flag = false;
		}
		
		if (!world.isRemote){
			setFlag(7, flag);
		}
	}
}
