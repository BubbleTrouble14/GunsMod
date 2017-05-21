package com.bubbletrouble.gunmod.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityGrenade extends Entity implements IProjectile
{
	double bounceFactor;
	boolean stopped;
	int fuse = 0;
	
    public EntityGrenade(World world)
    {
        super(world);
        setSize(0.2F, 0.2F);
        bounceFactor = 0.4;
        stopped = false;
    }

    public EntityGrenade(World world, EntityLivingBase entity)
    {
        this(world, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch, 0.8, 50);
    }
    
    public EntityGrenade(World world, double x, double y, double z, float yaw, float pitch, double force, int fuseLength)
    {
        this(world);
        
    	setRotation(yaw, 0);
    	
        // Set the velocity
    	double xHeading = -MathHelper.sin((yaw * 3.141593F) / 180F);
    	double zHeading = MathHelper.cos((yaw * 3.141593F) / 180F);
        motionX = force*xHeading*MathHelper.cos((pitch / 180F) * 3.141593F);
        motionY = -force*MathHelper.sin((pitch / 180F) * 3.141593F);
        motionZ = force*zHeading*MathHelper.cos((pitch / 180F) * 3.141593F);
    	
        // Set the position
        setPosition(x+xHeading*0.8, y, z+zHeading*0.8);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        
        fuse = fuseLength;
    }

    @Override
	public void onUpdate()
	{
    	
		if (!world.isRemote)
		{
	        if(fuse-- <= 0)
	        {
	            explode();
	        }
		}


		if(stopped)
        {
        	return;
        }
        
    	double prevVelX = motionX;
    	double prevVelY = motionY;
    	double prevVelZ = motionZ;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        addVelocity(motionX, motionY, motionZ);
        

        boolean collided = false;
        // Take into account bouncing (normal displacement just sets them to 0)
        if(motionX!=prevVelX)
        {
        	motionX = -prevVelX;
        	collided = true;
        }
        if(motionZ!=prevVelZ)
        {
        	motionZ = -prevVelZ;
        }
        
        if(motionY!=prevVelY)
        {
        	motionY = -prevVelY;
        	collided = true;
        }
        else
        {
        	motionY -= 0.04;
        }
        
        if(collided)
        {
        	motionX *= bounceFactor;
        	motionY *= bounceFactor;
        	motionZ *= bounceFactor;
        }
        
        // Air friction
        motionX *= 0.99;
        motionY *= 0.99;
        motionZ *= 0.99;
        
        // If we are on the ground and our total movement is below the threshold then stop the grenade
        if(onGround && (motionX*motionX+motionY*motionY+motionZ*motionZ)<0.02)
        {
        	stopped = true;
        	motionX = 0;
        	motionY = 0;
        	motionZ = 0;
        }
    }
    
	private void explode()
	{
		this.world.createExplosion(this, posX, posY, posZ, 4F, true);
		this.setDead();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setByte("fuse", (byte) fuse);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		fuse = nbttagcompound.getByte("fuse");
	}


	@Override
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) 
	{
		
	}

	@Override
	protected void entityInit() 
	{
		
	}
}
