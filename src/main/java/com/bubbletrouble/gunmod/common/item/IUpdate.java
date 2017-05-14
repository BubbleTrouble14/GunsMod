package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.network.RightGunFiredClient;
import com.bubbletrouble.gunmod.common.network.RightGunReloadFinished;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IUpdate 
{
	public default void rightStackUpdate(World worldIn, EntityPlayer p, ItemStack rightStack, int reloadTicks, int recoilTicks)
	{			
		if(!p.worldObj.isRemote)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)p;
			if (rightStack != null)
			{			
				ItemRangedWeapon w = (ItemRangedWeapon) rightStack.getItem();
				if (w.isReloading(rightStack))
				{
					w.setReloadTicks(rightStack, p, reloadTicks++);
					if (w.getReloadTicks(rightStack) >= w.getReloadDuration())
					{
						if (!p.worldObj.isRemote)
						{
							Main.modChannel.sendTo(new RightGunReloadFinished(), playerMP);
							w.setReloadTicks(rightStack, p, 0);
						}
					}
				}
				if(w.fired(rightStack))
				{
					w.setRecoilTicks(rightStack, p, recoilTicks++);
					if(w.getRecoilTicks(rightStack) >= w.recoilDelay() + 5)
					{
						if (!p.worldObj.isRemote)
						{
							Main.modChannel.sendTo(new RightGunFiredClient(), playerMP);
							w.setRecoilTicks(rightStack, p, 0);
						}
					}
				}		
			}	
		}
	}	
	
	public default void leftStackUpdate(World worldIn, EntityPlayer p, ItemStack leftStack, int ReloadTicks, int RecoilTicks)
	{		
		if(!p.worldObj.isRemote)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)p;
			if (leftStack != null)
			{			
				ItemRangedWeapon w = (ItemRangedWeapon) leftStack.getItem();
				if (w.isReloading(leftStack))
				{
					if (++ReloadTicks >= w.getReloadDuration())
					{
						if (!p.worldObj.isRemote)
						{
							Main.modChannel.sendTo(new RightGunReloadFinished(), playerMP);
							ReloadTicks = 0;
						}
					}
				}
				if(w.fired(leftStack))
				{
					++RecoilTicks;
					if(RecoilTicks >= w.recoilDelay() + 5)
					{
						if (!p.worldObj.isRemote)
						{
							Main.modChannel.sendTo(new RightGunFiredClient(), playerMP);
							RecoilTicks = 0;
						}
					}
				}		
			}	
		}
	}	
}
