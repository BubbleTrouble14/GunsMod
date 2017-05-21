package com.bubbletrouble.gunmod.utils;

import com.bubbletrouble.gunmod.init.RangedWeapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class GunTab extends CreativeTabs
{

	public GunTab(String label)
	{
		super(label);
	//	setBackgroundImageName("arkcraft.png");
	}

	@Override
	public boolean hasSearchBar()
	{
		return true;
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(RangedWeapons.fabricated_pistol);
	}
}
