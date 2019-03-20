package chylex.bettersprinting.forge;
import java.util.Arrays;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class BetterSprintingContainer extends DummyModContainer{
	public BetterSprintingContainer(){
		super(createModMetadata());
	}

	@Override
    public boolean registerBus(EventBus bus, LoadController controller){
        return true;
    }

	private static ModMetadata createModMetadata(){
		ModMetadata meta=new ModMetadata();
		meta.modId="BetterSprinting";
		meta.name="Better Sprinting (core)";
		meta.description="";
		meta.version="v11";
		meta.url="http://www.minecraftforum.net/topic/1451360-better-sprinting";
		meta.authorList=Arrays.asList(new String[]{"chylex"});
		return meta;
	}
}
