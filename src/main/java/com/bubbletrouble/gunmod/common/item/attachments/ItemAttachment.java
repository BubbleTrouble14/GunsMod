package com.bubbletrouble.gunmod.common.item.attachments;

import net.minecraft.item.Item;

/**
 * @author Lewis_McReu
 */
public class ItemAttachment extends Item
{
	private final AttachmentType type;

	public ItemAttachment(String name, AttachmentType type)
	{
		this.type = type;
	}

	public AttachmentType getType()
	{
		return this.type;
	}
}
