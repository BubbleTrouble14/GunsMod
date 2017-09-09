package com.bubbletrouble.gunmod.common.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.common.entity.EntityProjectile;
import com.bubbletrouble.gunmod.common.entity.ProjectileType;
import com.bubbletrouble.gunmod.common.inventory.InventoryAttachment;
import com.bubbletrouble.gunmod.common.item.attachments.NonSupporting;
import com.bubbletrouble.gunmod.common.network.LeftGunShoot;
import com.bubbletrouble.gunmod.common.network.RecoilLeftGun;
import com.bubbletrouble.gunmod.common.network.RecoilRightGun;
import com.bubbletrouble.gunmod.common.network.RightGunShoot;
import com.bubbletrouble.gunmod.common.testing.InventoryCapability;
import com.bubbletrouble.gunmod.events.KeyBindingEvent;
import com.bubbletrouble.gunmod.utils.I18n;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Lewis_McReu
 * @author BubbleTrouble
 */
public abstract class ItemRangedWeapon extends ItemBow implements IUpdateAttachments
{
	protected static final int MAX_DELAY = 72000;

	static boolean notClickedYet = true;

	private Set<ItemProjectile> projectiles;
	private final int maxAmmo;
	private final int ammoConsumption;
	private final String defaultAmmoType;
	private final long shotInterval;
	private final float speed;
	private final float inaccuracy;
	public long nextShotMillis = 0;
	private double damage;
	private int range;
	private final float recoilSneaking;
	private final float recoil;
	private final boolean shouldRecoil;
	private final boolean twoHanded;

