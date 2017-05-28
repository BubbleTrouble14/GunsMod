package com.bubbletrouble.gunmod.common.handlers;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EntityHandler
{
    static int entityID = 50;

    static int idCount = 0;

    public EntityHandler()
    {
    }

    public static void registerMonster(Class eClass, String name, Biome... biomes)
    {
        Random rand = new Random(name.hashCode());
        int mainColor = rand.nextInt() * 16777215;
        int secondColor = rand.nextInt() * 16777215;

       // EntityRegistry.registerModEntity(eClass, name, idCount++, Main.instance(), 10, 1, false, mainColor, secondColor);
        EntityRegistry.addSpawn(eClass, 25, 2, 4, EnumCreatureType.CREATURE, biomes);
    }

    public static void registerMonster(Class eClass, String name)
    {
        registerMonster(eClass, name, Biomes.BEACH, Biomes.DESERT, Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.EXTREME_HILLS);
    }

    public static void registerModEntity(ResourceLocation registryName, Class<? extends Entity> eClass, String name, Object mod, int trackRange, int updateFreq, boolean sVU)
    {
        EntityRegistry.registerModEntity(registryName, eClass, name, ++entityID, mod, trackRange, updateFreq, sVU);
    }
}
