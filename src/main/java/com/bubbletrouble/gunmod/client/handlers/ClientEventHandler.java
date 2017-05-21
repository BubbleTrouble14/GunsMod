package com.bubbletrouble.gunmod.client.handlers;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;
import com.bubbletrouble.gunmod.init.RangedWeapons;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler
{
	private static KeyBinding reload, attachment;

	private static Minecraft mc = Minecraft.getMinecraft();

	private static Random random = new Random();

	private static int swayTicks;
	private static float yawSway;
	private static float pitchSway;
	public static int disabledEquippItemAnimationTime = 0;

	private static final ResourceLocation SCOPE_OVERLAY = new ResourceLocation(Main.MODID, "textures/gui/scope.png");
	public static boolean showScopeOverlap = false;
	
	public static void init()
	{
		reload = new KeyBinding("key.arkcraft.reload", Keyboard.KEY_R, Main.MODID);
		ClientRegistry.registerKeyBinding(reload);
		
		attachment = new KeyBinding("key.attachment", Keyboard.KEY_M, Main.MODID);
		ClientRegistry.registerKeyBinding(attachment);

		ClientEventHandler h = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(h);
	}

	boolean mouseclicked = false;
	
	static boolean leftClick = true;
	static boolean rightClick = true;

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
		World world = Minecraft.getMinecraft().world;
		
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
			World w = Minecraft.getMinecraft().world;			
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
	
	public static int ticks = 0;
	public static int leftticks = 0;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onMouseEvent(MouseEvent evt)
	{		
		EntityPlayer p = Minecraft.getMinecraft().player;
		handleClick(evt, p);	
	} 

	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent evt)
	{
		if (mc.gameSettings.thirdPersonView == 0 && (showScopeOverlap && mc.currentScreen == null))
			evt.setNewfov(evt.getNewfov() / 6.0F);
	}

	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent evt)
	{
		if (showScopeOverlap) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent evt)
	{
		Minecraft mc = Minecraft.getMinecraft();
//		if ((showScopeOverlap) && (mc.thePlayer.getActiveItemStack() != )) 
//		{
//			showScopeOverlap = false;
//		}
		if (showScopeOverlap) {
			// Render scope
			if (evt.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
				if (mc.gameSettings.thirdPersonView == 0) {
					evt.setCanceled(true);
					if (showScopeOverlap && mc.currentScreen == null)
						showScope();
				}
			}
			// Remove crosshairs
			else if (evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && (showScopeOverlap))
				evt.setCanceled(true);
		}
		ItemStack stack = mc.player.getActiveItemStack();
		if (evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && (stack != null && stack.getItem() instanceof ItemRangedWeapon))
			evt.setCanceled(true);		
	}

	@SubscribeEvent
	public void holding(RenderLivingEvent.Pre<EntityLivingBase> event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer thePlayer = mc.player;
		ItemStack stack = thePlayer.getActiveItemStack();
		if (!event.isCanceled() && event.getEntity().equals(thePlayer) && stack != null) {
			if (stack.getItem() instanceof ItemRangedWeapon) {
				ModelPlayer model = (ModelPlayer) event.getRenderer().getMainModel();
				// TODO adapt for left/right handed
				model.rightArmPose = ArmPose.BOW_AND_ARROW;
			}
		}
	}

	private static final int maxTicks = 70;

	public static void showScope()
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer thePlayer = mc.player;

		// add sway
		swayTicks++;
		if (swayTicks > maxTicks) {
			// change values here for control of the amount of sway!
			int divider = thePlayer.isSneaking() ? 20 : 7;
			swayTicks = 0;
			yawSway = ((random.nextFloat() * 2 - 1) / divider) / maxTicks;
			pitchSway = ((random.nextFloat() * 2 - 1) / divider) / maxTicks;
		}

		EntityPlayer p = mc.player;
		p.rotationPitch += pitchSway;
		p.rotationYaw += yawSway;
		
		GL11.glPushMatrix();

		RenderScope();
		
		GL11.glPopMatrix();
	}
	
	public static void RenderScope()
	{
		ScaledResolution res = new ScaledResolution(mc);
		double width = res.getScaledWidth_double();
		double height = res.getScaledHeight_double();
		
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        mc.getTextureManager().bindTexture(SCOPE_OVERLAY);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(0.0D, (double)height, -90.0D).tex(0.0D, 1.0D).endVertex();
        vertexbuffer.pos((double)width, (double)height, -90.0D).tex(1.0D, 1.0D).endVertex();
        vertexbuffer.pos((double)width, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        vertexbuffer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}	
}
