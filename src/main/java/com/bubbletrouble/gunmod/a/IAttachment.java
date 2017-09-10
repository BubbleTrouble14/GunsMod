package com.bubbletrouble.gunmod.a;

import javax.annotation.Nonnull;

import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAttachment {
	public void runEffect(World world, EntityPlayer player);

	public void runUseEffect(World world, EntityPlayer player);

	@SideOnly(Side.CLIENT)
	public void runClientSideEffect(World world, EntityPlayer player);

	@SideOnly(Side.CLIENT)
	public void runClientSideUseEffect(World world, EntityPlayer player);

	public @Nonnull AttachmentType getType();
}
