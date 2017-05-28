package com.bubbletrouble.gunmod.common.testing;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler 
{
    public static final ResourceLocation Storage = new ResourceLocation(Main.MODID, "storage");

	public static ICapabilityProvider createProvider(int size) {
		return new InventoryCapability(size);
	}
    
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Item event)
    {

    	if(event.getItemStack().isItemEnchanted())
    	{
    		event.addCapability(Storage, createProvider(1));
    	}
    }  
}
