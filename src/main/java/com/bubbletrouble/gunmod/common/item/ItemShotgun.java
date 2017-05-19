package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.entity.EntityProjectile;
import com.bubbletrouble.gunmod.common.item.attachments.NonSupporting;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShotgun extends ItemRangedWeapon implements NonSupporting
{
	public ItemShotgun()
	{
		super("shotgun", 200, 2, "simple_shotgun_ammo", 1, 0.8, 6F, 15F, 14, 5, 4F, 7F, true, true);
	}

	@Override
	public int getReloadDuration()
	{
		return (int) (3.0 * 20.0);
	}

	/*
	 * @Override public void effectReloadDone(ItemStack stack, World world,
	 * EntityPlayer player) { world.playSoundAtEntity(player,
	 * "random.door_close", 0.8F, 1.0F / (this.getItemRand() .nextFloat() * 0.2F
	 * + 0.0F)); }
	 */
	/*
	 * @Override public void soundCharge(ItemStack stack, World world,
	 * EntityPlayer player) { world.playSoundAtEntity(player, ARKCraft.MODID +
	 * ":" + "shotgun_reload", 0.7F, 0.9F / (getItemRand().nextFloat() * 0.2F +
	 * 0.0F)); }
	 */
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() 
	{
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName(), "inventory"));  
    }
	
	@Override
	public void fire(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			for (int i = 0; i < this.getAmmoConsumption() * 10; i++)
			{
				EntityProjectile projectile = createProjectile(stack, world, player);
				if (projectile != null)
				{
					applyProjectileEnchantments(projectile, stack);
					world.spawnEntityInWorld(projectile);
				}
			}
		}
		afterFire(stack, world, player);
	}
}
