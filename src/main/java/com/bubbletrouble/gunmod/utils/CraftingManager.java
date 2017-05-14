package com.bubbletrouble.gunmod.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;

public class CraftingManager 
{
	private static CraftingManager instance = new CraftingManager();

	public static CraftingManager instance()
	{
		return instance;
	}
	
	public static void init()
	{
		
	}
	
	private Set<CrafterRecipe> recipes;


	
	
	
	
	
	
	public static class CrafterRecipe implements Comparable<CrafterRecipe>
	{
		private static short idCounter = 0;
		private final short id;
		private final Item item;
		/**
		 * craftingTime is a base value to possibly be adjusted by the
		 * machine/player actually doing the crafting
		 */
		private final int amount, craftingTime;
		private final Map<Item, Integer> items;
		
		public CrafterRecipe(Item item, int amount, int craftingTime, Object... objects)
		{
			id = idCounter++;
			this.item = item;
			this.amount = amount;
			this.craftingTime = craftingTime;
			this.items = new HashMap<>();
			if (objects.length % 2 == 0)
			{
				for (int i = 0; i < objects.length; i += 2)
				{
					Object o1 = objects[i];
					Object o2 = objects[i + 1];
					if (o1 instanceof Item && o2 instanceof Integer)
					{
						Item it = (Item) o1;
						Integer am = (Integer) o2;
						this.addItem(it, am);
					}
				}
			}
		}
		
		private void addItem(Item i, int amount)
		{
			if (!this.items.containsKey(i)) this.items.put(i, amount);
			else this.items.put(i, items.get(i) + amount);
		}
		
		/**
		 * @return the id
		 */
		public short getId()
		{
			return id;
		}

		/**
		 * @return the item
		 */
		public Item getItem()
		{
			return item;
		}

		/**
		 * @return the amount
		 */
		public int getAmount()
		{
			return amount;
		}

		/**
		 * @return the craftingTime
		 */
		public int getCraftingTime()
		{
			return craftingTime;
		}
		
		/**
		 * @return the items
		 */
		public Map<Item, Integer> getItems()
		{
			return items;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof CrafterRecipe) return ((CrafterRecipe) obj).item == item;
			return false;
		}

		@Override
		public int compareTo(CrafterRecipe o)
		{
			return id - o.id;
		}
		
	}
}

