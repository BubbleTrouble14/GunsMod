package com.bubbletrouble.gunmod.common.capaility;

import com.bubbletrouble.gunmod.Main;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiTest extends GuiContainer {
	
	private static final ResourceLocation iconLocation = new ResourceLocation(Main.MODID,
			"textures/gui/attachment_gui.png");

	public GuiTest(InventoryPlayer inv, EntityPlayer player) 
	{
		super(new TestContainer(inv, player));
		this.xSize = 175;
		this.ySize = 165;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(new TextComponentTranslation("container.test_bag", new Object[0]).getUnformattedText(), 8, 3, 4210752);
		this.fontRenderer.drawString(new TextComponentTranslation("container.inventory", new Object[0]).getUnformattedText(), 8, 48, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		this.mc.getTextureManager().bindTexture(iconLocation);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
	}
}