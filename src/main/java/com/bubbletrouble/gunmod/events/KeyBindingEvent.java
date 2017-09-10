package com.bubbletrouble.gunmod.events;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.a.ISupporting;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.OpenItemInventory;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;
import com.bubbletrouble.gunmod.utils.KeyBindings;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyBindingEvent {
	@SubscribeEvent
	public static void onPlayerKeypressed(InputEvent.KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (KeyBindings.reload.isPressed()) {
			doReload();
		} else if (KeyBindings.attachment.isPressed()) {
			// if (player.getHeldItemMainhand() == null || event.getStack().isEmpty())
			// return;
			ItemStack stack;
			// ItemStack.field_190927_a.;
			if (player.getHeldItemMainhand() != null
					&& (player.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon
							|| player.getHeldItemMainhand().getItem() instanceof ISupporting)) {
				Main.modChannel.sendToServer(new OpenAttachmentInventory());
			}
		} else if (KeyBindings.inventory.isPressed()) {
			// if (player.getHeldItemMainhand() == null || event.getStack().isEmpty())
			// return;
			ItemStack stack;
			// ItemStack.field_190927_a.;
			if (player.getHeldItemMainhand().isItemEnchanted()) {
				Main.modChannel.sendToServer(new OpenItemInventory());
			}
		}
	}

	public static void doReload() {
		EntityPlayer player = Minecraft.getMinecraft().player;

		ItemStack rightHandStack = player.getHeldItemMainhand();
		ItemStack leftHandStack = player.getHeldItemOffhand();

		if (rightHandStack != null && leftHandStack != null) {
			if (rightHandStack.getItem() instanceof ItemRangedWeapon
					&& leftHandStack.getItem() instanceof ItemRangedWeapon) {
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();

				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)
						&& !leftWeapon.isReloading(leftHandStack))// leftWeapon.getAmmoQuantity(leftHandStack) != 0)
				{
					rightWeapon.soundCharge(rightHandStack, player.world, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				} else if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)
						&& !rightWeapon.isReloading(rightHandStack)) {
					leftWeapon.soundCharge(leftHandStack, player.world, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			} else if (rightHandStack.getItem() instanceof ItemRangedWeapon) {
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					rightWeapon.soundCharge(rightHandStack, player.world, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
			} else if (leftHandStack.getItem() instanceof ItemRangedWeapon) {
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.world, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		} else if (leftHandStack != null) {
			if (leftHandStack.getItem() instanceof ItemRangedWeapon) {
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.world, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		} else if (rightHandStack != null) {
			if (rightHandStack.getItem() instanceof ItemRangedWeapon) {
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					rightWeapon.soundCharge(rightHandStack, player.world, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
			}
		}
	}
}
