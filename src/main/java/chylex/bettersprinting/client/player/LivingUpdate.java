package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class LivingUpdate{
	private static PlayerLogicHandler currentHandler;
	private static boolean hasTriggered;
	private static boolean isModDisabled;
	
	public static boolean checkIntegrity(){
		return hasTriggered;
	}
	
	public static void cleanup(){
		currentHandler = null;
		hasTriggered = false;
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public static void injectMovementInputUpdate(EntityPlayerSP $this){
		hasTriggered = true;
		isModDisabled = ClientModManager.isModDisabled();
		
		// this.movementInput.updatePlayerMoveState(); <<< REPLACE
		
		if (isModDisabled){
			$this.movementInput.updatePlayerMoveState();
			currentHandler = null;
			return;
		}
		
		if (currentHandler == null || currentHandler.getPlayer() != $this){
			currentHandler = new PlayerLogicHandler($this);
		}
		
		currentHandler.updateMovementInput();
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public static boolean injectSprinting(){
		if (isModDisabled){
			return false;
		}
		
		/*
		}
		<<< INSERTED HERE
		boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		*/
		
		currentHandler.updateSprinting();
		return true;
		
		/*
		}
		<<< SKIPPED TO HERE
		if (this.abilities.allowFlying) {
		*/
	}
	
	// UPDATE | EntityPlayerSP.livingTick | 1.13.2
	public static boolean injectAfterSuperCall(){
		if (isModDisabled){
			return false;
		}
		
		/*
		super.livingTick();
		<<< INSERTED HERE
		if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
		*/
		
		currentHandler.updateFlight();
		return true;
		
		/*
			this.sendPlayerAbilities();
		}
		<<< SKIPPED TO HERE
		*/
	}
	
	private LivingUpdate(){}
}
