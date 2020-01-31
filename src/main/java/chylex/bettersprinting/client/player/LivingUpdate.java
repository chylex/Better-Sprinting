package chylex.bettersprinting.client.player;
import chylex.bettersprinting.client.ClientModManager;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
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
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.15.2
	public static void injectMovementInputUpdate(ClientPlayerEntity player, boolean slowMovement){
		hasTriggered = true;
		isModDisabled = ClientModManager.isModDisabled();
		
		// this.movementInput.func_225607_a_(this.func_228354_I_()); <<< REPLACE
		
		if (isModDisabled){
			if (currentHandler != null){
				currentHandler.resetState();
				currentHandler = null;
			}
			
			player.movementInput.func_225607_a_(slowMovement);
			return;
		}
		
		if (currentHandler == null || currentHandler.getPlayer() != player){
			currentHandler = new PlayerLogicHandler(player);
		}
		
		currentHandler.updateMovementInput(slowMovement);
	}
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.15.2
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
	
	// UPDATE | ClientPlayerEntity.livingTick | 1.15.2
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
}
