package com.bubbletrouble.gunmod.events;

import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@SideOnly(Side.CLIENT)
public class RenderGunHandEvent
{
	public static World w = Minecraft.getMinecraft().world;
	public static EntityPlayer p = Minecraft.getMinecraft().player;

	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent evt)
	{				
		if (p.getHeldItemMainhand() != null && p.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
		{	
		//	ItemRangedWeapon w =  (ItemRangedWeapon) p.getHeldItemMainhand().getItem();
			ItemRenderer r = Minecraft.getMinecraft().getItemRenderer();

		    float f = Minecraft.getMinecraft().getRenderPartialTicks();
		    r.renderItemInFirstPerson((AbstractClientPlayer) p, f, 0, EnumHand.MAIN_HAND, 0, p.getHeldItemMainhand(), 0F);
			evt.setCanceled(true);

		}
		if (p.getHeldItemOffhand() != null && p.getHeldItemOffhand() .getItem() instanceof ItemRangedWeapon)
		{	
		//	ItemRangedWeapon w =  (ItemRangedWeapon) p.getHeldItemOffhand().getItem();
			ItemRenderer r = Minecraft.getMinecraft().getItemRenderer();

		    float f = Minecraft.getMinecraft().getRenderPartialTicks();
		    r.renderItemInFirstPerson((AbstractClientPlayer) p, f, 0, EnumHand.OFF_HAND, 0, p.getHeldItemOffhand() , 0F);
		    evt.setCanceled(true);
		}	
		if (ClickEvent.getShowScope()) {
			evt.setCanceled(true);
		}
	}
}
