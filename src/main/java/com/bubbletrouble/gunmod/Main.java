package com.bubbletrouble.gunmod;

import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.utils.GunSoundRegistry;
import com.bubbletrouble.gunmod.utils.GunTab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main
{
    public static final String MODID = "gunmod";
    public static final String VERSION = "1.0";
    
	@Instance(Main.MODID)
	private static Main instance;
	
	@SidedProxy(clientSide = "com.bubbletrouble.gunmod.client.proxy.ClientProxy",
			serverSide = "com.bubbletrouble.gunmod.server.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabGuns = new GunTab("gunTab");
	public final static EventBus EVENT_BUS = new EventBus();
	public static SimpleNetworkWrapper modChannel;

    
    @EventHandler
	public void preInit(FMLPreInitializationEvent event)
    {
		proxy.preInit(event);
    }
    

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GunSoundRegistry.init();
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
	
	public static Main instance()
	{
		return instance;
	}
}
