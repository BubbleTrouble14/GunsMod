	package com.bubbletrouble.gunmod.common.network;

import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RightGunReloadStarted implements IMessage
{
	public RightGunReloadStarted()
	{

	}

	@Override
	public void fromBytes(ByteBuf buf)
	{}

	@Override
	public void toBytes(ByteBuf buf)
	{}

	public static class Handler implements IMessageHandler<RightGunReloadStarted, IMessage>
	{
		@Override
		public IMessage onMessage(final RightGunReloadStarted message, MessageContext ctx)
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

	static void processMessage(RightGunReloadStarted message, EntityPlayerMP player)
	{
		if (player != null)
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
				weapon.setReloading(stack, player, true);
				weapon.soundCharge(stack, player.world, player);
			}
		}
	}

}
