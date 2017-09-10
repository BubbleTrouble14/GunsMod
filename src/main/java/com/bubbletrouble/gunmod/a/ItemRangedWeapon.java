package com.bubbletrouble.gunmod.a;

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
		if (getAmmunition(stack) == 0) {
			// Reload
			for (ItemStack s : player.inventory.mainInventory) {
				if (!s.isEmpty() && s.getItem() instanceof ItemAmmunition) {
					if (isValidAmmunition((ItemAmmunition) s.getItem())) {
						consumeAmmunition(player, stack, (ItemAmmunition) s.getItem());
						break;
					}
				}
			}
		} else {
			// Shoot
			setAmmunition(stack, getAmmunition(stack) - 1);
			world.spawnEntity(getAmmunitionItem(stack).createProjectile(player));
		}

		return super.onItemRightClick(world, player, hand);
	}
}
