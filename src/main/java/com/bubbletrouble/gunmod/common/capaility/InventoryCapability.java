package com.bubbletrouble.gunmod.common.capaility;

import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;
import com.bubbletrouble.gunmod.common.item.attachments.Flashable;
import com.bubbletrouble.gunmod.common.item.attachments.HoloScopeable;
import com.bubbletrouble.gunmod.common.item.attachments.ItemAttachment;
import com.bubbletrouble.gunmod.common.item.attachments.Laserable;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.item.attachments.Silenceable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryCapability implements ICapabilitySerializable<NBTBase> {

	protected BubbleStackHandler inputSlot;
	private final ItemStack invStack;

	public InventoryCapability(int size, ItemStack stack, NBTTagCompound nbt)
	{
		inputSlot = new BubbleStackHandler(size);
		inputSlot.setStackInSlot(0, new ItemStack(Items.APPLE));
		this.invStack = stack;
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
	
	public void onContentsChanged(int slot)
	{
		inputSlot.onContentsChanged(slot);
	}
	
	private boolean isInvOfType(AttachmentType type)
	{
		System.out.println(inputSlot.getStackInSlot(0));
		return inputSlot.getStackInSlot(0) != ItemStack.EMPTY && ((ItemAttachment) inputSlot.getStackInSlot(0).getItem()).getType().equals(type);
	}
	
	public boolean isScopePresent()
	{
		return isInvOfType(AttachmentType.SCOPE);
	}

	public boolean isFlashPresent()
	{
		return isInvOfType(AttachmentType.FLASH);
	}

	public boolean isLaserPresent()
	{
		return isInvOfType(AttachmentType.LASER);
	}

	public boolean isSilencerPresent()
	{
		return isInvOfType(AttachmentType.SILENCER);
	}

	public boolean isHoloScopePresent()
	{
		return isInvOfType(AttachmentType.HOLO_SCOPE);
	}
	
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		if (stack.getItem() instanceof ItemAttachment)
		{
			ItemAttachment item = (ItemAttachment) stack.getItem();
			Item inv = invStack.getItem();
			switch (item.getType())
			{
				case SCOPE:
					return inv instanceof Scopeable;
				case HOLO_SCOPE:
					return inv instanceof HoloScopeable;
				case FLASH:
					return inv instanceof Flashable;
				case LASER:
					return inv instanceof Laserable;
				case SILENCER:
					return inv instanceof Silenceable;
			}
		}
		return false;
	}
	
	
}