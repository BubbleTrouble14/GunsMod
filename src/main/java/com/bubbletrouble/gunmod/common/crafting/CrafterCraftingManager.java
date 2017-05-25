package com.bubbletrouble.gunmod.common.crafting;

import com.bubbletrouble.gunmod.init.RangedWeapons;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author wildbill22 Notes about adding recipes: 1) If a block has meta data:
 *         a) If not enter in ItemStack as 3rd param, it is set to 0 b) If
 *         entered, then just a stack with that meta will match c) If
 *         ARKShapelessRecipe.ANY (32767) is used, all the different meta types
 *         for the block will match 2) Do not have two recipes for the same
 *         ItemStack, only the first will be used
 */
public class CrafterCraftingManager extends CraftingManager
{

	private static CrafterCraftingManager instance = null;

	public CrafterCraftingManager()
	{
		super();
		instance = this;
	}

	public static CrafterCraftingManager getInstance()
	{
		if (instance == null)
		{
			instance = new CrafterCraftingManager();
		}
		return instance;
	}

	public static void registerCraftingRecipes()
	{
		getInstance().addShapelessRecipe(new ItemStack(RangedWeapons.simple_pistol, 1),
				new ItemStack(Items.GUNPOWDER, 5));
	
	}
}
