package com.bubbletrouble.gunmod.utils;

import org.lwjgl.input.Keyboard;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings 
{
	public static KeyBinding reload;
	public static KeyBinding attachment;
	public static KeyBinding inventory;

	public static void init()
	{	
		reload = new KeyBinding("key.gunmod.reload", Keyboard.KEY_R, Main.MODID);
		ClientRegistry.registerKeyBinding(reload);
		
		attachment = new KeyBinding("key.gunmod.attachment", Keyboard.KEY_M, Main.MODID);
		ClientRegistry.registerKeyBinding(attachment);
		
		inventory = new KeyBinding("key.gunmod.inventory", Keyboard.KEY_J, Main.MODID);
		ClientRegistry.registerKeyBinding(inventory);
	}
}
