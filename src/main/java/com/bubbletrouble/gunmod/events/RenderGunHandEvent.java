package com.bubbletrouble.gunmod.events;

import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
@SideOnly(Side.CLIENT)
public class RenderGunHandEvent
{
	public static World w = Minecraft.getMinecraft().theWorld;
//	public static EntityPlayer p = Minecraft.getMinecraft().thePlayer;
	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent evt)
	{			
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (p.getHeldItemMainhand() != null && p.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
		{	
			ItemRangedWeapon w =  (ItemRangedWeapon) p.getHeldItemMainhand().getItem();
			GunRenderer render = new GunRenderer(Minecraft.getMinecraft());
			if(w.isReloading(p.getHeldItemMainhand()))
			{
				evt.setCanceled(true);
				render.renderItemInFirstPerson((AbstractClientPlayer) p, 0F, 0F, EnumHand.MAIN_HAND, 1F, p.getHeldItemMainhand(), 0F);
				render.updateEquippedItem();
			}
		//	else if(w.isLoaded(p.getHeldItemMainhand(), p))
		//	{
		//		render.resetEquippedProgress(EnumHand.MAIN_HAND);;
		//	}
		}
	}
}
