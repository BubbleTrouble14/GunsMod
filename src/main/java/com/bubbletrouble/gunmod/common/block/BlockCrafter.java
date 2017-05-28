package com.bubbletrouble.gunmod.common.block;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.tileentity.TECrafter;

import net.minecraft.block.Block;
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

public class BlockCrafter extends Block 
{
    private int guiID;
	
	public BlockCrafter(int guiID)
	{
		super(Material.WOOD);
		this.guiID = guiID;
		this.setHardness(0.5F);
		this.setCreativeTab(Main.tabGuns);
		this.setUnlocalizedName("block_crafter");
		this.setRegistryName("block_crafter");
		GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
	}
	
	@Override
	 public TileEntity createTileEntity(World world, IBlockState state)
	 {
		 return new TECrafter();
	 }
	
	@Override
	public boolean hasTileEntity(IBlockState state) 
	{
		return true; 
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
  //  	TECrafter.setStacksEmpty();
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
    		EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
			playerIn.openGui(Main.instance(), guiID, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
			return true;		
    }
}
