package com.bubbletrouble.gunmod.a;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public abstract class ItemAttachment extends Item implements IAttachment {
	public ItemAttachment(ResourceLocation name) {
		super();
		setRegistryName(name);
	}

}
