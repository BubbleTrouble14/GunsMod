package com.bubbletrouble.gunmod.a;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAttachment extends Container {
	/** The Item Inventory for this Container */
	private InventoryAttachment inventory;
	private final ItemRangedWeapon item;
	private final int ATTACHMENT_SLOT_COUNT = 1;
	public static final int ATTACHMENT_SLOT_YPOS = 30;
	public static final int ATTACHMENT_SLOT_XPOS = 79;

	public ContainerAttachment(EntityPlayer player, InventoryPlayer invPlayer, ItemStack stack) {
		this.inventory = new InventoryAttachment(stack);
		this.item = (ItemRangedWeapon) stack.getItem();
		if (ATTACHMENT_SLOT_COUNT != 1) {
			// LogHelper.error("Mismatched slot count in container(" + ATTACHMENT_SLOT_COUNT
			// + ") and CompostBinInventory (" + tileInventoryAttachment.getSizeInventory()
			// + ")");
		}
		for (int col = 0; col < ATTACHMENT_SLOT_COUNT; col++) {
			int slotIndex = col;
			addSlotToContainer(new SlotAttachment(this.inventory, slotIndex, ATTACHMENT_SLOT_XPOS + col * 18,
					ATTACHMENT_SLOT_YPOS));
		}

		/* Player inventory */
		final int PLAYER_INVENTORY_YPOS = 84;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int slotIndex = col + row * 9 + 9;
				addSlotToContainer(new Slot(invPlayer, slotIndex, 8 + col * 18, PLAYER_INVENTORY_YPOS + row * 18));
			}
		}

		/* Hotbar inventory */
		final int HOTBAR_YPOS = 142;
		for (int col = 0; col < 9; col++) {
			addSlotToContainer(new Slot(invPlayer, col, 8 + col * 18, HOTBAR_YPOS));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true; // return tileInventoryAttachment.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in our custom Inventory or an ARMOR slot
			if (index < ATTACHMENT_SLOT_COUNT) {
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, ATTACHMENT_SLOT_COUNT, 37, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in inventory / hotbar, try to place in custom inventory
			// or armor slots
			else {
				// item is in inventory or action bar
				if (index >= ATTACHMENT_SLOT_COUNT) {
					// place in custom inventory
					if (!this.mergeItemStack(itemstack1, 0, ATTACHMENT_SLOT_COUNT, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.getCount() == 0) {
				slot.putStack((ItemStack) ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
			// slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player) {
		// this will prevent the player from interacting with the item that
		// opened the inventory:
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		// TODO Fix the slotClick
		// return null;
		return super.slotClick(slot, button, flag, player);
	}

	// IMPORTANT to override the mergeItemStack method if your inventory stack
	// size limit is 1
	/**
	 * Vanilla method fails to account for stack size limits of one, resulting in
	 * only one item getting placed in the slot and the rest disappearing into thin
	 * air; vanilla method also fails to check whether stack is valid for slot
	 */
	@Override
	protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards) {
		boolean flag1 = false;
		int k = (backwards ? end - 1 : start);
		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!backwards && k < end || backwards && k >= start)) {
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += (backwards ? -1 : 1);
					continue;
				}

				if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem()
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
					int l = itemstack1.getCount() + stack.getCount();

					if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
						stack.setCount(0); // = 0;
						itemstack1.setCount(1); // = l;
						inventory.markDirty();
						flag1 = true;
					} else if (itemstack1.getCount() < stack.getMaxStackSize() && l < slot.getSlotStackLimit()) {
						stack.shrink(stack.getMaxStackSize() - itemstack1.getCount()); // -= stack.getMaxStackSize() -
						// itemstack1.getCount();
						itemstack1.setCount(stack.getMaxStackSize()); // = stack.getMaxStackSize();
						inventory.markDirty();
						flag1 = true;
					}
				}

				k += (backwards ? -1 : 1);
			}
		}

		if (stack.getCount() > 0) {
			k = (backwards ? end - 1 : start);

			while (!backwards && k < end || backwards && k >= start) {
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += (backwards ? -1 : 1);
					continue;
				}

				if (itemstack1.isEmpty()) {
					int l = stack.getCount();

					if (l <= slot.getSlotStackLimit()) {
						slot.putStack(stack.copy());
						stack.setCount(0);
						inventory.markDirty();
						flag1 = true;
						break;
					} else {
						putStackInSlot(k,
								new ItemStack(stack.getItem(), slot.getSlotStackLimit(), stack.getItemDamage()));
						stack.shrink(slot.getSlotStackLimit());
						inventory.markDirty();
						flag1 = true;
					}
				}

				k += (backwards ? -1 : 1);
			}
		}

		return flag1;
	}

	private class SlotAttachment extends Slot {

		public SlotAttachment(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return this.inventory.isItemValidForSlot(this.slotNumber, stack);
		}
	}
}