package chylex.bettersprinting.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.bettersprinting.client.player.PlayerLogicHandler;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;

@SideOnly(Side.CLIENT)
public class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(int protocol){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){
		byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyingBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1 && !ClientSettings.disableMod){
			ClientModManager.svDisableMod = true;
			PlayerLogicHandler.showDisableWarningWhenPossible = true;
		}
		else if (type == 2 && !ClientSettings.disableMod){
			ClientModManager.svDisableMod = false;
			PlayerLogicHandler.showDisableWarningWhenPossible = true;
		}
	}
}
