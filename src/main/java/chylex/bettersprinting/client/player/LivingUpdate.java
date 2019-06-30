package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import chylex.bettersprinting.client.ClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class LivingUpdate{
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public static void injectMovementInputUpdate(ClientPlayerEntity $this, boolean slowMovement, boolean isSpectator){
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
			hasTriggered = true;
		}
		
		if (mc.playerController.isInCreativeMode() && $this.abilities.isFlying && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get()){
			$this.onGround = false;
		}
		
		/*
		this.movementInput.func_217607_a(flag3, this.isSpectator()); <<< REPLACE
		*/
		currentHandler.updateMovementInput(slowMovement, isSpectator);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public static void injectSprinting(ClientPlayerEntity $this){
		/*
				this.pushOutOfBlocks(this.posX + (double)this.getWidth() * 0.35D, axisalignedbb.minY + 0.5D, this.posZ + (double)this.getWidth() * 0.35D);
			}
		}
		
		<<< INSERTED HERE
		
		boolean flag7 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		if ((this.onGround || this.canSwim()) && !flag1 && !flag2 && this.func_223110_ee() && !this.isSprinting() && flag7 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS)) {
		*/
		
		currentHandler.updateLiving();
		
		/*
				this.setSprinting(false);
			}
		}
		
		<<< SKIPPED TO HERE
		
		if (this.abilities.allowFlying) {
		*/
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.14.3
	public static void injectAfterSuperCall(ClientPlayerEntity $this){
		/*
		super.livingTick();
		
		<<< INSERTED HERE
		
		if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
		*/
		
		if ($this.onGround && $this.abilities.isFlying && !mc.playerController.isSpectatorMode()){
			boolean shouldFlyOnGround = mc.playerController.isInCreativeMode() && ClientModManager.canFlyOnGround() && ClientSettings.flyOnGround.get();
			
			if (!shouldFlyOnGround){
				$this.abilities.isFlying = false;
				$this.sendPlayerAbilities();
			}
		}
		/*
			this.sendPlayerAbilities();
		}
		
		<<< SKIPPED TO HERE
		*/
	}
	
	private LivingUpdate(){}
}
