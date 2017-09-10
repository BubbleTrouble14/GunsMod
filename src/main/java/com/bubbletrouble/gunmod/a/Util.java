package com.bubbletrouble.gunmod.a;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Util {
	public static NBTTagCompound getNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}
}
