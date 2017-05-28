package com.bubbletrouble.gunmod.common.capaility;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;
import com.bubbletrouble.gunmod.common.item.attachments.Flashable;
import com.bubbletrouble.gunmod.common.item.attachments.HoloScopeable;
import com.bubbletrouble.gunmod.common.item.attachments.ItemAttachment;
import com.bubbletrouble.gunmod.common.item.attachments.Laserable;
import com.bubbletrouble.gunmod.common.item.attachments.Scopeable;
import com.bubbletrouble.gunmod.common.item.attachments.Silenceable;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.common.testing.InventoryCapability;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemContainer extends Item implements Scopeable
{			
	private static final int INVENTORY_SIZE = 1;
	IItemHandler handler;
	
	public ItemContainer()
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
				handler = inv;
				if(!(inv.getStackInSlot(0).isEmpty()))
				{
					if(isScopePresent())
					{
						System.out.println("can scope");
					}
					else if(isFlashPresent())
					{
						System.out.println("flashlight");
					}
				}
			}
		}
	}
	
	private boolean isInvOfType(AttachmentType type)
	{
		return handler.getStackInSlot(0) != ItemStack.EMPTY && ((ItemAttachment) handler.getStackInSlot(0).getItem()).getType().equals(type);
	}

	public boolean isScopePresent()
	{
		return isInvOfType(AttachmentType.SCOPE);
	}

	public boolean isFlashPresent()
	{
		return isInvOfType(AttachmentType.FLASH);
	}

	public boolean isLaserPresent()
	{
		return isInvOfType(AttachmentType.LASER);
	}

	public boolean isSilencerPresent()
	{
		return isInvOfType(AttachmentType.SILENCER);
	}

	public boolean isHoloScopePresent()
	{
		return isInvOfType(AttachmentType.HOLO_SCOPE);
	}
	
	public boolean setActiveAttachment(ItemStack stack)
	{
		if (handler.getStackInSlot(0).getItem() instanceof ItemAttachment)
		{
			Item inv = stack.getItem();
			ItemAttachment item = (ItemAttachment) handler.getStackInSlot(0).getItem();
			switch (item.getType())
			{
				case SCOPE:
					return inv instanceof Scopeable;
				case HOLO_SCOPE:
					return inv instanceof HoloScopeable;
				case FLASH:
					return inv instanceof Flashable;
				case LASER:
					return inv instanceof Laserable;
				case SILENCER:
					return inv instanceof Silenceable;
			}
		}
		return false;
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
	
	public boolean getAttachment(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getBoolean("model");
	}

	public void setUpdateModel(ItemStack stack, EntityPlayer player, boolean model) {
		stack.getTagCompound().setBoolean("model", model);
	}

}
