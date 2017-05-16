package com.bubbletrouble.gunmod.common.network;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RecoilLeftGun implements IMessage
{
	
	public RecoilLeftGun()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}


	public static class Handler implements IMessageHandler<RecoilLeftGun, IMessage>
	{
		@Override
		public IMessage onMessage(final RecoilLeftGun message, MessageContext ctx)
		{
			if (ctx.side != Side.CLIENT)
			{
				System.err.println("ReloadFinished received on wrong side:" + ctx.side);
				return null;
			}
			processMessage(message, Main.proxy.getPlayerFromContext(ctx));
			return null;
		}

		static void processMessage(RecoilLeftGun message, EntityPlayer player)
		{
			if (player != null)
			{
				ItemStack stack = player.getHeldItemOffhand();
				if (stack != null && stack.getItem() instanceof ItemRangedWeapon) 
				{
					ItemRangedWeapon w = (ItemRangedWeapon) stack.getItem();
					w.recoilDown(player, w.getRecoil(), w.getRecoilSneaking(), w.getShouldRecoil());
				}
			}
		}
	}
}
