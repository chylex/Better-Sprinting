package chylex.bettersprinting.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(int protocol){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeByte(protocol);
		return buffer;
	}
	
	public static PacketBuffer writeLanSettings(){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0).writeBoolean(true).writeBoolean(true);
		return buffer;
	}
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){
		if (side == Side.SERVER){
			PacketPipeline.sendToPlayer(writeLanSettings(), player);
			return;
		}
		
		byte type = data.readByte();
		
		if (type == 0){
			ClientModManager.svSurvivalFlyingBoost = data.readBoolean();
			ClientModManager.svRunInAllDirs = data.readBoolean();
		}
		else if (type == 1 && !ClientSettings.disableMod){
			ClientModManager.svDisableMod = true;
			ClientModManager.showDisableWarningWhenPossible = true;
		}
		else if (type == 2 && !ClientSettings.disableMod){
			ClientModManager.svDisableMod = false;
			ClientModManager.showDisableWarningWhenPossible = true;
		}
	}
}
