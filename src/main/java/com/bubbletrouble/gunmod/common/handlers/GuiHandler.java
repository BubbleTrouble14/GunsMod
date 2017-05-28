package com.bubbletrouble.gunmod.common.handlers;

import com.bubbletrouble.gunmod.client.gui.GUIAttachment;
import com.bubbletrouble.gunmod.client.gui.GuiCrafter;
import com.bubbletrouble.gunmod.common.container.ContainerCrafter;
import com.bubbletrouble.gunmod.common.container.ContainerInventoryAttachment;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.common.testing.GuiTest;
import com.bubbletrouble.gunmod.common.testing.TestContainer;
import com.bubbletrouble.gunmod.common.tileentity.TECrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
		else if (id == CommonProxy.GUI.CRAFTER.id)
		{
			BlockPos xyz = new BlockPos(x, y, z);
			TileEntity tileEntity = world.getTileEntity(xyz);
			if (tileEntity instanceof TECrafter)
			{
				return new ContainerCrafter(player.inventory,
						(TECrafter) tileEntity);
			}
			else
			{
			//	LogHelper
				//		.info("GuiHandler - getServerGuiElement: TileEntitySmithy not found!");
			}
		}
		else if (id == CommonProxy.GUI.TEST.id)
		{
			return new TestContainer(player.inventory, player);
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
		else if (id == CommonProxy.GUI.CRAFTER.id)
		{
			BlockPos xyz = new BlockPos(x, y, z);
			TileEntity tileEntity = world.getTileEntity(xyz);
			if (tileEntity instanceof TECrafter)
			{
				return new GuiCrafter(player.inventory,
						(TECrafter) tileEntity);
			}
			else
			{
			//	LogHelper
		//				.info("GuiHandler - getClientGuiElement: TileEntitySmithy not found!");
			}
		}
		else if (id == CommonProxy.GUI.TEST.id)
		{
			return new GuiTest(player.inventory, player);
		}
		return null;
	}
}
