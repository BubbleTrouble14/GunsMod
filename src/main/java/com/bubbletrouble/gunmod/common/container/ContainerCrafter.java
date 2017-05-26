package com.bubbletrouble.gunmod.common.container;

import com.bubbletrouble.gunmod.common.tileentity.TECrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCrafter extends Container
{

	private TECrafter tileInventory;
	private IInventory inventoryBlueprints;
	// These store cache values, used by the server to only update the client
	// side tile entity when values have changed
	private int[] cachedFields;
	public static final int RECIPE_ITEM_SLOT_YPOS = 60;

	public ContainerCrafter(InventoryPlayer invPlayer, TECrafter tileInventorySmithy)
	{
	//	LogHelper.info("ContainerMP: constructor called.");

		this.tileInventory = tileInventorySmithy;
	//	inventoryBlueprints = tileInventorySmithy.inventoryBlueprints;

		/* MP inventory */
		// Recipe blueprint slot
		this.addSlotToContainer(new SlotBlueprintInventory(inventoryBlueprints,
				TECrafter.BLUEPRINT_SLOT, 33, 16));

		// Input & Output slots
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 9; col++)
			{
				int slotIndex = col + row * 9;
				addSlotToContainer(new SlotRecipeInventory(this.tileInventory, slotIndex,
						8 + col * 18, RECIPE_ITEM_SLOT_YPOS + row * 18));
			}
		}

		/* Hotbar inventory */
		final int HOTBAR_YPOS = 186;
		for (int col = 0; col < 9; col++)
		{
			addSlotToContainer(new Slot(invPlayer, col, 8 + col * 18, HOTBAR_YPOS));
		}

		/* Player inventory */
		final int PLAYER_INVENTORY_YPOS = 128;
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 9; col++)
			{
				int slotIndex = col + row * 9 + 9;
				addSlotToContainer(new Slot(invPlayer, slotIndex, 8 + col * 18,
						PLAYER_INVENTORY_YPOS + row * 18));
			}
		}

		this.tileInventory.setGuiOpen(true, false);
	}
	
	 public void addListener(IContainerListener listener)
	 {
		 super.addListener(listener);
		 listener.sendAllWindowProperties(this, tileInventory);
	 }


	/* Nothing to do, this is a furnace type container */
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int sourceSlotIndex)
	{
		//LogHelper.info("ARKContainerSmithy: transferStackInSlot called.");
		System.out.println("Called");
		Slot sourceSlot = (Slot) inventorySlots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.getHasStack()) { return ItemStack.EMPTY; }
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is a MP container slot
		int nonPlayerSlotsCount = TECrafter.BLUEPRINT_SLOTS_COUNT + TECrafter.INVENTORY_SLOTS_COUNT;
		if (sourceSlotIndex > TECrafter.BLUEPRINT_SLOTS_COUNT - 1 && sourceSlotIndex < nonPlayerSlotsCount)
		{
			// This is a Smithy inventory slot so merge the stack into the
			// players inventory
			if (!mergeItemStack(sourceStack, nonPlayerSlotsCount, 36 + nonPlayerSlotsCount, false)) { return ItemStack.EMPTY; }
		}
		// Check if the slot clicked is one of the vanilla container slots
		else if (sourceSlotIndex >= nonPlayerSlotsCount && sourceSlotIndex < 36 + nonPlayerSlotsCount)
		{
			if (tileInventory.isItemValidForRecipeSlot(sourceStack))
			{
				// This is a vanilla container slot so merge the stack into the
				// Smithy inventory
				if (!mergeItemStack(sourceStack, TECrafter.BLUEPRINT_SLOTS_COUNT,
						nonPlayerSlotsCount, false)) {return ItemStack.EMPTY; }
			}
			else
			{
				return ItemStack.EMPTY;
			}
		}
		else
		{
		//	LogHelper.error("Invalid slotIndex:" + sourceSlotIndex);
			 return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to
		// null
		if (sourceStack.getCount() == 0)
		{
			sourceSlot.putStack(ItemStack.EMPTY);
		}
		else
		{
			sourceSlot.onSlotChanged();
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return tileInventory.isUsableByPlayer(playerIn);
	}

	// This is where you check if any values have changed and if so send an
	// update to any clients accessing this container
	// The container itemstacks are tested in Container.detectAndSendChanges, so
	// we don't need to do that
	// We iterate through all of the TileEntity Fields to find any which have
	// changed, and send them.
	// You don't have to use fields if you don't wish to; just manually match
	// the ID in sendProgressBarUpdate with the value in
	// updateProgressBar()
	// The progress bar values are restricted to shorts. If you have a larger
	// value (eg int), it's not a good idea to try and split it
	// up into two shorts because the progress bar values are sent
	// independently, and unless you add synchronization logic at the
	// receiving side, your int value will be wrong until the second short
	// arrives. Use a custom packet instead.
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		boolean allFieldsHaveChanged = false;
		boolean fieldHasChanged[] = new boolean[tileInventory.getFieldCount()];
		if (cachedFields == null)
		{
			cachedFields = new int[tileInventory.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i)
		{
			if (allFieldsHaveChanged || cachedFields[i] != tileInventory.getField(i))
			{
				cachedFields[i] = tileInventory.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of crafters (players using this container) and
		// update them if necessary
		for (int i = 0; i < this.listeners.size(); ++i)
		{
			IContainerListener icrafting = (IContainerListener) this.listeners.get(i);
			for (int fieldID = 0; fieldID < tileInventory.getFieldCount(); ++fieldID)
			{
				if (fieldHasChanged[fieldID])
				{
					// Note that although sendProgressBarUpdate takes 2 ints on
					// a server these are truncated to shorts
					icrafting.sendProgressBarUpdate(this, fieldID, cachedFields[fieldID]);
				}
			}
		}
	}

	// Called when a progress bar update is received from the server. The two
	// values (id and data) are the same two
	// values given to sendProgressBarUpdate. In this case we are using fields
	// so we just pass them to the tileEntity.
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data)
	{
		// LogHelper.info("ContainerInventorySmithy-updateProgressBar: Called on "
		// + (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ?
		// "client" : "server"));
		// LogHelper.info("ContainerInventorySmithy-updateProgressBar: id = " +
		// id + ", data = " + data);
		tileInventory.setField(id, data);
	}

	// SlotRecipeInventory is a slot for recipe items
	public class SlotRecipeInventory extends Slot
	{
		public SlotRecipeInventory(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if this function returns false, the player won't be able to insert
		// the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return tileInventory.isItemValidForRecipeSlot(stack);
		}
	}

	// SlotBlueprintInventory is a slot for blueprint items
	public class SlotBlueprintInventory extends Slot
	{
		public SlotBlueprintInventory(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if this function returns false, the player won't be able to insert
		// the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn)
		{
			return false;
		}
	}

	public void setBlueprintItemStack(ItemStack stack)
	{
		this.inventoryBlueprints.setInventorySlotContents(0, stack);
	}

	// Used by GUI to see if any players have the GUI open
	public int getNumCrafters()
	{
		return this.listeners.size();
	}
}
