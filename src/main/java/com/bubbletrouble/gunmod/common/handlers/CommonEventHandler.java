package com.bubbletrouble.gunmod.common.handlers;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunFiredClient;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadFinished;
import com.bubbletrouble.gunmod.common.network.RightGunFiredClient;
import com.bubbletrouble.gunmod.common.network.RightGunReloadFinished;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonEventHandler 
{
	public static void init()
	{
		CommonEventHandler handler = new CommonEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
	}
	
	public static int reloadTicks = 0;
	public static int ticksExsisted = 0;
	public static int ticksSwing = 0;
	public static int ticks = 0;
	public static int leftticks = 0;
	public static int leftReloadTicks = 0;
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt)
	{
		if(evt.side == Side.SERVER)
		{
		EntityPlayer p = evt.player;
		ItemStack stackRight = p.getHeldItemMainhand();
		ItemStack stackLeft = p.getHeldItemOffhand();

		if (stackRight != null && stackRight.getItem() instanceof ItemRangedWeapon)
		{			
			ItemRangedWeapon w = (ItemRangedWeapon) stackRight.getItem();
			if (w.isReloading(stackRight))
			{
			//	System.out.println(reloadTicks);
				if (++reloadTicks >= w.getReloadDuration())
				{
					if (!p.worldObj.isRemote)
					{
						w.setReloading(stackRight, p, false);
						reloadTicks = 0;
						w.hasAmmoAndConsume(stackRight, p);
						w.effectReloadDone(stackRight, p.worldObj, p);
						Main.modChannel.sendTo(new RightGunReloadFinished(), (EntityPlayerMP) p);
					}
				}
			}
			if(w.fired(stackRight))
			{
				++ticks;
				if(ticks >= w.recoilDelay() + 5)
				{
					if (!p.worldObj.isRemote)
					{
						Main.modChannel.sendTo(new RightGunFiredClient(), (EntityPlayerMP) p);
						ticks = 0;
						w.setFired(stackRight, p, false);
					}
				}
			}		
		}			
			if (stackLeft != null && stackLeft.getItem() instanceof ItemRangedWeapon)
			{			
				ItemRangedWeapon leftgun = (ItemRangedWeapon) stackLeft.getItem();
				if (leftgun.isReloading(stackLeft))
				{
					if (++leftReloadTicks >= leftgun.getReloadDuration())
					{
						if (!p.worldObj.isRemote)
						{
							leftgun.setReloading(stackLeft, p, false);
							leftReloadTicks = 0;
							leftgun.hasAmmoAndConsume(stackLeft, p);
							leftgun.effectReloadDone(stackLeft, p.worldObj, p);
							Main.modChannel.sendTo(new LeftGunReloadFinished(), (EntityPlayerMP) p);
						}
					}
				}
				if(leftgun.fired(stackLeft))
				{
					++leftticks;
					if(leftticks >= leftgun.recoilDelay() + 5)
					{
						if (!p.worldObj.isRemote)
						{
							Main.modChannel.sendTo(new LeftGunFiredClient(), (EntityPlayerMP) p);
							leftticks = 0;
							leftgun.setFired(stackLeft, p, false);	
						}
					}			
			}
			}
		}
	}

}
