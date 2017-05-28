package com.bubbletrouble.gunmod.common.testing;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryCapability implements ICapabilitySerializable<NBTBase> {

	protected BubbleStackHandler inputSlot;
	
	public InventoryCapability(int size)
	{
		inputSlot = new BubbleStackHandler(size);
		inputSlot.setStackInSlot(0, new ItemStack(Items.APPLE));
	}

	public InventoryCapability(int size, ItemStack stack, NBTTagCompound nbt)
	{
		inputSlot = new BubbleStackHandler(size);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inputSlot;
		}
		return null;
	}

	@Override
	public NBTBase serializeNBT() {
		NBTTagCompound NBTBase = new NBTTagCompound();
		((NBTTagCompound)NBTBase).setTag("inputSlot", inputSlot.serializeNBT());
		return NBTBase;	
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		inputSlot.deserializeNBT(((NBTTagCompound) nbt).getCompoundTag("inputSlot"));	
	}
}