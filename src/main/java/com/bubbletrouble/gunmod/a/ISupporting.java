package com.bubbletrouble.gunmod.a;

import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;

import net.minecraft.item.ItemStack;

public interface ISupporting {
	public boolean supportsAttachment(AttachmentType type);

	public ItemStack getAttachment(ItemStack itemStack);
	
	public void setAttachment(ItemStack itemStack, ItemStack attachment);

//	public int getAttachmentCount();
}
