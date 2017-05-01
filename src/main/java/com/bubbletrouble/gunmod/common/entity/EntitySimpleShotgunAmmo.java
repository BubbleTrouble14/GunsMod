package com.bubbletrouble.gunmod.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySimpleShotgunAmmo extends EntityProjectile
{
	public EntitySimpleShotgunAmmo(World world)
	{
		super(world);
	}

	public EntitySimpleShotgunAmmo(World world, double x, double y, double z)
	{
		this(world);
		setPosition(x, y, z);
	}

	public EntitySimpleShotgunAmmo(World worldIn, EntityLivingBase shooter, float speed, float inaccuracy, double damage, int range)
	{
		super(worldIn, shooter, speed, inaccuracy, damage, range);
	}

	@Override
	public float getGravity()
	{
		return 0.005F;
	}

	@Override
	public float getAirResistance()
	{
		return 0.98F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public void setKnockbackStrength(int knockBack)
	{
		knockBack = 10;
	}

	@Override
	public void onGroundHit(RayTraceResult movingobjectposition)
	{
		worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		breakGlass(movingobjectposition);
		this.setDead();
	}

	public static void fireFromDispenser(World world, double d, double d1, double d2, int i, int j, int k)
	{
		for (int i1 = 0; i1 < 2; i1++)
		{
			EntitySimpleShotgunAmmo entityShotgunShot = new EntitySimpleShotgunAmmo(world, d, d1, d2);

			entityShotgunShot.setThrowableHeading(i, j, k, 3F, 10.0F);
			world.spawnEntityInWorld(entityShotgunShot);
		}
	}

	@Override
	protected ItemStack getArrowStack() {
		// TODO Auto-generated method stub
		return null;
	}
}