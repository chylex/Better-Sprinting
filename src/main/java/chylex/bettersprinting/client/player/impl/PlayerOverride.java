package chylex.bettersprinting.client.player.impl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerOverride extends EntityPlayerSP{
	public PlayerOverride(Minecraft mc, World world, NetHandlerPlayClient netHandler, StatFileWriter statWriter){
		super(mc,world,netHandler,statWriter);
	}
	
	// TODO
}
