package com.bubbletrouble.gunmod.a;

import com.bubbletrouble.gunmod.common.entity.EntityProjectile;

import net.minecraft.entity.player.EntityPlayer;

public interface IAmmunition {
	public EntityProjectile createProjectile(EntityPlayer player);
}
