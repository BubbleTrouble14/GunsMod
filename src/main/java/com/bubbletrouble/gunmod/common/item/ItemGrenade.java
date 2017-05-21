package com.bubbletrouble.gunmod.common.item;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.entity.EntityGrenade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemGrenade extends Item
{
	public ItemGrenade()
	{
		setCreativeTab(Main.tabGuns);
		setUnlocalizedName("grenade");
		setRegistryName("grenade");
		GameRegistry.register(this);
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer p, EnumHand hand)
	{
		if(!worldIn.isRemote)
		{
			EntityGrenade entitysnowball = new EntityGrenade(worldIn, p);
	     //   entitysnowball.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
	        worldIn.spawnEntity(entitysnowball);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, p.getHeldItemMainhand());
	}
}
	