package com.bubbletrouble.gunmod.common.container;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTable
{
	public NBTTagCompound writeToNBT(NBTTagCompound compound);

	public void readFromNBT(NBTTagCompound compound);
}
