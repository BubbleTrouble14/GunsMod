package com.bubbletrouble.gunmod.a;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;

public interface ISupporting {
	public boolean supportsAttachment(AttachmentType type);

	public ItemStack getAttachment(ItemStack itemStack);

	public void setAttachment(ItemStack itemStack, ItemStack attachment);

	// public int getAttachmentCount();

	public default Collection<AttachmentType> getSupportedAttachmentTypes() {
		List<AttachmentType> list = Lists.newArrayList(AttachmentType.values());
		Iterator<AttachmentType> it = list.iterator();
		while (it.hasNext()) {
			AttachmentType att = it.next();
			if (!supportsAttachment(att))
				it.remove();
		}
		return list;
	}

	public default AttachmentType getAttachmentType(ItemStack stack) {
		ItemStack att = getAttachment(stack);
		if (!att.isEmpty()) {
			return ((IAttachment) att.getItem()).getType();
		}
		return null;
	}
}
