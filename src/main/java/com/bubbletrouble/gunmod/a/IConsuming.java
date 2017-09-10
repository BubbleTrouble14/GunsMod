package com.bubbletrouble.gunmod.a;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public interface IConsuming {
	public boolean isValidAmmunition(ItemAmmunition item);

	public default void consumeAmmunition(EntityPlayer player, ItemStack stack, ItemAmmunition item) {
		int cap = getAmmunitionCapacity();
		NonNullList<ItemStack> inv = player.inventory.mainInventory;

		for (ItemStack i : inv) {
			if (i.getItem() == item) {
				setAmmunitionItem(stack, item);
				int amo = i.getCount();
				int con = amo > cap ? cap : amo;
				setAmmunition(stack, con + getAmmunition(stack));
				i.shrink(con);
				cap -= con;
				if (cap == 0)
					return;
			}
		}
	}

	public default int getAmmunition(ItemStack stack) {
		NBTTagCompound nbt = Util.getNBT(stack);
		return nbt.getInteger("ammunition");
	}

	public default void setAmmunition(ItemStack stack, int amount) {
		NBTTagCompound nbt = Util.getNBT(stack);
		nbt.setInteger("ammunition", amount);
	}

	public default ItemAmmunition getAmmunitionItem(ItemStack stack) {
		NBTTagCompound nbt = Util.getNBT(stack);
		String item = nbt.getString("ammunitionitem");
		return !item.isEmpty() ? (ItemAmmunition) Item.getByNameOrId(item) : null;
	}

	public default void setAmmunitionItem(ItemStack stack, ItemAmmunition ammunition) {
		NBTTagCompound nbt = Util.getNBT(stack);
		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(ammunition);
		nbt.setString("ammunitionitem", resourcelocation.toString());

	}

	public int getAmmunitionCapacity();
}
