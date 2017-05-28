	package com.bubbletrouble.gunmod.common.network;

import com.bubbletrouble.gunmod.common.capaility.ItemInventoryBase;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.item.attachments.ItemAttachment;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class setAttachment implements IMessage
{
	public setAttachment()
	{

	}

	@Override
	public void fromBytes(ByteBuf buf)
	{}

	@Override
	public void toBytes(ByteBuf buf)
	{}

	public static class Handler implements IMessageHandler<setAttachment, IMessage>
	{
		@Override
		public IMessage onMessage(final setAttachment message, MessageContext ctx)
		{
			if (ctx.side != Side.SERVER)
			{
				System.err.println("MPUpdateDoReloadStarted received on wrong side:" + ctx.side);
				return null;
			}
			final EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServer().addScheduledTask(new Runnable()
			{
				public void run()
				{
					processMessage(message, player);
				}
			});
			return null;
		}
	}

	static void processMessage(setAttachment message, EntityPlayerMP player)
	{
		if (player != null)
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() instanceof ItemInventoryBase)
			{
				ItemInventoryBase weapon = (ItemInventoryBase) stack.getItem();
				IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(handler.getStackInSlot(0).getItem() instanceof ItemAttachment)
				{
					ItemAttachment item = (ItemAttachment) handler.getStackInSlot(0).getItem();
					System.out.println(item.getType());
					weapon.setAttachment(stack, item.getType().toString());
				}
				else weapon.setAttachment(stack, "empty");
			}
		}
	}

}
