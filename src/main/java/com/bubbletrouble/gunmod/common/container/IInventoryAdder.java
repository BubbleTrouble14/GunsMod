package com.bubbletrouble.gunmod.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IInventoryAdder 
{
//	public default void addOrDrop(ItemStack stack)
//	{
//		InventoryUtil.addOrDrop(stack, getIInventory(), getPosition(), getWorldIA());
//	}

	public IInventory getIInventory();

	public BlockPos getPosition();

	public World getWorldIA();
}
