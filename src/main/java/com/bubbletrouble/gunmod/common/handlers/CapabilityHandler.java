package com.bubbletrouble.gunmod.common.handlers;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler 
{
    public static final ResourceLocation Storage = new ResourceLocation(Main.MODID, "storage");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Item event)
    {
     //   if (!(event.getEntity() instanceof EntityPlayer)) return;
   // 	event.addCapability(Storage, new StorageProvider());
       // event.addCapability(MANA_CAP, new ManaProvider());
    }
}
