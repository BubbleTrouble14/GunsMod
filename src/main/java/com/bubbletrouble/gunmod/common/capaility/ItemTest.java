package com.bubbletrouble.gunmod.common.capaility;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.init.RangedWeapons;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemTest extends Item implements Scopeable
{			
	private static final int INVENTORY_SIZE = 1;
	
	public ItemTest()
	{
		setCreativeTab(Main.tabGuns);
		setUnlocalizedName("item_test");
		setRegistryName("item_test");
		setMaxStackSize(1);
		GameRegistry.register(this);
	}
    ModelResourceLocation scope = new ModelResourceLocation(Main.MODID + ":weapons/" + "fabricated_pistol" + "_scope", "inventory");

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) 
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if(isSelected)
		{
			if(entityIn instanceof EntityPlayer)
			{
				EntityPlayer p = (EntityPlayer) entityIn;
				IItemHandler inv = p.getHeldItem(EnumHand.MAIN_HAND).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(!(inv.getStackInSlot(0).isEmpty()))
				{
					if(inv.getStackInSlot(0).getItem() == RangedWeapons.scope && stack.getItem() instanceof Scopeable)
					{
						System.out.println("can scope");
						ModelLoader.setCustomModelResourceLocation(this, 0, scope); //(this, scope);
					}
					else {}
				}
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) 
	{
		if (!worldIn.isRemote)
		{
			playerIn.openGui(Main.instance(), CommonProxy.GUI.TEST.getID(), worldIn, 0, 0 ,0);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new InventoryCapability(INVENTORY_SIZE, stack, nbt);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1; 
	}
	
	private void checkNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
	}
	
	public boolean getUpdateModel(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getBoolean("model");
	}

	public void setUpdateModel(ItemStack stack, EntityPlayer player, boolean model) {
		stack.getTagCompound().setBoolean("model", model);
	}

}
