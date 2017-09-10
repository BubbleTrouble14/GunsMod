package com.bubbletrouble.gunmod.a;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public abstract class ItemAmmunition extends Item implements IAmmunition {
	public ItemAmmunition(ResourceLocation name) {
		this.setRegistryName(name);
	}
}
