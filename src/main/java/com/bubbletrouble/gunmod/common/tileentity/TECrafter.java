package com.bubbletrouble.gunmod.common.tileentity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.crafting.CrafterCraftingManager;
import com.bubbletrouble.gunmod.common.crafting.IRecipes;
import com.bubbletrouble.gunmod.common.inventory.InventoryCrafter;
import com.bubbletrouble.gunmod.common.network.UpdateCrafterToCraftItem;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author wildbill22
 */
public class TECrafter extends TileEntity implements IInventory, ITickable
{
	// class variables
	int tick = 20;

	// Constants for the inventory
	public static final int INVENTORY_SLOTS_COUNT = 27;
	public static final int TOTAL_SLOTS_COUNT = INVENTORY_SLOTS_COUNT;
	public static final int FIRST_INVENTORY_SLOT = 0;
	public static final int LAST_INVENTORY_SLOT = INVENTORY_SLOTS_COUNT - 1;
	public static final int BLUEPRINT_SLOTS_COUNT = 1;
	public static final int BLUEPRINT_SLOT = 0;

	// Variables to sync from client to server
	private boolean craftAll = false;
	private boolean craftOne = false;
	/**
	 * The currently displayed blueprint
	 */
	private short blueprintSelected = 0;
	private boolean guiOpen = false;

	private void sendUpdateToServer()
	{
		Main.modChannel.sendToServer(new UpdateCrafterToCraftItem(blueprintSelected, craftOne,
				craftAll, guiOpen, this.pos));
	}

	public void setGuiOpen(boolean guiOpen, boolean andUpdateServer)
	{
		this.guiOpen = guiOpen;
		if (andUpdateServer)
		{
			sendUpdateToServer();
		}
	}

	/**
	 * Set true to craft items
	 */
	@SideOnly(Side.CLIENT)
	public void setCraftAllPressed(boolean craftAllPressed, boolean andUpdateServer)
	{
		this.craftAll = craftAllPressed;
		if (andUpdateServer)
		{
			sendUpdateToServer();
		}
	}

	/**
	 * Set true to craft items
	 */
	@SideOnly(Side.CLIENT)
	public void setCraftOnePressed(boolean craftOnePressed, boolean andUpdateServer)
	{
		this.craftOne = craftOnePressed;
		if (andUpdateServer)
		{
			sendUpdateToServer();
		}
	}

	public boolean isCrafting()
	{
		return craftOne || craftAll;
	}

	public boolean isCraftingOne()
	{
		return craftOne;
	}

	public boolean isCraftingAll()
	{
		return craftAll;
	}

	/**
	 * itemStacks variable that will store the blueprints
	 */
	private ItemStack[] blueprintStacks;

	/**
	 * The number of blueprints (number of recipes defined)
	 */
	private int numBlueprints;

	/**
	 * Get blueprint selected (to be displayed)
	 */
	public int getNumBlueprints()
	{
		return numBlueprints;
	}

	/**
	 * The blueprint in the blueprint slots that is selected
	 */
	// private short blueprintSlotSelected = 0; // Currently just one slot

	// public InventoryBlueprints inventoryBlueprints = new
	// InventoryBlueprints("Blueprints", false,
	// BLUEPRINT_SLOTS_COUNT, SmithyCraftingManager.getInstance());
	public InventoryCrafter inventoryBlueprints = new InventoryCrafter("Blueprints", false,
			BLUEPRINT_SLOTS_COUNT);

	/**
	 * Get blueprint selected (to be displayed)
	 */
	public int getBlueprintSelected()
	{
		return blueprintSelected;
	}

	public void setBlueprintSelected(int blueprintSelected)
	{
		this.blueprintSelected = (short) blueprintSelected;
		this.inventoryBlueprints.setInventorySlotContents(0, blueprintStacks[blueprintSelected]);
	}

	/**
	 * Select next blueprint
	 */
	@SideOnly(Side.CLIENT)
	public void selectNextBlueprint()
	{
		blueprintSelected++;
		if (blueprintSelected >= numBlueprints)
		{
			blueprintSelected = (short) (numBlueprints - 1);
		}
		this.inventoryBlueprints.setInventorySlotContents(0, blueprintStacks[blueprintSelected]);
		sendUpdateToServer();
	}

	/**
	 * Select next blueprint
	 */
	@SideOnly(Side.CLIENT)
	public void selectPrevBlueprint()
	{
		blueprintSelected--;
		if (blueprintSelected <= 0)
		{
			blueprintSelected = 0;
		}
		this.inventoryBlueprints.setInventorySlotContents(0, blueprintStacks[blueprintSelected]);
		sendUpdateToServer();
	}

