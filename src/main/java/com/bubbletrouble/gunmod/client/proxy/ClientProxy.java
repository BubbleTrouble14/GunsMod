package com.bubbletrouble.gunmod.client.proxy;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.client.gui.GUIOverlayReloading;
import com.bubbletrouble.gunmod.client.handlers.ClientEventHandler;
import com.bubbletrouble.gunmod.client.render.RenderAdvancedBullet;
import com.bubbletrouble.gunmod.client.render.RenderSimpleBullet;
import com.bubbletrouble.gunmod.client.render.RenderSimpleRifleAmmo;
import com.bubbletrouble.gunmod.client.render.RenderSimpleShotgunAmmo;
import com.bubbletrouble.gunmod.common.entity.EntityAdvancedBullet;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleBullet;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleRifleAmmo;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleShotgunAmmo;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.init.RangedWeapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy
{	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);	
        RangedWeapons.initModels();
	}
	
	@Override
	protected final void registerEventHandlers()
	{
		super.registerEventHandlers();
		ClientEventHandler.init();
		MinecraftForge.EVENT_BUS.register(new GUIOverlayReloading());
	}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		registerItemTexture(item, 0, id);
	}
	
	public void registerItemTexture(final Item item, int meta, String name)
	{
		if (item instanceof ItemRangedWeapon)
			name = "weapons/" + name;
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Main.MODID + ":" + name, "inventory"));	
		ModelLoader.registerItemVariants(item, new ResourceLocation(Main.MODID, name));
	}
	
	@Override
	public EntityPlayer getPlayerFromContext(MessageContext ctx)
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public long getTime()
	{
		return Minecraft.getSystemTime();
	}

	@Override
	public long getWorldTime()
	{
		if (Minecraft.getMinecraft().theWorld != null)
			return Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void registerEntityModels()
	{
			RenderingRegistry.registerEntityRenderingHandler(EntitySimpleBullet.class, new RenderSimpleBullet());
			RenderingRegistry.registerEntityRenderingHandler(EntitySimpleShotgunAmmo.class, new RenderSimpleShotgunAmmo());
			RenderingRegistry.registerEntityRenderingHandler(EntitySimpleRifleAmmo.class, new RenderSimpleRifleAmmo());
			RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedBullet.class, new RenderAdvancedBullet());
	}
}
