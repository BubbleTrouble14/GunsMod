package com.bubbletrouble.gunmod.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
/**
 * @author wildbill22
 *         Like InventoryBasic
 */
public class InventoryPlayerCrafting implements IInventory
{
    private String inventoryTitle;
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private boolean hasCustomName;

    public InventoryPlayerCrafting(String title, boolean customName, int slotCount)
    {
        this.inventoryTitle = title;
        this.hasCustomName = customName;
        this.slotsCount = slotCount;
        this.inventoryContents = new ItemStack[slotCount];
    }

    public void loadInventoryFromNBT(NBTTagCompound nbt)
    {
        final byte NBT_TYPE_COMPOUND = 10;
        NBTTagList dataForAllItems = nbt.getTagList("Items", NBT_TYPE_COMPOUND);
        loadInventoryFromNBT(dataForAllItems);
    }

    public void loadInventoryFromNBT(NBTTagList nbt)
    {
        int i;
        for (i = 0; i < this.getSizeInventory(); ++i)
        {
            this.setInventorySlotContents(i, (ItemStack) ItemStack.EMPTY);
        }
        for (i = 0; i < nbt.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.getSizeInventory())
            {
                this.setInventorySlotContents(j, new ItemStack(nbttagcompound));
            }
        }
    }

	public void saveInventoryToNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
//                LogHelper.info("InventoryPlayerCrafting: Saved a " + itemstack.getItem() + " to inventory.");
            }
        }
        nbt.setTag("Items", nbttaglist);
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    public ItemStack[] getItemStacks()
    {
        return inventoryContents;
    }

    @Override
    public String getName()
    {
        return this.inventoryTitle;
    }

    @Override
    public boolean hasCustomName()
    {
        return hasCustomName;
    }

    @Override
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (!this.inventoryContents[index].isEmpty())
        {
            ItemStack itemstack;
            if (this.inventoryContents[index].getCount() <= count)
            {
                itemstack = this.inventoryContents[index];
                this.inventoryContents[index] = ItemStack.EMPTY;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.inventoryContents[index].splitStack(count);
                if (this.inventoryContents[index].getCount() == 0)
                {
                    this.inventoryContents[index] = ItemStack.EMPTY;
                }
                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventoryContents[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit()); // = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < this.inventoryContents.length; ++i)
        {
            this.inventoryContents[i] = ItemStack.EMPTY;
        }
    }

	@Override
	public ITextComponent getDisplayName() {
        return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));

	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
        if (!this.inventoryContents[index].isEmpty())
        {
            ItemStack itemstack = this.inventoryContents[index];
            this.inventoryContents[index] = ItemStack.EMPTY;
            return itemstack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}
}
