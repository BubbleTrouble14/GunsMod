package com.bubbletrouble.gunmod.common.handlers;

import com.bubbletrouble.gunmod.client.gui.GUIAttachment;
import com.bubbletrouble.gunmod.common.container.ContainerInventoryAttachment;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		
		if (id == CommonProxy.GUI.ATTACHMENTS.id)
		{
			return new ContainerInventoryAttachment(player, player.inventory, InventoryAttachment.create(player.getHeldItemMainhand()));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == CommonProxy.GUI.ATTACHMENTS.id)
		{
			return new GUIAttachment(player, player.inventory, InventoryAttachment.create(player.getHeldItemMainhand()));
		}
		return null;
	}
}
