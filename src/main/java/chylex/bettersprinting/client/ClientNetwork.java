package chylex.bettersprinting.client;
import chylex.bettersprinting.BetterSprintingNetwork;
import chylex.bettersprinting.BetterSprintingNetwork.INetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public final class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(final int protocol){
		final PacketBuffer buffer = INetworkHandler.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	private static PacketBuffer writeLanSettings(){
		final PacketBuffer buffer = INetworkHandler.buf();
		buffer.writeByte(0).writeBoolean(false).writeBoolean(true);
		return buffer;
	}
	
	@Override
	public void onPacket(final LogicalSide side, final ByteBuf data, final PlayerEntity player){
		if (side == LogicalSide.SERVER){
			BetterSprintingNetwork.sendToPlayer(writeLanSettings(), player);
			return;
		}
		
		final byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = true;
			ClientEventHandler.showDisableWarningWhenPossible = true;
		}
		else if (type == 2 && !ClientSettings.disableMod.get()){
			ClientModManager.svDisableMod = false;
			ClientEventHandler.showDisableWarningWhenPossible = true;
		}
	}
}
