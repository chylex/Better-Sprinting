package chylex.bettersprinting.system;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPipeline{
	private static final String channelName = "BSM";
	private static PacketPipeline instance;
	
	public static void initialize(INetworkHandler handler){
		if (instance != null)throw new RuntimeException("Packet pipeline has already been registered!");
		instance = new PacketPipeline(handler);
	}
	
	private final FMLEventChannel channel;
	private final INetworkHandler handler;
	
	private PacketPipeline(INetworkHandler handler){
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);
		channel.register(this);
		this.handler = handler;
	}
	
	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent e){
		handler.onPacket(Side.CLIENT, e.getPacket().payload(), getClientPlayer());
	}
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent e){
		handler.onPacket(Side.SERVER, e.getPacket().payload(), ((NetHandlerPlayServer)e.getHandler()).playerEntity);
	}
	
	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().player;
	}
	
	public static PacketBuffer buf(){
		return new PacketBuffer(Unpooled.buffer());
	}
	
	public static void sendToPlayer(PacketBuffer buffer, EntityPlayer player){
		instance.channel.sendTo(new FMLProxyPacket(buffer, channelName), (EntityPlayerMP)player);
	}
	
	public static void sendToServer(PacketBuffer buffer){
		instance.channel.sendToServer(new FMLProxyPacket(buffer, channelName));
	}
	
	public static interface INetworkHandler{
		void onPacket(Side side, ByteBuf data, EntityPlayer player);
	}
}
