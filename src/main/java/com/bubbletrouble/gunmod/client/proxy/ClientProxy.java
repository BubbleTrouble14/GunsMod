package com.bubbletrouble.gunmod.client.proxy;

import com.bubbletrouble.gunmod.client.render.RenderAdvancedBullet;
import com.bubbletrouble.gunmod.client.render.RenderSimpleBullet;
import com.bubbletrouble.gunmod.client.render.RenderSimpleRifleAmmo;
import com.bubbletrouble.gunmod.client.render.RenderSimpleShotgunAmmo;
import com.bubbletrouble.gunmod.common.entity.EntityAdvancedBullet;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleBullet;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleRifleAmmo;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleShotgunAmmo;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.init.RangedWeapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
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
        registerEntityModels();
	}
	
//	@Override
//	protected final void registerEventHandlers()
//	{
	//	super.registerEventHandlers();
	//	ClientEventHandler.init();
	//	MinecraftForge.EVENT_BUS.register(new GUIOverlayReloading());
//	}
	
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
	
	public void registerEntityModels()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedBullet.class, new IRenderFactory<EntityAdvancedBullet>() {
			@Override
			public Render<EntityAdvancedBullet> createRenderFor(RenderManager manager) {
				return new RenderAdvancedBullet(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySimpleBullet.class, new IRenderFactory<EntitySimpleBullet>(){
			@Override
			public Render<EntitySimpleBullet> createRenderFor(RenderManager manager) {
				return new RenderSimpleBullet(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySimpleRifleAmmo.class, new IRenderFactory<EntitySimpleRifleAmmo>() {
			@Override
			public Render<EntitySimpleRifleAmmo> createRenderFor(RenderManager manager) {
				return new RenderSimpleRifleAmmo(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySimpleShotgunAmmo.class, new IRenderFactory<EntitySimpleShotgunAmmo>() {
			@Override
			public Render<EntitySimpleShotgunAmmo> createRenderFor(RenderManager manager) {
				return new RenderSimpleShotgunAmmo(manager);
			}
		});
	}
}
