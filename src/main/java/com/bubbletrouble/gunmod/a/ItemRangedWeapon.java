package com.bubbletrouble.gunmod.a;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class ItemRangedWeapon extends Item implements IConsuming, ISupporting {
	public ItemRangedWeapon(ResourceLocation name) {
		super();
		setMaxStackSize(1);
		setRegistryName(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!isReloading(stack)) {
			if (getAmmunition(stack) == 0) {
				// Reload
				this.setReloadingProgress(stack, getReloadingTime());
			} else {
				// Shoot
				setAmmunition(stack, getAmmunition(stack) - 1);
				world.spawnEntity(getAmmunitionItem(stack).createProjectile(player));
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		EntityPlayer player = (EntityPlayer) entityIn;
		if (isReloading(stack)) {
			setReloadingProgress(stack, getReloadingProgress(stack) - 1);
			if (getReloadingProgress(stack) == 0) {
				for (ItemStack s : player.inventory.mainInventory) {
					if (!s.isEmpty() && s.getItem() instanceof ItemAmmunition) {
						if (isValidAmmunition((ItemAmmunition) s.getItem())) {
							consumeAmmunition(player, stack, (ItemAmmunition) s.getItem());
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (!slotChanged && oldStack.getItem() == newStack.getItem())
			return false;
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
}
