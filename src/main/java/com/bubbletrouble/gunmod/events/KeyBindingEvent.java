package com.bubbletrouble.gunmod.events;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.network.LeftGunReloadStarted;
import com.bubbletrouble.gunmod.common.network.OpenAttachmentInventory;
import com.bubbletrouble.gunmod.common.network.RightGunReloadStarted;
import com.bubbletrouble.gunmod.init.RangedWeapons;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingEvent 
{
	private static KeyBinding reload, attachment;

	public static void init()
	{	
		reload = new KeyBinding("key.arkcraft.reload", Keyboard.KEY_R, Main.MODID);
		ClientRegistry.registerKeyBinding(reload);
		
		attachment = new KeyBinding("key.attachment", Keyboard.KEY_M, Main.MODID);
		ClientRegistry.registerKeyBinding(attachment);
		
		KeyBindingEvent h = new KeyBindingEvent();
		MinecraftForge.EVENT_BUS.register(h);
	}

	@SubscribeEvent
	public static void onPlayerKeypressed(InputEvent.KeyInputEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (reload.isPressed()) {
			doReload();
		}
		else if (attachment.isPressed()) {
			if(player.getHeldItemMainhand().getItem() != null && player.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
			{
				Main.modChannel.sendToServer(new OpenAttachmentInventory());
			}
		}
	}
	
	@SubscribeEvent
	public static void clientTick(ClientTickEvent  evt)
	{
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		ItemStack stackRight = p.getHeldItemMainhand();
		ItemStack stackLeft = p.getHeldItemOffhand();
					
		InventoryAttachment inv = InventoryAttachment.create(stackRight);
		if (inv != null && inv.isFlashPresent())
		{
			updateFlashlight(p);
		}
		else if (inv != null && inv.isLaserPresent())
		{
			updateLaser(p);
		} 
		InventoryAttachment invleft = InventoryAttachment.create(stackLeft);
		if (invleft != null && invleft.isFlashPresent())
		{
			updateFlashlight(p);
		}
		else if (invleft != null && invleft.isLaserPresent())
		{
			updateLaser(p);
		} 
	}
	
	
	public static void doReload()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		ItemStack rightHandStack = player.getHeldItemMainhand();
		ItemStack leftHandStack = player.getHeldItemOffhand();
		
		if(rightHandStack != null && leftHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon && leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();

				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player) && !leftWeapon.isReloading(leftHandStack))//leftWeapon.getAmmoQuantity(leftHandStack) != 0) 
				{
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
				else if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player) && !rightWeapon.isReloading(rightHandStack)) 
				{
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
			else if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) {
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
				}
			}
			else if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		else if(leftHandStack != null)
		{
			if(leftHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon leftWeapon = (ItemRangedWeapon) leftHandStack.getItem();
				if (!leftWeapon.isReloading(leftHandStack) && leftWeapon.canReload(leftHandStack, player)) {
					leftWeapon.soundCharge(leftHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new LeftGunReloadStarted());
					leftWeapon.setReloading(leftHandStack, player, true);
				}
			}
		}
		else if(rightHandStack != null)
		{
			if(rightHandStack.getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rightWeapon = (ItemRangedWeapon) rightHandStack.getItem();	
				if (!rightWeapon.isReloading(rightHandStack) && rightWeapon.canReload(rightHandStack, player)) 
				{
					rightWeapon.soundCharge(rightHandStack, player.worldObj, player);
					Main.modChannel.sendToServer(new RightGunReloadStarted());
					rightWeapon.setReloading(rightHandStack, player, true);
			}
		}
		}
	}
	
	public static void updateFlashlight(Entity entityIn)
	{
		RayTraceResult mop = rayTrace(entityIn, 20, 1.0F);
		if (mop != null && mop.typeOfHit != RayTraceResult.Type.MISS) {
			World world = entityIn.worldObj;
			BlockPos pos;

			if (mop.typeOfHit == RayTraceResult.Type.ENTITY) {
				pos = mop.entityHit.getPosition();
			}
			else {
				pos = mop.getBlockPos();
				pos = pos.offset(mop.sideHit);
			}

			if (world.isAirBlock(pos)) {
				world.setBlockState(pos, RangedWeapons.blockLight.getDefaultState());
				world.scheduleUpdate(pos, RangedWeapons.blockLight, 2);
			}
		}
	}	
			
	public static void updateLaser(EntityPlayer p)
	{
		World w = p.worldObj;
		RayTraceResult mop = getMouseOver(0);// Minecraft.getMinecraft().objectMouseOver;//rayTrace(p, 35, 1.0F);
		
		if (mop == null) return;
		if (mop.typeOfHit == RayTraceResult.Type.BLOCK || mop.typeOfHit == RayTraceResult.Type.ENTITY) {
			double x = mop.hitVec.xCoord;
			double y = mop.hitVec.yCoord;
			double z = mop.hitVec.zCoord;
			w.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0, 0, 0, 0);
		}
	}		
	
	public static RayTraceResult rayTrace(Entity player, double distance, float partialTick)
	{
		return player.rayTrace(10, partialTick);
	}
	
	public static Vec3d getPositionEyes(Entity player, float partialTick)
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
		
	public static RayTraceResult getMouseOver(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		RayTraceResult raytraceresult = null;

		if (entity != null) {
			if (mc.theWorld != null) {
				mc.mcProfiler.startSection("pick");
				mc.pointedEntity = null;
				double d0 = 10;
				raytraceresult = entity.rayTrace(d0, partialTicks);
				Vec3d vec3d = entity.getPositionEyes(partialTicks);
				double d1 = d0;

				if (raytraceresult != null) {
					d1 = raytraceresult.hitVec.distanceTo(vec3d);
				}

				Vec3d vec3d1 = entity.getLook(partialTicks);
				Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
				Entity pointedEntity = null;
				Vec3d vec3d3 = null;
				List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox()
						.addCoord(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0).expand(1.0D, 1.0D, 1.0D),
						Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
							public boolean apply(@Nullable Entity p_apply_1_) {
								return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
							}
						}));
				double d2 = d1;

				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = (Entity) list.get(j);
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox()
							.expandXyz((double) entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult2 = axisalignedbb.calculateIntercept(vec3d, vec3d2);

					if (axisalignedbb.isVecInside(vec3d)) {
						if (d2 >= 0.0D) {
							pointedEntity = entity1;
							vec3d3 = raytraceresult2 == null ? vec3d : raytraceresult2.hitVec;
							d2 = 0.0D;
						}
					} else if (raytraceresult2 != null) {
						double d3 = vec3d.distanceTo(raytraceresult2.hitVec);

						if (d3 < d2 || d2 == 0.0D) {
							if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity()
									&& !entity.canRiderInteract()) {
								if (d2 == 0.0D) {
									pointedEntity = entity1;
									vec3d3 = raytraceresult2.hitVec;
								}
							} else {
								pointedEntity = entity1;
								vec3d3 = raytraceresult2.hitVec;
								d2 = d3;
							}
						}
					}
				}

				if (pointedEntity != null && (d2 < d1 || raytraceresult == null)) {
					raytraceresult = new RayTraceResult(pointedEntity, vec3d3);

					if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
						mc.pointedEntity = pointedEntity;
					}
				}
			}
		}
		return raytraceresult;
	}
	
}


