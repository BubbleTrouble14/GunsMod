package com.bubbletrouble.gunmod.events;

import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@SideOnly(Side.CLIENT)
public class ClickEvent 
{
	public static boolean mouseclicked = false;
	public static boolean leftClick = true;
	public static boolean rightClick = true;
	public static int ticks = 0;
	public static int leftticks = 0;
	
	public static boolean showScopeOverlap = false;
	private static Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public static void onMouseEvent(MouseEvent evt)
	{		
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		handleClick(evt, p);	
	} 
	
	public static boolean onItemleftClick(MouseEvent evt) {
		if (evt.isButtonstate()) {	
			if (evt.getButton() == 0) 
			{
				evt.setCanceled(true);
				leftClick = true;
				return false;
			}
		} else {
			if (leftClick) {
				if (evt.getButton() == 0) 
				{
					leftClick = false;
					return true;
				}
			}
		}
		return false;
	}	
	
	public static boolean onItemRightClick(MouseEvent evt) {
		if (evt.isButtonstate()) {
			if (evt.getButton() == 1) 
			{
				evt.setCanceled(true);
				rightClick = true;
				return false;
			}
		} else {
			if (rightClick) {
				if (evt.getButton() == 1) 
				{
					rightClick = false;
					return true;
				}
			}
		}
		return false;
	}
	
	public static void handleClick(MouseEvent evt , EntityPlayer p)
	{
		ItemStack rightHandStack = p.getHeldItemMainhand();
		ItemStack leftHandStack = p.getHeldItemOffhand();
		World world = Minecraft.getMinecraft().theWorld;
		
		if(rightHandStack != null && leftHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon && leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				dualGuns(evt, rightHandStack, leftHandStack, world , p);
			}
			else if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				rightGun(evt, rightHandStack, world, p);
			}
			else if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				leftGun(evt, leftHandStack, world, p);
			}
		}
		else if(rightHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				rightGun(evt, rightHandStack, world, p);
			}
		}
		else if(leftHandStack != null)
		{
			if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				leftGun(evt, leftHandStack, world, p);
			}
		}
	}
	
	public static void leftGun(MouseEvent evt, ItemStack stack, World world, EntityPlayer player)
	{		
		if(evt.getButton() == 0)
		{
			InventoryAttachment att = InventoryAttachment.create(stack);
			evt.setCanceled(true);
			if (att != null && att.isScopePresent())
			{
				showScopeOverlap = evt.isButtonstate();
			}
		}
		if(onItemRightClick(evt))
		{
			ItemRangedWeapon leftGun = (ItemRangedWeapon)stack.getItem();
			leftGun.shootLeftGun(stack, world, player);
		}	
	}
	
	public static void rightGun(MouseEvent evt, ItemStack stack, World world, EntityPlayer player)
	{
		if(evt.getButton() == 1)
		{
			World w = Minecraft.getMinecraft().theWorld;			
			BlockPos pos = mc.objectMouseOver.getBlockPos();
			Entity entity = mc.objectMouseOver.entityHit;
			InventoryAttachment att = InventoryAttachment.create(stack);
			
			if (att != null && att.isScopePresent())
			{
				if(mc.objectMouseOver.typeOfHit == Type.ENTITY)
				{
					if(entity instanceof EntityItemFrame)
					{
						evt.setCanceled(false);
						showScopeOverlap = false;
					}
				}
				else if (mc.objectMouseOver.typeOfHit == Type.BLOCK)
				{
					Block block = w.getBlockState(pos).getBlock();
					if(block instanceof BlockContainer)
					{
						evt.setCanceled(false);
						showScopeOverlap = false;
					}
					else
					{
						showScopeOverlap = evt.isButtonstate() && !(Minecraft.getMinecraft().currentScreen instanceof GuiInventory);
						evt.setCanceled(true);
					}
				}
				else 
				{
					evt.setCanceled(true);
					if (!(Minecraft.getMinecraft().currentScreen instanceof GuiInventory))
					{
						showScopeOverlap = evt.isButtonstate() && !(Minecraft.getMinecraft().currentScreen instanceof GuiInventory);
					}
				}
			}
		}
		if(onItemleftClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon rightGun = (ItemRangedWeapon)stack.getItem();
			rightGun.shootRightGun(stack, world, player);
		}
	}
	
	public static void dualGuns(MouseEvent evt, ItemStack rightStack, ItemStack leftStack, World world, EntityPlayer player)
	{
		if(onItemleftClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon leftGun = (ItemRangedWeapon)leftStack.getItem();
			leftGun.shootLeftGun(leftStack, world, player);
		}	
		if(onItemRightClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon rightGun = (ItemRangedWeapon)rightStack.getItem();
			rightGun.shootRightGun(rightStack, world, player);
		}
	}
	
	public static boolean getShowScope()
	{
		return showScopeOverlap;
	}
}
