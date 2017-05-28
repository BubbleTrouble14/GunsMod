package com.bubbletrouble.gunmod.common.testing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class CommonContainer extends Container 
{
    public CommonContainer(IInventory playerInventory, EntityPlayer player)
    {        
		//Player Inventory
        final int PLAYER_INVENTORY_YPOS = 84;
		for (int row = 0; row < 3; ++row) 
		{
			for (int col = 0; col < 9; ++col) 
			{
				int slotIndex = col + row * 9 + 9;
				this.addSlotToContainer(new Slot(playerInventory, slotIndex, 8 + col * 18, PLAYER_INVENTORY_YPOS + row * 18));
			}

			//Player Hotbar
			final int HOTBAR_YPOS = 142;
	        for (int col = 0; col < 9; ++col)
	        {
	            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, HOTBAR_YPOS));
	        }
		}
    }
	
	@Override	
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}