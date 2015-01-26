package chylex.bettersprinting.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import chylex.bettersprinting.system.PacketPipeline.INetworkHandler;

public class ServerNetwork implements INetworkHandler{
	/*
	 * INCOMING PACKETS
	 * ================
	 * Payload packet on "BSprint" channel
	 * * byte 4 - old protocol, no custom functionality
	 * * byte 5 - new protocol, if you updated your handling code then ignore people who send this one and wait for BSM
	 * 
	 * Payload packet on "BSM" channel
	 * * byte 0, byte X - new protocol, X is the protocol version (currently 10)
	 *                  - you are free to use new functionality
	 * 
	 * OUTCOMING PACKETS
	 * =================
	 * Payload packet on "BSM" channel
	 * * byte 0, boolean enableSurvivalFlyBoost, boolean enableAllDirs - custom settings, both are false by default
	 * * byte 1 - disables the mod on client-side
	 */
	
	@Override
	public void onPacket(Side side, ByteBuf data, EntityPlayer player){
		byte type = data.readByte();
		// TODO
	}
}
