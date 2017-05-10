package com.bubbletrouble.gunmod.events;

import org.lwjgl.input.Keyboard;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.client.handlers.ClientEventHandler;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
@SideOnly(Side.CLIENT)
public class KeyBindingEvent 
{
	private static KeyBinding reload, attachment;

	public static void init()
	{
		reload = new KeyBinding("key.arkcraft.reload", Keyboard.KEY_R, Main.MODID);
		ClientRegistry.registerKeyBinding(reload);
		
		attachment = new KeyBinding("key.attachment", Keyboard.KEY_M, Main.MODID);
		ClientRegistry.registerKeyBinding(attachment);

		ClientEventHandler h = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(h);
	}

	@SubscribeEvent
	public static void onPlayerKeypressed(InputEvent.KeyInputEvent event)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (reload.isPressed()) {
			doReload();
		}
		else if (attachment.isPressed()) {
			if(player.getHeldItemMainhand().getItem() != null && player.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
			{
				Main.modChannel.sendToServer(new OpenAttachmentInventory());
			}
		}
	}
	
	public static void doReload()
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		ItemStack rightHandStack = player.getHeldItemMainhand();
		ItemStack leftHandStack = player.getHeldItemOffhand();
		
		if(rightHandStack != null && leftHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon && leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();

				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player) && !leftWeapon.isReloading(leftHandStack))//leftWeapon.getAmmoQuantity(leftHandStack) != 0) 
				{
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
				else if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player) && !rightWeapon.isReloading(rightHandStack)) 
				{
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
			else if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
			}
			else if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		else if(leftHandStack != null)
		{
			if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		else if(rightHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();	
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) 
				{
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
			}
		}
		}
	}
	
}


