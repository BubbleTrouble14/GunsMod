package com.bubbletrouble.gunmod.common.item.attachments;

/**
 * @author Lewis_McReu
 */
public enum AttachmentType {
	SCOPE(1), HOLO_SCOPE(2), FLASH(3), LASER(4), SILENCER(5);

	private final int id;

	AttachmentType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
