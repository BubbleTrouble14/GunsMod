package com.bubbletrouble.gunmod.client.handlers;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.client.gui.GUIAttachment;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadFinished;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.RightGunReloadFinished;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber
public class ClientEventHandler
{
	private static KeyBinding reload, attachment, playerPooping, arkmode, playerCrafting;

	private static Minecraft mc = Minecraft.getMinecraft();

	private static Random random = new Random();

	private static int swayTicks;
	private static float yawSway;
	private static float pitchSway;
	public static int disabledEquippItemAnimationTime = 0;

	private ItemStack selected;
	private static final ResourceLocation SCOPE_OVERLAY = new ResourceLocation(Main.MODID, "textures/gui/scope.png");
	private static final ResourceLocation SPYGLASS_OVERLAY = new ResourceLocation(Main.MODID, "textures/gui/spyglass.png");
	public boolean showScopeOverlap = false;

	public static void init()
	{
		reload = new KeyBinding("key.arkcraft.reload", Keyboard.KEY_R, Main.MODID);
		ClientRegistry.registerKeyBinding(reload);
		
		attachment = new KeyBinding("key.attachment", Keyboard.KEY_M, Main.MODID);
		ClientRegistry.registerKeyBinding(attachment);

		ClientEventHandler h = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(h);
	}

	public Vec3d getPositionEyes(EntityPlayer player, float partialTick)
	{
		if (partialTick == 1.0F) {

			return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		}
		else {
			double d0 = player.prevPosX + (player.posX - player.prevPosX) * partialTick;
			double d1 = player.prevPosY + (player.posY - player.prevPosY) * partialTick + player.getEyeHeight();
			double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTick;
			return new Vec3d(d0, d1, d2);
		}
	}

	public RayTraceResult rayTrace(EntityPlayer player, double distance, float partialTick)
	{
		Vec3d vec3 = getPositionEyes(player, partialTick);
		Vec3d vec31 = player.getLook(partialTick);
		Vec3d vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
		return player.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
	}
	
	@SubscribeEvent
	public void craftedItem(ItemCraftedEvent evt)	
	{
		System.out.println("Crafted" + evt.crafting);
	}
	
	
	boolean mouseclicked = false;
	
	static boolean leftClick = true;
	static boolean rightClick = true;

