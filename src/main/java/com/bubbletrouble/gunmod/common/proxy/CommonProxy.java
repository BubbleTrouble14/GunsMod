package com.bubbletrouble.gunmod.common.proxy;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.cap.DecreaseStamina;
import com.bubbletrouble.gunmod.common.cap.IStamina;
import com.bubbletrouble.gunmod.common.cap.IncreaseStamina;
import com.bubbletrouble.gunmod.common.cap.Stamina;
import com.bubbletrouble.gunmod.common.cap.StaminaStorage;
import com.bubbletrouble.gunmod.common.crafting.CrafterCraftingManager;
import com.bubbletrouble.gunmod.common.handlers.GuiHandler;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.LeftGunShoot;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.OpenItemInventory;
import com.bubbletrouble.gunmod.common.network.RecoilLeftGun;
import com.bubbletrouble.gunmod.common.network.RecoilRightGun;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.RightGunShoot;
import com.bubbletrouble.gunmod.common.network.UpdateCrafterToCraftItem;
import com.bubbletrouble.gunmod.init.RangedWeapons;
import com.bubbletrouble.gunmod.init.Recipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
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

		ATTACHMENTS(0),
		CRAFTER(1),
		TEST(2);
		
		public final int id;

		GUI(int id)
		{
			this.id = getNextId();
		}

		static int idCounter = 0;

		private static int getNextId()
		{
			return idCounter++;
		}
		
		public final int getID()
		{
			return id;
		}
	}

	public void preInit(FMLPreInitializationEvent event)
	{
		setupNetwork(event);
		RangedWeapons.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance(), new GuiHandler());
	}
		
	public void init(FMLInitializationEvent event)
	{
		registerEventHandlers();
		CrafterCraftingManager.registerCraftingRecipes();
		Recipes.init();
	}

	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	protected void registerEventHandlers()
	{
        CapabilityManager.INSTANCE.register(IStamina.class, new StaminaStorage(), Stamina.class);
     //   MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		//MinecraftForge.EVENT_BUS.register(PlayerUpdateEvent.class);
	}
	
	private final void setupNetwork(FMLPreInitializationEvent event)
	{
		SimpleNetworkWrapper modChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
		Main.modChannel = modChannel;
		
		int id = 0;
		
	//	modChannel.registerMessage(ReloadFinished.Handler.class, ReloadFinished.class, id++, Side.CLIENT);
		modChannel.registerMessage(RightGunShoot.Handler.class, RightGunShoot.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunShoot.Handler.class, LeftGunShoot.class, id++, Side.SERVER);
		modChannel.registerMessage(RightGunReloadStarted.Handler.class, RightGunReloadStarted.class, id++, Side.SERVER);
		modChannel.registerMessage(LeftGunReloadStarted.Handler.class, LeftGunReloadStarted.class, id++, Side.SERVER);
		modChannel.registerMessage(OpenAttachmentInventory.Handler.class, OpenAttachmentInventory.class, id++, Side.SERVER);
		modChannel.registerMessage(RecoilLeftGun.Handler.class, RecoilLeftGun.class, id++, Side.CLIENT);
		modChannel.registerMessage(RecoilRightGun.Handler.class, RecoilRightGun.class, id++, Side.CLIENT);
		modChannel.registerMessage(UpdateCrafterToCraftItem.Handler.class, UpdateCrafterToCraftItem.class, id++, Side.SERVER);
		modChannel.registerMessage(OpenItemInventory.Handler.class, OpenItemInventory.class, id++, Side.SERVER);
		modChannel.registerMessage(DecreaseStamina.Handler.class, DecreaseStamina.class, id++, Side.SERVER);
		modChannel.registerMessage(IncreaseStamina.Handler.class, IncreaseStamina.class, id++, Side.SERVER);
	}
	
	public EntityPlayer getPlayer()
	{	
		return null;
	}

	public abstract EntityPlayer getPlayerFromContext(MessageContext ctx);

	public abstract long getTime();

	public abstract long getWorldTime();
	}
