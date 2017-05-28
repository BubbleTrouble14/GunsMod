package com.bubbletrouble.gunmod.common.capaility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TestContainer extends CommonContainer
{
	/** The Item Inventory for this Container */
	private final int ATTACHMENT_SLOT_COUNT = 1;
	public static final int ATTACHMENT_SLOT_YPOS = 30;
	public static final int ATTACHMENT_SLOT_XPOS = 79;

	public TestContainer(InventoryPlayer invPlayer, EntityPlayer p)
	{
		super(invPlayer, p);
		
		IItemHandler inv = p.getHeldItem(EnumHand.MAIN_HAND).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		
		for (int col = 0; col < ATTACHMENT_SLOT_COUNT; col++)
		{
			int slotIndex = col;
			addSlotToContainer(new SlotAttachment(inv, slotIndex, ATTACHMENT_SLOT_XPOS + col * 18,
					ATTACHMENT_SLOT_YPOS));
		
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		final Slot slot = this.inventorySlots.get(index);
		ItemStack originalStack = ItemStack.EMPTY;

		if (slot != null && !slot.getStack().isEmpty())
		{
			ItemStack stack = slot.getStack();
			originalStack = stack.copy();

			if (index < ATTACHMENT_SLOT_COUNT)
			{
				if (!this.mergeItemStack(stack, ATTACHMENT_SLOT_COUNT, 37, true)) { return ItemStack.EMPTY; }

				slot.onSlotChange(stack, originalStack);
			}
			else
			{
				if (index >= ATTACHMENT_SLOT_COUNT)
				{
					if (!this.mergeItemStack(stack, 0, ATTACHMENT_SLOT_COUNT, false)) 
					{ 
						return ItemStack.EMPTY; 
					}
				}
			}

			if (stack.getCount() == 0)
			{
				slot.putStack((ItemStack) ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (stack.getCount() == originalStack.getCount()) 
			{
				return ItemStack.EMPTY; 
			}	
			
			slot.onTake(player, stack);
			
			}
		return originalStack;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}

	private class SlotAttachment extends SlotItemHandler 
	{

		public SlotAttachment(IItemHandler inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return !(stack.getItem() instanceof ItemTest);
		}

	}
}