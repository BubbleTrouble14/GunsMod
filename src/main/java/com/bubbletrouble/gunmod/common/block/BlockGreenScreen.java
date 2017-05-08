package com.bubbletrouble.gunmod.common.block;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGreenScreen extends Block
{

	public BlockGreenScreen()
	{
		super(Material.GROUND);
		this.setLightLevel(0.9F);
		this.setCreativeTab(Main.tabGuns);
		this.setUnlocalizedName("green_screen");
		this.setRegistryName("green_screen");
		GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
	}
	
    @SideOnly(Side.CLIENT)
    public void initModel() 
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
    
}
