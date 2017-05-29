package com.bubbletrouble.gunmod.common.testing;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.item.ItemEnchantedBook;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemInventoryGiver extends ItemEnchantedBook
{
	public ItemInventoryGiver(String name)
	{
		this.setCreativeTab(Main.tabGuns);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		GameRegistry.register(this);
	}

}
