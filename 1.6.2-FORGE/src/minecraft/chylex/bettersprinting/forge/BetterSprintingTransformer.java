package chylex.bettersprinting.forge;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;

public class BetterSprintingTransformer implements IClassTransformer{
	@Override
	public byte[] transform(String obf, String srg, byte[] bytes){
		if (BetterSprintingCoremod.modFile==null||obf.equals(srg))return bytes;

		boolean ok=false;
		for(String srgcls:BetterSprintingCoremod.classes){
			if (srg.contains(srgcls))ok=true;
		}
		if (!ok)return bytes;
		
		ZipFile zip=null;
		DataInputStream dis=null;
		byte[] nbytes=null;
		try{
			zip=new ZipFile(BetterSprintingCoremod.modFile);
			ZipEntry entry=zip.getEntry(obf+".class");
			if (entry==null)return bytes;
			
			dis=new DataInputStream(zip.getInputStream(entry));
			nbytes=new byte[(int)entry.getSize()];
			dis.readFully(nbytes);
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Could not load Better Sprinting Coremod!");
		}finally{
			try{
				if (zip!=null)zip.close();
				if (dis!=null)dis.close();
			}catch(IOException e){}
		}
		
		return nbytes==null?bytes:nbytes;
	}
}
