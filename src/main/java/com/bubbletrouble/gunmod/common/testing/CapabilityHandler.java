package com.bubbletrouble.gunmod.common.testing;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.cap.IStamina;
import com.bubbletrouble.gunmod.common.cap.StaminaCapability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CapabilityHandler 
{
    public static final ResourceLocation Storage = new ResourceLocation(Main.MODID, "storage");
    public static final ResourceLocation Speed = new ResourceLocation(Main.MODID, "Speed");


	public static ICapabilityProvider createProvider(int size) {
		return new InventoryCapability(size);
	}
    
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Item event)
    {
    	if(!event.getItemStack().isEmpty())
    	{
    		if(event.getItemStack().getItem() instanceof ItemSword || event.getItemStack().getItem() instanceof ItemTool)
    		{
    	    	event.addCapability(Storage, createProvider(1)); 	
    		}
    	}
    }  
    
    @SubscribeEvent
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        	event.addCapability(Speed, new StaminaCapability(300F));   	
    }  
    
    int stackCount; 
    
    @SubscribeEvent   
    public void onPlayerJoin(PlayerLoggedInEvent evt)
    {
    	EntityPlayer p = evt.player;
		IStamina stam = p.getCapability(StaminaCapability.Stamina, null);
		stam.setStamina(300);
    }
    
    private float sprintingValue = 15;
    private float walkingValue = 5;
    private float sneakingValue = 2;
    private float standingValue = 20;
    private float jumpingValue = 20;
    
    @SubscribeEvent   
    public void onPlayerUpdate(PlayerTickEvent evt)
    {
    	EntityPlayer p = evt.player;
		IStamina stam = p.getCapability(StaminaCapability.Stamina, null);
		
	//	if(evt.phase == Phase.END && !p.world.isRemote)System.out.println(stam.getStamina());
		
    	if(evt.phase == Phase.END) //&& p.world.isRemote)
    	{
//    		System.out.println(stam.getStamina());

	    	if(isPlayerMoving(evt, p))
	    	{
	    		if(p.isSprinting())
	    		{
	    			if(DelayInHalfaSecond(evt))	stam.decreaseStamina(sprintingValue);	
	    		}
	    		else if(p.isSneaking())
	    		{
	    			if(DelayInHalfaSecond(evt))stam.decreaseStamina(sneakingValue);	
	    		}
	    		else 
	    		{
	    			if(DelayInHalfaSecond(evt))stam.decreaseStamina(walkingValue);		
	    		} 
	    	}
	    	else 
	    	{
	    		if(DelayInHalfaSecond(evt))stam.decreaseStamina(standingValue);		
	    	} 
	    	if(!p.onGround && !p.capabilities.isFlying)
	    	{    	
	    		jumped = true;
	    		if(jumped)
	    		{
	    			if(DelayInHalfaSecond(evt))stam.decreaseStamina(jumpingValue);		
	    			jumped = false;
	    		}
	    	}
    	}
    }
    
    public static float ticks;
    public boolean jumped;
    
    public boolean isPlayerMoving(PlayerTickEvent evt, EntityPlayer p)
    {
    	if(p.moveForward != 0)
    	{
    		return true;
    	}    		
	    return false;   
    }

	public boolean DelayInHalfaSecond(PlayerTickEvent evt)
    {
        ticks++;
        if(ticks >= 10)
        {
        	ticks = 0;
        	return true;
        }
		return false;     	
    }
    	
}
