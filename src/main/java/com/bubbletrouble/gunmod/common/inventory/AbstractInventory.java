package com.bubbletrouble.gunmod.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Class aiming to minimize amount of code needed for an inventory. MarkDirty()
 * is called where appropriate, but not implemented (usually only TileEntities
 * need it). Default displays an empty string as the translated (i.e.
 * non-custom) name. I intentionally left out isUseableByPlayer and
 * getInventoryStackLimit because they tend to have custom rules; implementing
 * them here would make them easy to overlook, though you may choose to do so
 * (good default return values are 'true' and '64', respectively).
 */
public abstract class AbstractInventory implements IInventory
{
	/** The inventory slots need to be initialized during construction */
	protected NonNullList<ItemStack> inventory;

	@Override
	public int getSizeInventory()
	{
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			if (stack.getCount() > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			}
			else {
				setInventorySlotContents(slot, ItemStack.EMPTY);
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		inventory.set(slot, itemstack); //= itemstack;
		if (itemstack != ItemStack.EMPTY && itemstack.getCount() > getInventoryStackLimit()) 
		{
			//set stack size
			itemstack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public void markDirty()
	{
	} // usually only TileEntities implement this method

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < inventory.size(); ++i) {
			inventory.set(i, ItemStack.EMPTY); // = null;
		}
	}

	/**
	 * Return unlocalized name here, or pre-translated and return true for
	 * hasCustomName()
	 */
	@Override
	public String getName()
	{
		return "";
	}

	@Override
	public boolean hasCustomName()
	{
		return true;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}

	/**
	 * NBT key used to set and retrieve the NBTTagCompound containing this
	 * inventory
	 */
	protected abstract String getNbtKey();

	/**
	 * Writes this inventory to NBT; must be called manually Fails silently if
	 * {@link #getNbtKey} returns null or an empty string
	 */
	public void writeToNBT(NBTTagCompound compound)
	{
		String key = getNbtKey();
		if (key == null || key.equals("")) {
			return;
		}
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		compound.setTag(key, items);
	}

	/**
	 * Loads this inventory from NBT; must be called manually Fails silently if
	 * {@link #getNbtKey} returns null or an empty string
	 */
	public void readFromNBT(NBTTagCompound compound)
	{
		String key = getNbtKey();
		if (key == null || key.equals("")) { return; }
		NBTTagList items = compound.getTagList(key, compound.getId());
		for (int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory())
			{
				inventory.set(slot,  new ItemStack(item));// = new ItemStack(item);
			}
		}
	}
}