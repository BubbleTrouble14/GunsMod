package com.bubbletrouble.gunmod.common.container;

import net.minecraft.world.World;

public interface ICrafter extends IInventoryAdder, NBTable
{
	public default void updateCrafter()
	{
		updateCrafting();
		World world = getWorldIA();
		if(!world.isRemote)
		{
			
		}
		
	}

	public default void updateCrafting()
	{
		
	}
}
