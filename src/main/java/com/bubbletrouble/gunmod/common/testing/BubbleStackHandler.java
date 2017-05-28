package com.bubbletrouble.gunmod.common.testing;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class BubbleStackHandler extends ItemStackHandler
{
	public BubbleStackHandler(int size)
	{
		super(size);
	}
	
	@Override
	public int getSlotLimit(int slot)
	{	
		return 1;
	}
	
	@Override
	protected void onContentsChanged(int slot)
	{
		super.onContentsChanged(slot);
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return super.getStackInSlot(slot);
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
	}
	
	@Override
	public int getSlots() {
		return super.getSlots();
	}
	
	@Override
	public NBTTagCompound serializeNBT() 
	{
		return super.serializeNBT();
	}
	
	@Override
	public void setSize(int size) 
	{
		super.setSize(size);
	}
	
	@Override
	protected void validateSlotIndex(int slot) 
	{
		super.validateSlotIndex(slot);
	}
}
