package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.common.item.attachments.Flashable;
import com.bubbletrouble.gunmod.common.item.attachments.Laserable;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.item.attachments.Silenceable;

public class ItemSimplePistol extends ItemRangedWeapon implements Scopeable, Laserable, Flashable, Silenceable
{
	public ItemSimplePistol()
	{
		super("simple_pistol", 150, 6, "simple_bullet", 1, 0.476, 5F, 2.5F, 6, 20, 2.5F, 5F, true, false);
	}

	/*
	 * @Override public void soundCharge(ItemStack stack, World world,
	 * EntityPlayer player) { world.playSoundAtEntity(player, ARKCraft.MODID +
	 * ":" + "simple_pistol_reload", 0.7F, 0.9F / (getItemRand().nextFloat() *
	 * 0.2F + 0.0F)); }
	 */

	@Override
	public int getReloadDuration()
	{
		return (int) (2.5 * 20.0 * 2);
	}
}
