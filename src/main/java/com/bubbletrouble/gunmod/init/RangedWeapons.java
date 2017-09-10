package com.bubbletrouble.gunmod.init;

import com.bubbletrouble.gunmod.Main;
import com.bubbletrouble.gunmod.a.ItemAmmunition;
import com.bubbletrouble.gunmod.common.block.BlockCrafter;
import com.bubbletrouble.gunmod.common.block.BlockGreenScreen;
import com.bubbletrouble.gunmod.common.block.BlockLight;
import com.bubbletrouble.gunmod.common.entity.EntityAdvancedBullet;
import com.bubbletrouble.gunmod.common.entity.EntityProjectile;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleBullet;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleRifleAmmo;
import com.bubbletrouble.gunmod.common.entity.EntitySimpleShotgunAmmo;
import com.bubbletrouble.gunmod.common.handlers.EntityHandler;
import com.bubbletrouble.gunmod.common.item.ItemFabricatedPistol;
import com.bubbletrouble.gunmod.common.item.ItemGrenade;
import com.bubbletrouble.gunmod.common.item.ItemLongneckRifle;
import com.bubbletrouble.gunmod.common.item.ItemProjectile;
import com.bubbletrouble.gunmod.common.item.ItemRangedWeapon;
import com.bubbletrouble.gunmod.common.item.ItemShotgun;
import com.bubbletrouble.gunmod.common.item.ItemSimplePistol;
import com.bubbletrouble.gunmod.common.item.attachments.AttachmentType;
import com.bubbletrouble.gunmod.common.item.attachments.ItemAttachment;
import com.bubbletrouble.gunmod.common.proxy.CommonProxy;
import com.bubbletrouble.gunmod.common.tileentity.TECrafter;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RangedWeapons {

	public static ItemAttachment scope, flash_light, silencer, laser, holo_scope;
	public static ItemProjectile tranquilizer, simple_bullet, simple_rifle_ammo, simple_shotgun_ammo,
			rocket_propelled_grenade, advanced_bullet;
	public static ItemRangedWeapon rocket_launcher, tranq_gun;
	public static ItemRangedWeapon simple_pistol;
	public static ItemRangedWeapon fabricated_pistol;
	public static ItemRangedWeapon longneck_rifle;
	public static ItemRangedWeapon shotgun;
	public static ItemRangedWeapon rangedWeapon;
	public static BlockLight blockLight;
	public static BlockCrafter block_crafter;
	public static BlockGreenScreen green_screen;
	public static ItemGrenade grenade;
	public static Item item_test_ammo;
	public static Item item_test_attachment;

	public static void init() {
		GameRegistry.register(
				new com.bubbletrouble.gunmod.a.ItemRangedWeapon(new ResourceLocation(Main.MODID, "test_gun")) {
					@Override
					public boolean isValidAmmunition(ItemAmmunition item) {
						return item == item_test_ammo;
					}

					@Override
					public boolean supportsAttachment(AttachmentType type) {
						return type == AttachmentType.SCOPE;
					}

					@Override
					public ItemStack getAttachment(ItemStack itemStack) {
						return itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("attachment")
								? new ItemStack(itemStack.getTagCompound().getCompoundTag("attachment"))
								: ItemStack.EMPTY;
					}

					@Override
					public void setAttachment(ItemStack itemStack, ItemStack attachment) {
						if (itemStack.getTagCompound() == null)
							itemStack.setTagCompound(new NBTTagCompound());

						NBTTagCompound c = itemStack.getTagCompound();
						c.setTag("attachment", attachment.serializeNBT());
					}

					@Override
					public int getAmmunitionCapacity() {
						return 4;
					}
				});
		item_test_ammo = new ItemAmmunition(new ResourceLocation(Main.MODID, "test_ammo")) {
			@Override
			public EntityProjectile createProjectile(EntityPlayer player) {
				return new EntitySimpleBullet(player.world, player, 5, 0, 5, 20);
			}
		};
		GameRegistry.register(item_test_ammo);

		item_test_attachment = new com.bubbletrouble.gunmod.a.ItemAttachment(
				new ResourceLocation(Main.MODID, "test_attachment")) {

			@Override
			public void runUseEffect(World world, EntityPlayer player) {
				// TODO Auto-generated method stub

			}

			@Override
			public void runEffect(World world, EntityPlayer player) {
				// TODO Auto-generated method stub

			}

			@Override
			@SideOnly(Side.CLIENT)
			public void runClientSideUseEffect(World world, EntityPlayer player) {
				// TODO Auto-generated method stub

			}

			@Override
			@SideOnly(Side.CLIENT)
			public void runClientSideEffect(World world, EntityPlayer player) {
				// TODO Auto-generated method stub

			}

			@Override
			public AttachmentType getType() {
				return AttachmentType.SCOPE;
			}
		};
		GameRegistry.register(item_test_attachment);

		blockLight = new BlockLight();
		block_crafter = new BlockCrafter(CommonProxy.GUI.CRAFTER.getID());
		// green_screen = new BlockGreenScreen();
		// grenade = new ItemGrenade();

		GameRegistry.registerTileEntity(TECrafter.class, "TE_Crafter");

		// Guns
		fabricated_pistol = new ItemFabricatedPistol();
		GameRegistry.register(fabricated_pistol);
		simple_pistol = new ItemSimplePistol();
		GameRegistry.register(simple_pistol);
		longneck_rifle = new ItemLongneckRifle();
		GameRegistry.register(longneck_rifle);
		shotgun = new ItemShotgun();
		GameRegistry.register(shotgun);

		// Attachments
		scope = registerAttachment("scope", AttachmentType.SCOPE);
		flash_light = registerAttachment("flash_light", AttachmentType.FLASH);
		// holo_scope = registerAttachment("holo_scope", AttachmentType.HOLO_SCOPE);
		laser = registerAttachment("laser", AttachmentType.LASER);
		silencer = registerAttachment("silencer", AttachmentType.SILENCER);

		// Ammo
		advanced_bullet = addItemProjectile("advanced_bullet");
		simple_bullet = addItemProjectile("simple_bullet");
		simple_rifle_ammo = addItemProjectile("simple_rifle_ammo");
		simple_shotgun_ammo = addItemProjectile("simple_shotgun_ammo");

		// Register which bullets can be used for which gun
		longneck_rifle.registerProjectile(simple_rifle_ammo);
		fabricated_pistol.registerProjectile(advanced_bullet);
		simple_pistol.registerProjectile(simple_bullet);
		shotgun.registerProjectile(simple_shotgun_ammo);

		registerWeaponEntities();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		// Guns
		fabricated_pistol.initModel();
		simple_pistol.initModel();
		longneck_rifle.initModel();
		shotgun.initModel();
		block_crafter.initModel();
		// green_screen.initModel();

		// Attachments
		ModelLoader.setCustomModelResourceLocation(scope, 0,
				new ModelResourceLocation(Main.MODID + ":" + "scope", "inventory"));
		ModelLoader.setCustomModelResourceLocation(flash_light, 0,
				new ModelResourceLocation(Main.MODID + ":" + "flash_light", "inventory"));
		ModelLoader.setCustomModelResourceLocation(silencer, 0,
				new ModelResourceLocation(Main.MODID + ":" + "silencer", "inventory"));
		// ModelLoader.setCustomModelResourceLocation(holo_scope, 0, new
		// ModelResourceLocation(Main.MODID + ":" + "holo_scope" , "inventory"));
		ModelLoader.setCustomModelResourceLocation(laser, 0,
				new ModelResourceLocation(Main.MODID + ":" + "laser", "inventory"));

		// Ammo
		ModelLoader.setCustomModelResourceLocation(simple_rifle_ammo, 0,
				new ModelResourceLocation(Main.MODID + ":" + "simple_rifle_ammo", "inventory"));
		ModelLoader.setCustomModelResourceLocation(simple_shotgun_ammo, 0,
				new ModelResourceLocation(Main.MODID + ":" + "simple_shotgun_ammo", "inventory"));
		ModelLoader.setCustomModelResourceLocation(simple_bullet, 0,
				new ModelResourceLocation(Main.MODID + ":" + "simple_bullet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(advanced_bullet, 0,
				new ModelResourceLocation(Main.MODID + ":" + "advanced_bullet", "inventory"));
	}

	private static void registerWeaponEntities() {
		ResourceLocation simple_bullet = new ResourceLocation(Main.MODID + ":" + "simple_bullet_entity", "inventory");
		ResourceLocation simple_shotgun_ammo = new ResourceLocation(Main.MODID + ":" + "simple_shotgun_ammo_entity",
				"inventory");
		ResourceLocation simple_rifle_ammo = new ResourceLocation(Main.MODID + ":" + "simple_rifle_ammo_entity",
				"inventory");
		ResourceLocation advanced_bullet = new ResourceLocation(Main.MODID + ":" + "advanced_bullet_entity",
				"inventory");
		ResourceLocation grenade_entity = new ResourceLocation(Main.MODID + ":" + "grenade_entity", "inventory");

		EntityHandler.registerModEntity(simple_bullet, EntitySimpleBullet.class, "simple_bullet", Main.instance(), 16,
				20, true);
		EntityHandler.registerModEntity(simple_shotgun_ammo, EntitySimpleShotgunAmmo.class, "simple_shotgun_ammo",
				Main.instance(), 64, 10, true);
		EntityHandler.registerModEntity(simple_rifle_ammo, EntitySimpleRifleAmmo.class, "simple_rifle_ammo",
				Main.instance(), 64, 10, true);
		EntityHandler.registerModEntity(advanced_bullet, EntityAdvancedBullet.class, "advanced_bullet", Main.instance(),
				64, 10, true);
		// EntityHandler.registerModEntity(grenade_entity, EntityGrenade.class,
		// "grenade_entity", Main.instance(), 64, 10,
		// true);
		// EntityHandler.registerModEntity(EntityRocketPropelledGrenade.class,
		// "rocket_propelled_grenade", Main
		// .instance(), 64, 10, true);
	}

	protected static ItemAttachment registerAttachment(String name, AttachmentType type) {
		ItemAttachment item = new ItemAttachment(name, type);
		item.setUnlocalizedName(name);
		item.setRegistryName(name);
		item.setCreativeTab(Main.tabGuns);
		GameRegistry.register(item);
		return item;
	}

	protected static ItemProjectile addItemProjectile(String name) {
		ItemProjectile item = new ItemProjectile();
		item.setUnlocalizedName(name);
		item.setRegistryName(name);
		item.setCreativeTab(Main.tabGuns);
		GameRegistry.register(item);
		return item;
	}
}
