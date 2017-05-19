package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.common.item.attachments.Flashable;
import com.bubbletrouble.gunmod.common.item.attachments.Laserable;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.item.attachments.Silenceable;

public class ItemLongneckRifle extends ItemRangedWeapon implements Scopeable, Silenceable, Laserable, Flashable
{
	public ItemLongneckRifle()
	{
		super("longneck_rifle", 350, 1, "simple_rifle_ammo", 1, 1, 7F, 0F, 10, 200, 2.5F, 5F, true, true);
	}

	@Override
	public int getReloadDuration()
	{
		return (int) (4.0 * 20.0);
	}
	
	@Override
	public void initModel() 
	{
		super.initModel();
	}

}