	public boolean onItemleftClick(MouseEvent evt) {
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
	
	public boolean onItemRightClick(MouseEvent evt) {
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
	
	public void handleClick(MouseEvent evt , EntityPlayer p)
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
	
	public void leftGun(MouseEvent evt, ItemStack stack, World world, EntityPlayer player)
	{		
		if(evt.getButton() == 0)
		{
			//if(att != nu)
			InventoryAttachment att = InventoryAttachment.create(stack);
			evt.setCanceled(true);
			if (att != null && att.isScopePresent())
			{
				showScopeOverlap = evt.isButtonstate();
			}
			selected = stack;
		}
		if(onItemRightClick(evt))
		{
	//		evt.setCanceled(true);
			ItemRangedWeapon leftGun = (ItemRangedWeapon)stack.getItem();
			leftGun.shootLeftGun(stack, world, player);
			System.out.println("Fire the left gun once too");
		}	
	}
	
	public void rightGun(MouseEvent evt, ItemStack stack, World world, EntityPlayer player)
	{
		if(evt.getButton() == 1)
		{
			InventoryAttachment att = InventoryAttachment.create(stack);
			evt.setCanceled(true);
			//if(att != nu)
			if (att != null && att.isScopePresent())
			{
				showScopeOverlap = evt.isButtonstate();
			}
			selected = stack;
		}
		if(onItemleftClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon rightGun = (ItemRangedWeapon)stack.getItem();
			rightGun.shootRightGun(stack, world, player);
			System.out.println("Fire the right gun once");
		}
	}
	
	public void dualGuns(MouseEvent evt, ItemStack rightStack, ItemStack leftStack, World world, EntityPlayer player)
	{
		if(onItemleftClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon leftGun = (ItemRangedWeapon)leftStack.getItem();
			leftGun.shootLeftGun(leftStack, world, player);
			System.out.println("Fire the left gun once");
		}	
		if(onItemRightClick(evt))
		{
			evt.setCanceled(true);
			ItemRangedWeapon rightGun = (ItemRangedWeapon)rightStack.getItem();
			rightGun.shootRightGun(rightStack, world, player);
			System.out.println("Fire the right gun once");
		}
	}
	
	public static int ticks = 0;
	public static int leftticks = 0;
	
	@SubscribeEvent
	public void onMouseEvent(MouseEvent evt)
	{		
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		handleClick(evt, p);	
	} 
	
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent
//	public void onRenderTick(RenderTickEvent evt)
//	{
//		Minecraft mc = Minecraft.getMinecraft();
//		EntityPlayer p = mc.thePlayer;
//		
//		if(p != null)
//		{
//			ItemStack stackRight = p.getHeldItemMainhand();
//			ItemStack stackLeft = p.getHeldItemOffhand();
//		if (stackLeft != null && stackLeft.getItem() instanceof ItemRangedWeapon)
//		{			
//			ItemRangedWeapon leftgun = (ItemRangedWeapon) stackLeft.getItem();
//			if(leftgun.fired(stackLeft))
//			{
//				System.out.println(leftticks);
//				++leftticks;
//				if(leftticks >= leftgun.recoilDelay() + 15)
//				{
//					leftgun.recoilDown(p, leftgun.getRecoil(), leftgun.getRecoilSneaking(), leftgun.getShouldRecoil());
//					leftticks = 0;
//					leftgun.setFired(stackLeft, p, false);	
//				}	
//			}
//		}
//		if (stackRight != null && stackRight.getItem() instanceof ItemRangedWeapon)
//		{			
//			ItemRangedWeapon w = (ItemRangedWeapon) stackRight.getItem();
//			if(w.fired(stackRight))
//			{
//				System.out.println(ticks);
//				if(++ticks == w.recoilDelay() + 15)
//				{
//					w.recoilDown(p, w.getRecoil(), w.getRecoilSneaking(), w.getShouldRecoil());
//					ticks = 0;
//					w.setFired(stackRight, p, false);
//				}
//			}	
//		}
//		}
//	}
	

	private boolean showSpyglassOverlay;

	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent evt)
	{
		if (mc.gameSettings.thirdPersonView == 0 && (showScopeOverlap || showSpyglassOverlay))
			evt.setNewfov(evt.getNewfov() / 6.0F);
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent evt)
	{
		if (showScopeOverlap || showSpyglassOverlay) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent evt)
	{
		Minecraft mc = Minecraft.getMinecraft();
	//	if ((showScopeOverlap || showSpyglassOverlay) && (mc.player.getActiveItemStack() != selected || !Mouse.isButtonDown(0))) {
	//		showScopeOverlap = false;
	//		showSpyglassOverlay = false;
	//	}
		if (showScopeOverlap || showSpyglassOverlay) {
			// Render scope
			if (evt.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
				if (mc.gameSettings.thirdPersonView == 0) {
					evt.setCanceled(true);
					if (showScopeOverlap)
						showScope();
				}
			}
			// Remove crosshairs
			else if (evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && (showScopeOverlap || showSpyglassOverlay))
				evt.setCanceled(true);
		}
		ItemStack stack = mc.thePlayer.getActiveItemStack();
		if (evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && (stack != null && stack.getItem() instanceof ItemRangedWeapon))
			evt.setCanceled(true);		
	}

	@SubscribeEvent
	public void holding(RenderLivingEvent.Pre event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer thePlayer = mc.thePlayer;
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

	public void showScope()
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer thePlayer = mc.thePlayer;

		// add sway
		swayTicks++;
		if (swayTicks > maxTicks) {
			// change values here for control of the amount of sway!
			int divider = thePlayer.isSneaking() ? 20 : 7;
			swayTicks = 0;
			yawSway = ((random.nextFloat() * 2 - 1) / divider) / maxTicks;
			pitchSway = ((random.nextFloat() * 2 - 1) / divider) / maxTicks;
		}

		EntityPlayer p = mc.thePlayer;
		p.rotationPitch += pitchSway;
		p.rotationYaw += yawSway;
		
		GL11.glPushMatrix();

		RenderScope();
		
		GL11.glPopMatrix();
	}
	
	public void RenderScope()
	{
		ScaledResolution res = new ScaledResolution(mc);
		double width = res.getScaledWidth_double();
		double height = res.getScaledHeight_double();
		
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(SCOPE_OVERLAY);
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

	public static void doReload()
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		ItemStack rightHandStack = player.getHeldItemMainhand();
		ItemStack leftHandStack = player.getHeldItemOffhand();
		
		if(rightHandStack != null && leftHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon && leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();

				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					Main.modChannel.sendToServer(new RightGunReloadStarted());
				//	rightWeapon.setReloading(rightHandStack, player, true);
				}
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
				//	leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
			else if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					Main.modChannel.sendToServer(new RightGunReloadStarted());
				//	rightWeapon.setReloading(rightHandStack, player, true);
				}
			}
			else if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				System.out.println("Reload");
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
				//	leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		else if(rightHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();			
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					Main.modChannel.sendToServer(new RightGunReloadStarted());
				//	rightWeapon.setReloading(rightHandStack, player, true);
			}
		}
		else if(leftHandStack != null)
		{
			if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
				//	leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		}
		
		/*
		else if (rightStack != null && rightStack.getItem() instanceof ItemRangedWeapon) {
			ItemRangedWeapon weapon = (ItemRangedWeapon) rightStack.getItem();
			if (!weapon.isReloading(rightStack) && weapon.canReload(rightStack, player)) {
				Main.modChannel.sendToServer(new ReloadStarted());
				weapon.setReloading(rightStack, player, true);
			}
		}	*/
	}

	@SubscribeEvent
	public void onPlayerKeypressed(InputEvent.KeyInputEvent event)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (reload.isPressed()) {
			doReload();
		}
		else if (attachment.isPressed()) {
			//if (player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemRangedWeapon && !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof NonSupporting)) {
				if(player.getHeldItemMainhand().getItem() != null && player.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
				{
					System.out.println("Open Inv");
					Main.modChannel.sendToServer(new OpenAttachmentInventory());
				}
			//}
		}
	}
}