	/**
	 * name, durability, maxAmmo, defaultAmmoType, ammoConsumption, shotInterval, speed, inaccuracy, damage, range, recoil, recoilSneaking, shouldRecoil, twoHanded
	 */
	public ItemRangedWeapon(String name, int durability, int maxAmmo, String defaultAmmoType, int ammoConsumption,
			double shotInterval, float speed, float inaccuracy, double damage, int range, float recoil,
			float recoilSneaking, boolean shouldRecoil, boolean twoHanded) {
		super();
		this.speed = speed;
		this.inaccuracy = inaccuracy;
        this.addPropertyOverride(new ResourceLocation("reloading"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
            	if(stack.getItem() instanceof ItemRangedWeapon)
            	{
	              	InventoryAttachment att = InventoryAttachment.create(stack);
	        		if (att.getStackInSlot(0) != ItemStack.EMPTY) 
	        		{
	        			if (att.isScopePresent()) {
	        				return 1.0f;
	        			} else if (att.isFlashPresent()) {
	        				return 2.0f;
	        			} else if (att.isLaserPresent()) {
	        				return 3.0f;
	        			} else if (att.isSilencerPresent()) {
	        				return 4.0f;
	        			}
	        		}
            	}
            	return 0.0f;
            }
        });
        this.addPropertyOverride(new ResourceLocation("reloading"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
        		if (isReloading(stack))
        		{
        			return 1.0f;
        		}
        		return 0.0f;
            }
        });
		//Milliseconds
		this.shotInterval = (long) (shotInterval * 1000);
		this.ammoConsumption = ammoConsumption;
		this.defaultAmmoType = defaultAmmoType;
		this.maxAmmo = maxAmmo;
		this.setMaxDamage(durability);
		this.setMaxStackSize(1);
		this.projectiles = new HashSet<>();
		this.setCreativeTab(Main.tabGuns);
		setRegistryName(name);
		this.setUnlocalizedName(name);
		this.damage = damage;
		this.range = range;
		this.recoilSneaking = recoilSneaking;
		this.recoil = recoil;
		this.shouldRecoil = shouldRecoil;
		this.twoHanded = twoHanded;	
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new InventoryCapability(1, stack, nbt);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() 
	{
        ModelResourceLocation scope = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_scope", "inventory");
        ModelResourceLocation laser = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_laser", "inventory");
        ModelResourceLocation silencer = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_silencer", "inventory");
        ModelResourceLocation flashlight = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_flashlight", "inventory");
        ModelResourceLocation reload = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() + "_reload", "inventory");
        ModelResourceLocation normal = new ModelResourceLocation(Main.MODID + ":weapons/" + this.getUnlocalizedName() , "inventory");
        
        ModelBakery.registerItemVariants(this, scope, laser, silencer, flashlight, reload, normal);

        ModelLoader.setCustomMeshDefinition(this, stack -> {
     //   	System.out.println(normal);

        	InventoryAttachment att = InventoryAttachment.create(stack);
    		if (att.getStackInSlot(0) != ItemStack.EMPTY) {
    			if (att.isScopePresent()) {
    				return scope;
    			} else if (att.isFlashPresent()) {
    				return flashlight;
    			} else if (att.isLaserPresent()) {
    				return laser;
    			} else if (att.isSilencerPresent()) {
    				return silencer;
    			}
    			else return normal;
    		}
    		if (isReloading(stack))
    		{
    			return reload;
    		}
    		else return normal;
        });     
    }
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
	{
		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public String getUnlocalizedName() {
		String s = super.getUnlocalizedName();
		return s.substring(s.indexOf('.') + 1);
	}

	public boolean IsTwoHanded() {
		return twoHanded;
	}

	public int getMaxAmmo() {
		return this.maxAmmo;
	}

	public long getShotInterval() {
		return this.shotInterval;
	}

	public int getAmmoConsumption() {
		return this.ammoConsumption;
	}

	public boolean registerProjectile(ItemProjectile projectile) {
		return this.projectiles.add(projectile);
	}

	public boolean isValidProjectile(Item item) {
		return this.projectiles.contains(item);
	}
	
	static int reloadRightTicks = 0;
	static int reloadLeftTicks = 0;

	static int recoilRightTicks = 0;
	static int recoilLeftTicks = 0;
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		ItemStack leftStack  = ItemStack.EMPTY;
		ItemStack rightStack = ItemStack.EMPTY;
				
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) entity;
			
			if(!p.getHeldItemOffhand().isEmpty() &&  p.getHeldItemOffhand().getItem() instanceof ItemRangedWeapon)leftStack = p.getHeldItemOffhand();
			if(!p.getHeldItemMainhand().isEmpty() &&  p.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)rightStack = p.getHeldItemMainhand();
			
			if(!p.world.isRemote)
			{	
				updateServer(rightStack, leftStack, world, p);
				disableOffHandSlot(p);		
			}
			else
			{
				updateClient(rightStack, leftStack, world, p);
			}
		}			
	}
	
	private void updateServer(ItemStack rightStack, ItemStack leftStack, World world, EntityPlayer p) 
	{	
			if(!rightStack.isEmpty())
			{
				ItemRangedWeapon rightW = (ItemRangedWeapon) rightStack.getItem();
				if (rightW.isReloading(rightStack))
				{
					rightW.setReloadTicks(rightStack, p, reloadRightTicks++);
					if (rightW.getReloadTicks(rightStack) >= rightW.getReloadDuration())
					{
						rightW.setReloading(rightStack, p, false);
						rightW.setReloadTicks(rightStack, p, 0);
						rightW.hasAmmoAndConsume(rightStack, p);
						reloadRightTicks = 0;
					}
				}
				else if(rightW.fired(rightStack))
				{
					rightW.setRecoilTicks(rightStack, p, recoilRightTicks++);
		
					if(rightW.getRecoilTicks(rightStack) >= rightW.recoilDelay())
					{
						rightW.recoilDown(p, rightW.getRecoil(), rightW.getRecoilSneaking(), rightW.getShouldRecoil());
						Main.modChannel.sendTo(new RecoilRightGun(), (EntityPlayerMP) p);	
						rightW.setFired(rightStack, p, false);
						rightW.setRecoilTicks(rightStack, p, 0);
						recoilRightTicks = 0;
					}
				}
			}
			if(!leftStack.isEmpty())
			{
				ItemRangedWeapon leftW = (ItemRangedWeapon) leftStack.getItem();
				if (leftW.isReloading(leftStack))
				{
					leftW.setReloadTicks(leftStack, p, reloadLeftTicks++);
		
					if (leftW.getReloadTicks(leftStack) >= leftW.getReloadDuration())
					{
						leftW.setReloading(leftStack, p, false);
						leftW.setReloadTicks(leftStack, p, 0);
						leftW.hasAmmoAndConsume(leftStack, p);
						reloadLeftTicks = 0;
					}				
				}	
				else if(leftW.fired(leftStack))
				{
					leftW.setRecoilTicks(leftStack, p, recoilLeftTicks++);
						
					if(leftW.getRecoilTicks(leftStack) >= leftW.recoilDelay())
					{				
						leftW.recoilDown(p, leftW.getRecoil(), leftW.getRecoilSneaking(), leftW.getShouldRecoil());
						Main.modChannel.sendTo(new RecoilLeftGun(), (EntityPlayerMP) p);	
						leftW.setFired(leftStack, p, false);
						leftW.setRecoilTicks(leftStack, p, 0);	
						recoilLeftTicks = 0;					
					}	
				}
			}
		
	}

	private void updateClient(ItemStack rightStack, ItemStack leftStack, World world, EntityPlayer p) 
	{
		IUpdateAttachments.super.updateAttachments(rightStack, leftStack, p);
	}

	private void disableOffHandSlot(EntityPlayer p) 
	{
		if(!p.capabilities.isCreativeMode)
		{
			if(p.getHeldItemOffhand() != null &&  p.getHeldItemOffhand().getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon w = (ItemRangedWeapon)p.getHeldItemOffhand().getItem();
				if(w.IsTwoHanded())
				{
					if(p.inventory.inventoryChanged)
					{
						ItemStack i = p.getHeldItemOffhand().copy();
						p.inventory.removeStackFromSlot(40);
						p.inventory.addItemStackToInventory(i);		
					}	
				}
			}
			if(p.getHeldItemOffhand() != null &&  p.getHeldItemOffhand().getItem() instanceof ItemRangedWeapon && p.getHeldItemMainhand() != null && p.getHeldItemMainhand().getItem() instanceof ItemRangedWeapon)
			{
				ItemRangedWeapon rw = (ItemRangedWeapon)p.getHeldItemMainhand().getItem();

				if(rw.IsTwoHanded())
				{
					ItemStack i = p.getHeldItemOffhand().copy();
					p.inventory.removeStackFromSlot(40);
					p.inventory.addItemStackToInventory(i);
					p.world.updateEntity(p);
				}
			}	
		}
	}

	public Random getItemRand() {
		return new Random();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return MAX_DELAY;
	}
	
	public void setReloadTicks(ItemStack stack, EntityPlayer player, int reloadTicks) {
		stack.getTagCompound().setInteger("reloadTicks", reloadTicks);
	}
	
	public int getReloadTicks(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getInteger("reloadTicks");
	}
	
	public void setRecoilTicks(ItemStack stack, EntityPlayer player, int recoilTicks) {
		stack.getTagCompound().setInteger("recoilTicks", recoilTicks);
	}
	
	public int getRecoilTicks(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getInteger("recoilTicks");
	}
	
	public void setReloading(ItemStack stack, EntityPlayer player, boolean reloading) {
		stack.getTagCompound().setBoolean("reloading", reloading);
	}

	public boolean isReloading(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getBoolean("reloading");
	}

	public boolean fired(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getBoolean("fired");
	}

	public void setFired(ItemStack stack, EntityPlayer player, boolean model) {
		stack.getTagCompound().setBoolean("fired", model);
	}
	
	public boolean getUpdateModel(ItemStack stack) {
		checkNBT(stack);
		return stack.getTagCompound().getBoolean("model");
	}

	public void setUpdateModel(ItemStack stack, EntityPlayer player, boolean model) {
		stack.getTagCompound().setBoolean("model", model);
	}

	private void checkNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
	}

	public static int ticks = 0;
	public static int leftticks = 0;

	public void setActiveHand() {

	}

	public boolean onItemleftClick() {
		if (Mouse.getEventButtonState()) {
			if (Mouse.getEventButton() == 0) 
			{
				notClickedYet = true;
				return false;
			}
		} else {
			if (notClickedYet) {
				if (Mouse.getEventButton() == 0) 
				{
					notClickedYet = false;
					return true;
				}
			}
		}
		return false;
	}

	public void shootRightGun(ItemStack stack, World world, EntityPlayer entity) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			if (canFire(stack, entity)) {
				if (this.nextShotMillis < System.currentTimeMillis()) {
					postShootingEffects(stack, entity, world);
					setFired(stack, entity ,true);
					afterFire(stack, world, entity);
					Main.modChannel.sendToServer(new RightGunShoot());
				}
			} else {
				if (!this.isReloading(stack)) {
					soundEmpty(stack, world, entity);
				}

			}
		}
	}

	public void shootLeftGun(ItemStack stack, World world, EntityPlayer entity) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			if (canFire(stack, entity)) {
				if (this.nextShotMillis < System.currentTimeMillis()) {
					postShootingEffects(stack, entity, world);
					setFired(stack, entity ,true);
					afterFire(stack, world, entity);
					Main.modChannel.sendToServer(new LeftGunShoot());
				}
			} else {
				if (!this.isReloading(stack)) {
					soundEmpty(stack, world, entity);
				}

			}
		}
	}

	public void recoilDown(EntityPlayer entityIn, float recoil, float recoilSneaking, boolean shouldRecoil) {
		float i = recoil == 0F ? 0F : recoil - 0.5F;
		float j = recoilSneaking == 0F ? 0F : recoilSneaking - 0.5F;
		if (shouldRecoil)entityIn.rotationPitch += entityIn.isSneaking() ? i : j;
	}

	public float getRecoil() {
		return recoil;
	}

	public float getRecoilSneaking() {
		return recoilSneaking;
	}

	public boolean getShouldRecoil() {
		return shouldRecoil;
	}

	public void recoilUp(EntityPlayer entityIn, float recoil, float recoilSneaking, boolean shouldRecoil) {
		if (shouldRecoil)
			entityIn.rotationPitch -= entityIn.isSneaking() ? recoil : recoilSneaking;
	}

	public int recoilDelay() {
		return 4;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	public void hasAmmoAndConsume(ItemStack stack, EntityPlayer player) 
	{
		int ammoFinal = getAmmoQuantity(stack);
		String type = "";
		NonNullList<ItemStack> inventory = player.inventory.mainInventory;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack invStack = inventory.get(i);
			if (invStack != null)
				if (isValidProjectile(invStack.getItem())) {
					int stackSize = invStack.getCount();
					type = invStack.getItem().getUnlocalizedName().substring(5);
					int ammo = stackSize < this.getMaxAmmo() - ammoFinal ? stackSize : this.getMaxAmmo() - ammoFinal;
					ammoFinal += ammo;

					//Set stack size
					invStack.setCount(stackSize - ammo);
					if (invStack.getCount() < 1)
						inventory.remove(i);
					if (ammoFinal == this.getMaxAmmo())
						break;
				}
		}
		if (ammoFinal > 0) {
			setAmmoType(stack, type);
			setAmmoQuantity(stack, ammoFinal);
		}
	}

	public boolean canReload(ItemStack stack, EntityPlayer player)
	{
		if(getAmmoQuantity(stack) == getMaxAmmo()) return false;

		if(getMaxAmmo() == 1)
			{
			return !player.capabilities.isCreativeMode;
			}
		return getAmmoQuantity(stack) < getMaxAmmo() && !player.capabilities.isCreativeMode;
	}

	public boolean canFire(ItemStack stack, EntityPlayer player) {
		return (player.capabilities.isCreativeMode || isLoaded(stack, player));
	}

	public boolean hasAmmoInInventory(EntityPlayer player) {
		return findAvailableAmmo(player) != null;
	}

	public ItemProjectile findAvailableAmmo(EntityPlayer player) {
		for (ItemProjectile projectile : projectiles) {
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if (this.isProjectile(stack)) {
					return projectile;
				}
			}
		}
		return null;
	}

	protected boolean isProjectile(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemProjectile;
	}

	public int getAmmoQuantityInInventory(ItemStack stack, EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		String type = getAmmoType(stack);
		Item item = Item.REGISTRY.getObject(new ResourceLocation(Main.MODID + ":" + type));//GameRegistry.findItem(Main.MODID, type);
		int out = 0;
		if (type != null) { // && inventory.hasItemStack(item)
			for (ItemStack s : inventory.mainInventory) {
				if (s != null && s.getItem().equals(item)) {
					out += s.getCount();
				}
			}
		}
		return out;
	}

	public int getAmmoQuantity(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().getInteger("ammo");
		else
			return 0;
	}

	public void setAmmoQuantity(ItemStack stack, int ammo) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("ammo", ammo);
	}

	public String getAmmoType(ItemStack stack) {
		String type = null;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ammotype"))
			type = stack.getTagCompound().getString("ammotype");
		if (type == null || type.equals(""))
			type = this.getDefaultAmmoType();
		return type.toLowerCase();
	}

	public void setAmmoType(ItemStack stack, String type) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("ammotype", type);
	}

	public String getDefaultAmmoType() {
		return this.defaultAmmoType;
	}

	public boolean isLoaded(ItemStack stack, EntityPlayer player) {
		return getAmmoQuantity(stack) > 0 || player.capabilities.isCreativeMode;
	}

	public void soundEmpty(ItemStack itemstack, World world, EntityPlayer entityplayer) 
	{
		world.playSound(entityplayer, entityplayer.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS,
				1.0F, 1.0F / 0.8F);
	}

	public void soundCharge(ItemStack stack, World world, EntityPlayer player)
	{
		BlockPos pos = player.getPosition();
		world.playSound(player, pos,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(Main.MODID + ":" + this.getUnlocalizedName() + "_reload")),
				SoundCategory.PLAYERS, 0.7F, 0.9F / (getItemRand().nextFloat() * 0.2F + 0.0F));	
	}

	public abstract int getReloadDuration();

	public void applyProjectileEnchantments(EntityProjectile entity, ItemStack itemstack) {
		int damage = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(49), itemstack);
		if (damage > 0) {
			entity.setDamage(damage);
		}

		int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(48), itemstack);
		if (knockback > 0) {
			entity.setKnockbackStrength(knockback);
		}

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(50), itemstack) > 0) {
			entity.setFire(100);
		}
	}

	public final void postShootingEffects(ItemStack itemstack, EntityPlayer entityplayer, World world) {
		effectPlayer(itemstack, entityplayer, world);
		effectShoot(entityplayer, itemstack, world, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
				entityplayer.rotationYaw, entityplayer.rotationPitch);
	}

	public void effectPlayer(ItemStack itemstack, EntityPlayer entityplayer, World world) {
		float f = entityplayer.isSneaking() ? -0.01F : -0.02F;
		double d = -MathHelper.sin((entityplayer.rotationYaw / 180F) * 3.141593F)
				* MathHelper.cos((0 / 180F) * 3.141593F) * f;
		double d1 = MathHelper.cos((entityplayer.rotationYaw / 180F) * 3.141593F)
				* MathHelper.cos((0 / 180F) * 3.141593F) * f;
		recoilUp(entityplayer, recoil, recoilSneaking, shouldRecoil);
		entityplayer.addVelocity(d, 0, d1);
	}

	public void effectShoot(EntityPlayer p, ItemStack stack, World world, double x, double y, double z, float yaw,
			float pitch) {
		String soundPath = Main.MODID + ":" + this.getUnlocalizedName() + "_shoot";
		ItemRangedWeapon w = (ItemRangedWeapon) stack.getItem();
		if(!(w instanceof NonSupporting))
		{
			InventoryAttachment att = InventoryAttachment.create(stack);
			if (att != null && att.isSilencerPresent())
				soundPath = soundPath + "_silenced";
		}
		world.playSound(p, p.getPosition(), SoundEvent.REGISTRY.getObject(new ResourceLocation(soundPath)),
				SoundCategory.PLAYERS, 0.7F, 0.9F / (getItemRand().nextFloat() * 0.2F + 0.0F));

		float particleX = -MathHelper.sin(((yaw + 23) / 180F) * 3.141593F) * MathHelper.cos((pitch / 180F) * 3.141593F);
		float particleY = -MathHelper.sin((pitch / 180F) * 3.141593F) - 0.1F;
		float particleZ = MathHelper.cos(((yaw + 23) / 180F) * 3.141593F) * MathHelper.cos((pitch / 180F) * 3.141593F);

		for (int i = 0; i < 3; i++) {
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D,
					0.0D);
		}
		world.spawnParticle(EnumParticleTypes.FLAME, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
	}

	public void fire(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			for (int i = 0; i < getAmmoConsumption(); i++) {
				EntityProjectile p = createProjectile(stack, world, player);
				applyProjectileEnchantments(p, stack);
				if (p != null)
					world.spawnEntity(p);
			}
		}
		afterFire(stack, world, player);
	}

	public void afterFire(ItemStack stack, World world, EntityPlayer player) 
	{
		if (!player.capabilities.isCreativeMode)
			this.setAmmoQuantity(stack, this.getAmmoQuantity(stack) - ammoConsumption);
		int damage = 1;
		int ammo = this.getAmmoQuantity(stack);

		if (stack.getItemDamage() + damage > stack.getMaxDamage()) {
			String type = this.getAmmoType(stack);
			Item i = Item.REGISTRY.getObject(new ResourceLocation(Main.MODID + ":" + type));
			ItemStack s = new ItemStack(i, ammo);
			player.inventory.addItemStackToInventory(s);
		} else if (ammo < 1) {
			if (hasAmmoInInventory(player) && player.world.isRemote){ //FMLCommonHandler.instance().getSide().isClient()) {
				KeyBindingEvent.doReload();
			} else {
				this.setAmmoType(stack, "");
			}
		}
		this.nextShotMillis = System.currentTimeMillis() + getShotInterval();
		stack.damageItem(damage, player);
	}

	protected EntityProjectile createProjectile(ItemStack stack, World world, EntityPlayer player) {
		try {
			String type = this.getAmmoType(stack);

			Class<?> c = Class.forName(
					"com.bubbletrouble.gunmod.common.entity." + ProjectileType.valueOf(type.toUpperCase()).getEntity());
			Constructor<?> con = c.getConstructor(World.class, EntityLivingBase.class, float.class, float.class,
					double.class, int.class);
			return (EntityProjectile) con.newInstance(world, player, this.speed, this.inaccuracy, this.damage,
					this.range);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getRange() {
		return range;
	}

	public double getDamage() {
		return damage;
	}

	public void effectReloadDone(ItemStack stack, World world, EntityPlayer player) {}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) 
	{
		tooltip.add(ChatFormatting.DARK_AQUA + "Damage " + ChatFormatting.WHITE + this.getDamage() + " Hearts");
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			int duarabilty = stack.getMaxDamage() - stack.getItemDamage();
			tooltip.add(ChatFormatting.DARK_AQUA + I18n.translate("reload_time.title") + " " + ChatFormatting.WHITE  + getReloadDuration() / 20 + I18n.translate("seconds.title"));
			tooltip.add(ChatFormatting.DARK_AQUA + "Range " + ChatFormatting.WHITE + this.getRange() + " blocks");
			tooltip.add(ChatFormatting.DARK_AQUA + "Has recoil : " + ChatFormatting.WHITE + getShouldRecoil());
			tooltip.add(ChatFormatting.DARK_AQUA + "Duarabilty " + ChatFormatting.WHITE + duarabilty + "/" + stack.getMaxDamage());
			tooltip.add(ChatFormatting.DARK_AQUA + "Two-Handed : " + ChatFormatting.WHITE + IsTwoHanded());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) 
	{
		if(oldStack != null && newStack != null)
		{			
			if(oldStack.getItem() != newStack.getItem())
			{
				return true;	
			}
			if(slotChanged)
			{
				return true;	
			}			
		}
		return false;	
	}
}