	// Create and initialize the itemStacks variable that will store the
	// itemStacks
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOTS_COUNT];

	@SuppressWarnings("rawtypes")
	public TECrafter()
	{
		// FIXME - Is there another way to do this? See note in RecipeHandler
		numBlueprints = CrafterCraftingManager.getInstance().getNumRecipes();
		// numBlueprints = RecipeHandler.numSmithyCraftingRecipes;
		blueprintStacks = new ItemStack[numBlueprints];
		List recipes = CrafterCraftingManager.getInstance().getRecipeList();
		Iterator iterator = recipes.iterator();
		IRecipes irecipe;
		int i = 0;
		while (iterator.hasNext())
		{
			irecipe = (IRecipes) iterator.next();
			blueprintStacks[i] = irecipe.getRecipeOutput();
			i++;
		}
		setBlueprintSelected(0);
	}

	/**
	 * The number of items that can be crafted
	 */
	private short numThatCanBeCrafted = 0;

	@SideOnly(Side.CLIENT)
	public int getNumToBeCrafted()
	{
		if (tick == 20)
		{
			canCraft();
		}
		return numThatCanBeCrafted;
	}

	/**
	 * The number of seconds required to craft an item
	 */
	private static final short CRAFT_TIME_FOR_ITEM = (short)10;

	/**
	 * The number of seconds the current item has been crafting Logic: -1 when
	 * none are being crafted 0 when the items is to be crafted n seconds until
	 * it will be crafted
	 */
	private short craftingTime = -1;

	/**
	 * Time to craft current item being crafted
	 */
	public int craftingTimeRemainingOnItem()
	{
		return (int) craftingTime;
	}

	// Returns double between 0 and 1 representing % done
	public double fractionCraftingRemainingForItem()
	{
		if (craftingTime < 0) { return 0.0D; }
		double fraction = craftingTime / (double) CRAFT_TIME_FOR_ITEM;
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	// This method is called every tick to update the tile entity, i.e.
	// It runs both on the server and the client.
	@Override
	public void update()
	{
		if (tick >= 0)
		{
			tick--;
			return;
		}
		else
		{
			tick = 20;
		}

		// If not crafting an item, return
		if (!craftAll && !craftOne) { return; }
		// Reset crafting time if it reaches -1 (is true after crafting one of
		// multiple, or after pushing button in GUI)
		if (craftingTime < 0)
		{
			craftingTime = CRAFT_TIME_FOR_ITEM;
			if (this.guiOpen)
			{
				// See if an item can be crafted
				if (!craftItem(false))
				{
					craftAll = false;
					craftOne = false;
					craftingTime = -1;
				}
			}
		}
		else
		{
			craftingTime--;
		}

		// If craftingTime has reached -1, try and craft the item
		if (craftingTime < 0)
		{
		//	LogHelper.info("TileInventorySmith: About to craft the item on " + (FMLCommonHandler
		//			.instance().getEffectiveSide() == Side.CLIENT ? "client" : "server"));
			if (!craftItem())
			{
				craftAll = false;
			}
			if (craftOne)
			{
				craftOne = false;
			}
		}
	}

	/**
	 * Check if the item is craftable and there is sufficient space in the
	 * output slots
	 *
	 * @return true if crafting the item is possible
	 */
	private boolean canCraft()
	{
		return craftItem(false);
	}

	/**
	 * Craft an item, if possible
	 */
	private boolean craftItem()
	{
		return craftItem(true);
	}

	/**
	 * checks that there are enough items to craft the item and that there is
	 * room for the result in the output slots If desired, crafts the item
	 *
	 * @param doCraftItem
	 *            - If true, craft the item. If false, check whether crafting is
	 *            possible, but don't change the inventory
	 * @return false if no item can be crafted, true otherwise
	 */
	private boolean craftItem(boolean doCraftItem)
	{
		Integer firstSuitableOutputSlot = null;
		ItemStack result = blueprintStacks[blueprintSelected];

		// No recipes?
		if (result == null) { return false; }

		// LogHelper.info("TileInventorySmithy: Update called on " +
		// (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ?
		// "client" : "server"));
		// LogHelper.info("TileInventorySmithy: craftAll = " + (craftAll == true
		// ? "true" : "false"));

		// find the first suitable output slot, 1st check for identical item
		// that has enough space
		for (int outputSlot = LAST_INVENTORY_SLOT; outputSlot > FIRST_INVENTORY_SLOT; outputSlot--)
		{
			ItemStack outputStack = itemStacks[outputSlot];
			if (!outputStack.isEmpty() && outputStack.getItem() == result.getItem() && (!outputStack
					.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata()) && ItemStack
					.areItemStackTagsEqual(outputStack, result))
			{
				int combinedSize = itemStacks[outputSlot].getCount() + result.getCount();
				if (combinedSize <= getInventoryStackLimit() && combinedSize <= itemStacks[outputSlot]
						.getMaxStackSize())
				{
					firstSuitableOutputSlot = outputSlot;
					break;
				}
			}
		}
		if (firstSuitableOutputSlot == null)
		{
			// 2nd look for for empty slot if no partially filled slots are
			// found
			for (int outputSlot = LAST_INVENTORY_SLOT; outputSlot > FIRST_INVENTORY_SLOT; outputSlot--)
			{
				ItemStack outputStack = itemStacks[outputSlot];
				if (outputStack.isEmpty())
				{
					firstSuitableOutputSlot = outputSlot;
					break;
				}
			}
		}
		if (firstSuitableOutputSlot == null)
		{
		//	LogHelper.info("TileInventorySmithy: No output slots available.");
			return false;
		}

		// finds if there is enough inventory to craft the result
		if (!doCraftItem)
		{
			numThatCanBeCrafted = (short) CrafterCraftingManager.getInstance().hasMatchingRecipe(
					result, itemStacks, false);
			if (numThatCanBeCrafted <= 0)
			{
				if (this.guiOpen)
				{
			//		LogHelper.info("TileInventorySmithy: Can't craft item from inventory.");
				}
				return false;
			}
			return true;
		}

		// Craft an item
		int numCrafted = (short) CrafterCraftingManager.getInstance().hasMatchingRecipe(result,
				itemStacks, true);

		if (numCrafted <= 0) { return false; }

		// alter output slot
	//	LogHelper.info("TileInventorySmithy: Update called on " + (FMLCommonHandler.instance()
	//			.getEffectiveSide() == Side.CLIENT ? "client" : "server"));
	//	LogHelper
	//			.info("TileInventorySmithy: Copy craft result to slot: " + firstSuitableOutputSlot);
		if (itemStacks[firstSuitableOutputSlot] == null)
		{
			itemStacks[firstSuitableOutputSlot] = result.copy(); // Use deep
																	// .copy()
																	// to avoid
																	// altering
																	// the
																	// recipe
		}
		else
		{
			itemStacks[firstSuitableOutputSlot].grow(result.getCount()); //+= result.stackSize;
		}
		markDirty();
		return true;
	}

	@Override
	public String getName()
	{
		return "container.smithy.name";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getSizeInventory()
	{
		return itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int count)
	{
		ItemStack itemStackInSlot = getStackInSlot(slotIndex);
		if (itemStackInSlot.isEmpty()) { return ItemStack.EMPTY; }

		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count)
		{
			itemStackRemoved = itemStackInSlot;
			setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		}
		else
		{
			itemStackRemoved = itemStackInSlot.splitStack(count);
			if (itemStackInSlot.getCount() == 0)
			{
				setInventorySlotContents(slotIndex, ItemStack.EMPTY);
			}
		}
		markDirty();
		return itemStackRemoved;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack)
	{
		itemStacks[slotIndex] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit())
		{
			itemstack.setCount(getInventoryStackLimit());// = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	@Override
	public void clear()
	{
		Arrays.fill(itemStacks, ItemStack.EMPTY);
	}

	// Return true if stack is a valid fertilizer for the crop plot
	public boolean isItemValidForRecipeSlot(ItemStack stack)
	{
		return CrafterCraftingManager.getInstance().isItemInRecipe(stack);
	}

	// ------------------------------

	// This is where you save any data that you don't want to lose when the tile
	// entity unloads
	// In this case, it saves the state of the Smithy and the itemstacks stored
	// in the inventory
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound)
	{
		super.writeToNBT(parentNBTTagCompound); // The super call is required to
												// save and load the tiles
												// location

		// Save the stored item stacks

		// to use an analogy with Java, this code generates an array of hashmaps
		// The itemStack in each slot is converted to an NBTTagCompound, which
		// is effectively a hashmap of key->value pairs such
		// as slot=1, id=2353, count=1, etc
		// Each of these NBTTagCompound are then inserted into NBTTagList, which
		// is similar to an array.
		NBTTagList dataForAllSlots = new NBTTagList();
		for (int i = 0; i < this.itemStacks.length; ++i)
		{
			if (!this.itemStacks[i].isEmpty())
			{
				NBTTagCompound dataForThisSlot = new NBTTagCompound();
				dataForThisSlot.setByte("Slot", (byte) i);
				this.itemStacks[i].writeToNBT(dataForThisSlot);
				dataForAllSlots.appendTag(dataForThisSlot);
			}
		}
		// the array of hashmaps is then inserted into the parent hashmap for
		// the container
		parentNBTTagCompound.setTag("Items", dataForAllSlots);

		// Save everything else
		parentNBTTagCompound.setShort("blueprintSelected", blueprintSelected);
		//LogHelper.info("TileInventorySmithy: Wrote inventory.");
		return parentNBTTagCompound;

	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		super.readFromNBT(nbtTagCompound); // The super call is required to save
											// and load the tiles location
		final byte NBT_TYPE_COMPOUND = 10; // See NBTBase.createNewByType() for
											// a listing
		NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

		Arrays.fill(itemStacks, ItemStack.EMPTY); // set all slots to empty
		for (int i = 0; i < dataForAllSlots.tagCount(); ++i)
		{
			NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
			byte slotNumber = dataForOneSlot.getByte("Slot");
			if (slotNumber >= 0 && slotNumber < this.itemStacks.length)
			{
				this.itemStacks[slotNumber] = new ItemStack(dataForOneSlot);
			}
		}

		// Load everything else. Trim the arrays (or pad with 0) to make sure
		// they have the correct number of elements
		blueprintSelected = nbtTagCompound.getShort("blueprintSelected");
	//	LogHelper.info("TileInventorySmithy: Read inventory.");
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}
	  
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		final int METADATA = 0;			
		return new SPacketUpdateTileEntity(this.pos, METADATA, nbtTagCompound);
	}

	// When the world loads from disk, the server needs to send the TileEntity
	// information to the client
	// it uses getDescriptionPacket() and onDataPacket() to do this
	
	//TODO Packets
