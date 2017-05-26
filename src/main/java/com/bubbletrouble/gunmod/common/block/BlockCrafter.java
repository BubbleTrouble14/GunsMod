package com.bubbletrouble.gunmod.common.block;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.tileentity.TECrafter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrafter extends BlockContainer
{
	public BlockCrafter()
	{
		super(Material.WOOD);
		this.setHardness(0.5F);
		this.setCreativeTab(Main.tabGuns);
		this.setUnlocalizedName("block_crafter");
		this.setRegistryName("block_crafter");
		GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
	//	return null;
		return new TECrafter();
	}
	
    @SideOnly(Side.CLIENT)
    public void initModel() 
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
    		ItemStack stack) 
    {
    	TECrafter.setStacksEmpty();
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
    		EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if (!playerIn.isSneaking())
		{
			playerIn.openGui(Main.instance(), 1, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
			return true;
		}
		return false;
    }
}
