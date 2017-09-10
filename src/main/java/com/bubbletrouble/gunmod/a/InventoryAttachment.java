package com.bubbletrouble.gunmod.a;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InventoryAttachment implements IInventory {
	private ItemStack container;
	private ISupporting containerItem;
	private ItemStack stack;

	public InventoryAttachment() {
		this.stack = ItemStack.EMPTY;
	}

	public InventoryAttachment(@Nonnull ItemStack stack) {
		this();
		this.container = stack;
		this.containerItem = (ISupporting) stack.getItem();
		this.stack = containerItem.getAttachment(stack);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stack;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = getStackInSlot(index);
		if (!stack.isEmpty()) {
			return stack.splitStack(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.stack;
		this.stack = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		containerItem.setAttachment(this.container, this.stack);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IAttachment
				&& containerItem.supportsAttachment(((IAttachment) stack.getItem()).getType());
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
}
