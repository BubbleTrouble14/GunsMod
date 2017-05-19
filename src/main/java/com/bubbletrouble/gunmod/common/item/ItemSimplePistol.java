package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.common.item.attachments.Flashable;
import com.bubbletrouble.gunmod.common.item.attachments.Laserable;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.item.attachments.Silenceable;

public class ItemSimplePistol extends ItemRangedWeapon implements Scopeable, Laserable, Flashable, Silenceable
{
	public ItemSimplePistol()
	{
		super("simple_pistol", 150, 6, "simple_bullet", 1, 0.476, 5F, 2.5F, 3, 20, 2.5F, 5F, true, false);
	}

	@Override
	public void initModel() 
	{
		super.initModel();
	}

	@Override
	public int getReloadDuration()
	{
		return (int) (2.5 * 20.0);
	}
}
