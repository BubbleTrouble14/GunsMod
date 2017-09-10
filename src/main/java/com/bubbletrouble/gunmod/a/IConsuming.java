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

	public static final String AMMUNITION_AMOUNT_KEY = "ammunition";
	public static final String AMMUNITION_TYPE_KEY = "ammunitionitem";
	public static final String RELOADING_PROGRESS = "reloadingtime";

	public default int getAmmunition(ItemStack stack) {
		NBTTagCompound nbt = Util.getNBT(stack);
		return nbt.getInteger(AMMUNITION_AMOUNT_KEY);
	}

	public default void setAmmunition(ItemStack stack, int amount) {
		NBTTagCompound nbt = Util.getNBT(stack);
		nbt.setInteger(AMMUNITION_AMOUNT_KEY, amount);
	}

	public default ItemAmmunition getAmmunitionItem(ItemStack stack) {
		NBTTagCompound nbt = Util.getNBT(stack);
		String item = nbt.getString(AMMUNITION_TYPE_KEY);
		return !item.isEmpty() ? (ItemAmmunition) Item.getByNameOrId(item) : null;
	}

	public default void setAmmunitionItem(ItemStack stack, ItemAmmunition ammunition) {
		NBTTagCompound nbt = Util.getNBT(stack);
		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(ammunition);
		nbt.setString(AMMUNITION_TYPE_KEY, resourcelocation.toString());
	}

	public default int getReloadingProgress(ItemStack stack) {
		NBTTagCompound nbt = Util.getNBT(stack);
		return nbt.getInteger(RELOADING_PROGRESS);
	}

	public default void setReloadingProgress(ItemStack stack, int value) {
		NBTTagCompound nbt = Util.getNBT(stack);
		nbt.setInteger(RELOADING_PROGRESS, value);
	}

	public default boolean isReloading(ItemStack stack) {
		return getReloadingProgress(stack) > 0;
	}

	public int getReloadingTime();

	public int getAmmunitionCapacity();
}
