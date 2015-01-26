package chylex.bettersprinting.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import chylex.bettersprinting.system.PacketPipeline;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;

public class ClientNetwork implements INetworkHandler{
	public static PacketBuffer writeModNotification(){
		PacketBuffer buffer = PacketPipeline.buf();
		buffer.writeByte(0);
		buffer.writeByte(10);
		return buffer;
	}
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){}
}