//	@Override
//	public Packet getDescriptionPacket()
//	{
//		NBTTagCompound nbtTagCompound = new NBTTagCompound();
//		writeToNBT(nbtTagCompound);
//		final int METADATA = 0;
//		return new S35PacketUpdateTileEntity(this.pos, METADATA, nbtTagCompound);
//	}

//	@Override
//	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
//	{
//		readFromNBT(pkt.getNbtCompound());
//	}

	// Fields are used to send non-inventory information from the server to
	// interested clients
	// The container code caches the fields and sends the client any fields
	// which have changed.
	// The field ID is limited to byte, and the field value is limited to short.
	// (if you use more than this, they get cast down
	// in the network packets)
	// If you need more than this, or shorts are too small, use a custom packet
	// in your container instead.

	// private static final byte BLUEPRINT_SEL_FIELD_ID = 0;
	private static final byte NUMBER_OF_FIELDS = 0;

	@Override
	public int getField(int id)
	{
		// if (id == BLUEPRINT_SEL_FIELD_ID) return blueprintSelected;
		System.err.println("Invalid field ID in TileInventorySmithy.getField:" + id);
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		// if (id == BLUEPRINT_SEL_FIELD_ID) {
		// blueprintSelected = (short)value;
		// }
		// else {
		System.err.println("Invalid field ID in TileInventorySmithy.setField:" + id);
		// }
	}

	@Override
	public int getFieldCount()
	{
		return NUMBER_OF_FIELDS;
	}

	/**
	 * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity
	 * should be re-created when the ID, or Metadata changes. Use with caution
	 * as this will leave straggler TileEntities, or create conflicts with other
	 * TileEntities if not used properly.
	 *
	 * @param world
	 *            Current world
	 * @param pos
	 *            Tile's world position
	 * @param oldID
	 *            The old ID of the block
	 * @param newID
	 *            The new ID of the block (May be the same)
	 * @return True to remove the old tile entity, false to keep it in tact {and
	 *         create a new one if the new values specify to}
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(
		this.getName());
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {	
		if (!itemStacks[index].isEmpty())
		{
			ItemStack itemstack = itemStacks[index];
			itemStacks[index] = ItemStack.EMPTY;
			return itemstack;
		}
		return ItemStack.EMPTY;	
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) 
	{
		if (this.world.getTileEntity(this.pos) != this) { return false; }
		final double X_CENTRE_OFFSET = 0.5;
		final double Y_CENTRE_OFFSET = 0.5;
		final double Z_CENTRE_OFFSET = 0.5;
		final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
		return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET,
				pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
	}
}
