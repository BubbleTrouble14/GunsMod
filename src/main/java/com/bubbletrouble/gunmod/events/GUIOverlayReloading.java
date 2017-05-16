package com.bubbletrouble.gunmod.events;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GUIOverlayReloading extends Gui
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public void renderGUIOverlay(RenderGameOverlayEvent.Post e)
	{
		EntityPlayer p = mc.thePlayer;
		ItemStack stack = p.getHeldItemMainhand();
		ItemStack leftStack = p.getHeldItemOffhand();
		if (e.getType().equals(ElementType.HOTBAR))
		{
			if (stack != null && stack.getItem() instanceof ItemRangedWeapon && leftStack != null && leftStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
				boolean rld = weapon.isLoaded(stack, p);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDisable(GL11.GL_LIGHTING);
				int x0 = e.getResolution().getScaledWidth() / 2 - 88 + p.inventory.currentItem * 20;
				int y0 = e.getResolution().getScaledHeight() - 3;
				float f;
				int color;
				if (rld && weapon.getReloadTicks(stack) == 0)//&& PlayerUpdateEvent.reloadTicks == 0)
				{
					f = 1F;
					if (Mouse.isButtonDown(1) && Minecraft.getMinecraft().currentScreen == null) 
					{
						color = 0x60C60000;
					}
					else
					{
						color = 0x60348E00;
					}

				}
				else if (weapon.isReloading(stack))
				{
					f = Math.min((float) weapon.getReloadTicks(stack) /weapon.getReloadDuration(), 1F); // PlayerUpdateEvent.reloadTicks /
					color = 0x60EAA800;
				}
				else
				{
					f = 0F;
					color = 0;
				}
				drawRect(x0, y0, x0 + 16, y0 - (int) (f * 16), color);
				
				ItemRangedWeapon weapon2 = (ItemRangedWeapon) leftStack.getItem();
				boolean rlda = weapon2.isLoaded(leftStack, p);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDisable(GL11.GL_LIGHTING);
				int x01 = e.getResolution().getScaledWidth() / 2 - 10 - 107;
				int y01 = e.getResolution().getScaledHeight() - 3;
				int color1;
				if (rlda && weapon2.getReloadTicks(leftStack) == 0) // && PlayerUpdateEvent.leftReloadTicks == 0)
				{
					f = 1F;
					if (Mouse.isButtonDown(0) && Minecraft.getMinecraft().currentScreen == null) 
					{
						color1 = 0x60C60000;
					}
					else
					{
						color1 = 0x60358E00;
					}

				}
				else if (weapon2.isReloading(leftStack))
				{
					f = Math.min((float) weapon2.getReloadTicks(leftStack) / weapon2.getReloadDuration(), 1F);  //PlayerUpdateEvent.leftReloadTicks /
					color1 = 0x61EAA800;
				}
				else
				{
					f = 0F;
					color1 = 0;
				}
				drawRect(x01, y01, x01 + 16, y01 - (int) (f * 16), color1);
			}
			else if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
				boolean rld = weapon.isLoaded(stack, p);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDisable(GL11.GL_LIGHTING);
				int x0 = e.getResolution().getScaledWidth() / 2 - 88 + p.inventory.currentItem * 20;
				int y0 = e.getResolution().getScaledHeight() - 3;
				float f;
				int color;
				if (rld && weapon.getReloadTicks(stack) == 0)// && PlayerUpdateEvent.reloadTicks == 0)
				{
					f = 1F;
					if (Mouse.isButtonDown(0) && Minecraft.getMinecraft().currentScreen == null) 
					{
						color = 0x60C60000;
					}
					else
					{
						color = 0x60348E00;
					}

				}
				else if (weapon.isReloading(stack))
				{
					f = Math.min((float) weapon.getReloadTicks(stack) / weapon.getReloadDuration(), 1F); // PlayerUpdateEvent.reloadTicks / 
					color = 0x60EAA800;
				}
				else
				{
					f = 0F;
					color = 0;
				}
				drawRect(x0, y0, x0 + 16, y0 - (int) (f * 16), color);
			}
			else if (leftStack != null && leftStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon weapon = (ItemRangedWeapon) leftStack.getItem();
				boolean rld = weapon.isLoaded(leftStack, p);
				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDisable(GL11.GL_LIGHTING);
				int x0 = e.getResolution().getScaledWidth() / 2 - 10 - 107;
				int y0 = e.getResolution().getScaledHeight() - 3;
				float f;
				int color;
				if (rld && weapon.getReloadTicks(leftStack) == 0) //&& PlayerUpdateEvent.leftReloadTicks == 0)
				{
					f = 1F;
					if (Mouse.isButtonDown(1) && Minecraft.getMinecraft().currentScreen == null) 
					{
						color = 0x60C60000;
					}
					else
					{
						color = 0x60358E00;
					}

				}
				else if (weapon.isReloading(leftStack))
				{
					f = Math.min((float) weapon.getReloadDuration(), 1F); //PlayerUpdateEvent.leftReloadTicks / w
					color = 0x60EAA800;
				}
				else
				{
					f = 0F;
					color = 0;
				}
				drawRect(x0, y0, x0 + 16, y0 - (int) (f * 16), color);
			}
		}
		
		//Dispaly bullet count
		else if (e.getType().equals(ElementType.HELMET))
		{
			if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
			{
				String text = "";
				if (!p.capabilities.isCreativeMode)
				{
					ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
					text = weapon.getAmmoQuantity(stack) + "/" + weapon.getAmmoQuantityInInventory(stack, p);
				}
				else
				{
					text = '\u221e' + "";
				}
				int x = e.getResolution().getScaledWidth() - 4 - mc.fontRendererObj.getStringWidth(text);
				int y = 20;
				drawString(mc.fontRendererObj, text, x, y - 16, 0xFFFFFFFF);
			}
			if (leftStack != null && leftStack.getItem() instanceof ItemRangedWeapon)
			{
				String text = "";
				if (!p.capabilities.isCreativeMode)
				{
					ItemRangedWeapon weapon = (ItemRangedWeapon) leftStack.getItem();
					text = weapon.getAmmoQuantity(leftStack) + "/" + weapon.getAmmoQuantityInInventory(leftStack, p);
				}
				else
				{
					text = '\u221e' + "";
				}
				int x = 4;
				int y = 20;
				drawString(mc.fontRendererObj, text, x, y - 16, 0xFFFFFFFF);
			}
		}

	}
}
