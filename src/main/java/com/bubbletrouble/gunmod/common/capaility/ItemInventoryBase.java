package com.bubbletrouble.gunmod.common.capaility;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.common.testing.InventoryCapability;

import net.minecraft.client.renderer.block.model.ModelBakery;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemInventoryBase extends Item
{
	public int invSize;
	
	public ItemInventoryBase(String name, int invSize)
	{
		this.invSize = invSize;
		this.setCreativeTab(Main.tabGuns);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		GameRegistry.register(this);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new InventoryCapability(invSize, stack, nbt);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) 
	{
		if(entity instanceof EntityPlayer && !entity.world.isRemote)
		{
			EntityPlayer p = (EntityPlayer) entity;
			IItemHandler handler = p.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	//		System.out.println(stack);
		//	System.out.println(handler);
		//	System.out.println(handler.getStackInSlot(0));
		}
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() 
	{
		ModelResourceLocation scope = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_scope", "inventory");
	    ModelResourceLocation laser = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_laser", "inventory");
	    ModelResourceLocation silencer = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_silencer", "inventory");
	    ModelResourceLocation flashlight = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_flashlight", "inventory");
	    ModelResourceLocation reload = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_flashlight", "inventory");
	    ModelResourceLocation normal = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() , "inventory");
	        
	    ModelBakery.registerItemVariants(this, scope, laser, silencer, flashlight, reload, normal);
	    
	    ModelLoader.setCustomMeshDefinition(this, stack -> 
		{
			if(getAttachment(stack) == "SCOPE")
			{
				return scope;
			}
			else if(getAttachment(stack) == "LASER")
			{
				System.out.println("working");
				return laser;
			}
			else if(getAttachment(stack) == "FLASH")
			{
				return flashlight;
			}
			else if(getAttachment(stack) == "SILENCER")
			{
				return silencer;
			}
			else return normal;
		});
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
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1; 
	}
	
	//NBT
	private void checkNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
	}
	
	public String getAttachment(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getString("attachment");
	}

	public void setAttachment(ItemStack stack, String attachment) {
		stack.getTagCompound().setString("attachment", attachment);
	}

}
