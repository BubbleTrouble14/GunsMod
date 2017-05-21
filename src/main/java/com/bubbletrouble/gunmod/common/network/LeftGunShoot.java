package com.bubbletrouble.gunmod.common.network;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.utils.SoundUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class LeftGunShoot implements IMessage
{
	public LeftGunShoot()
	{

	}

	@Override
	public void fromBytes(ByteBuf buf)
	{}

	@Override
	public void toBytes(ByteBuf buf)
	{}

	public static class Handler implements IMessageHandler<LeftGunShoot, IMessage>
	{
		@Override
		public IMessage onMessage(final LeftGunShoot message, MessageContext ctx)
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

	static void processMessage(LeftGunShoot message, EntityPlayerMP player)
	{
		if (player != null)
		{
			ItemStack stack = player.getHeldItemOffhand();
			if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
				String soundPath = Main.MODID + ":" + weapon.getUnlocalizedName() + "_shoot";
	
				weapon.fire(stack, player.world, player);
				player.setActiveHand(EnumHand.OFF_HAND);
				weapon.setFired(stack, player, true);
				//weapon.afterFire(stack, player.world, player);

				SoundUtil.playSound(player.world, player.posX, player.posY, player.posZ, new ResourceLocation(soundPath), SoundCategory.PLAYERS, 1.5F, 1F / (weapon.getItemRand().nextFloat() * 0.4F + 0.7F), false);
			}
		}
	}

}
