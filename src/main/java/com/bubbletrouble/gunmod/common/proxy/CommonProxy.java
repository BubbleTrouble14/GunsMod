package com.bubbletrouble.gunmod.common.proxy;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.handlers.CommonEventHandler;
import com.bubbletrouble.gunmod.common.handlers.GuiHandler;
import com.bubbletrouble.gunmod.common.network.LeftGunFired;
import com.bubbletrouble.gunmod.common.network.LeftGunFiredClient;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadFinished;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.RightGunFired;
import com.bubbletrouble.gunmod.common.network.RightGunFiredClient;
import com.bubbletrouble.gunmod.common.network.RightGunReloadFinished;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;
import com.bubbletrouble.gunmod.events.PlayerUpdateEvent;
import com.bubbletrouble.gunmod.init.RangedWeapons;
import com.bubbletrouble.gunmod.init.Recipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class CommonProxy 
{

	public CommonProxy()	{}
	
	public enum GUI
	{

		ATTACHMENTS;

		public final int id;

		GUI()
		{
			this.id = getNextId();
		}

		static int idCounter = 0;

		private static int getNextId()
		{
			return idCounter++;
		}
	}

	public void preInit(FMLPreInitializationEvent event)
	{
		setupNetwork(event);
		RangedWeapons.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance(), new GuiHandler());
	}

//	public void registerItemRenderer(Item item, int meta, String id) {}
		
	public void init(FMLInitializationEvent event)
	{
		registerEventHandlers();
		Recipes.init();
//		ARKCraftEntities.init();
	}

	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	protected void registerEventHandlers()
	{
	//	CommonEventHandler.init();
		new PlayerUpdateEvent();
	}
	
	private final void setupNetwork(FMLPreInitializationEvent event)
	{
		SimpleNetworkWrapper modChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
		Main.modChannel = modChannel;
		
		int id = 0;
		
		modChannel.registerMessage(RightGunReloadStarted.Handler.class, RightGunReloadStarted.class, id++, Side.SERVER);
		modChannel.registerMessage(RightGunReloadFinished.Handler.class, RightGunReloadFinished.class, id++, Side.CLIENT);
		modChannel.registerMessage(RightGunFired.Handler.class, RightGunFired.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunFired.Handler.class, LeftGunFired.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunReloadStarted.Handler.class, LeftGunReloadStarted.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunReloadFinished.Handler.class, LeftGunReloadFinished.class, id++, Side.CLIENT);
		modChannel.registerMessage(OpenAttachmentInventory.Handler.class, OpenAttachmentInventory.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunFiredClient.Handler.class, LeftGunFiredClient.class, id++, Side.CLIENT);
		modChannel.registerMessage(RightGunFiredClient.Handler.class, RightGunFiredClient.class, id++, Side.CLIENT);
	}
	
	public EntityPlayer getPlayer()
	{	
		return null;
	}

	public abstract EntityPlayer getPlayerFromContext(MessageContext ctx);

	public abstract long getTime();

	public abstract long getWorldTime();
	
	//public abstract void registerEntityModels();
}
