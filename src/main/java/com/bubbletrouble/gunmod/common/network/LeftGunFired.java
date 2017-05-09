package com.bubbletrouble.gunmod.common.network;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.utils.SoundUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class LeftGunFired implements IMessage
{
	public LeftGunFired()
	{

	}

	@Override
	public void fromBytes(ByteBuf buf)
	{}

	@Override
	public void toBytes(ByteBuf buf)
	{}

	public static class Handler implements IMessageHandler<LeftGunFired, IMessage>
	{
		@Override
		public IMessage onMessage(final LeftGunFired message, MessageContext ctx)
		{
			if (ctx.side != Side.SERVER)
			{
				System.err.println("MPUpdateDoReloadStarted received on wrong side:" + ctx.side);
				return null;
			}
			final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
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

	static void processMessage(LeftGunFired message, EntityPlayerMP player)
	{
		if (player != null)
		{
			ItemStack stack = player.getHeldItemOffhand();
			if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
			//	weapon.setFired(stack, player, true);
				String soundPath = Main.MODID + ":" + weapon.getUnlocalizedName() + "_shoot";
				weapon.fire(stack, player.worldObj, player, 0);
				player.setActiveHand(EnumHand.OFF_HAND);
			//	Main.modChannel.sendTo(new LeftGunFiredClient(), (EntityPlayerMP) player);
				weapon.setFired(stack, player, true);
				//TODO
			//	InventoryAttachment att = InventoryAttachment.create(stack);
			//	if (att != null && att.isSilencerPresent())
			//		soundPath = soundPath + "_silenced";
				//weapon.setFired(stack, player, true);
				SoundUtil.playSound(player.worldObj, player.posX, player.posY, player.posZ, new ResourceLocation(soundPath), SoundCategory.PLAYERS, 1.5F, 1F / (weapon.getItemRand().nextFloat() * 0.4F + 0.7F), false);
			}
		}
	}

}
