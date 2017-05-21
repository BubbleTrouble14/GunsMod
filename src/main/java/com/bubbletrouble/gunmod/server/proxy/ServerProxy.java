package com.bubbletrouble.gunmod.server.proxy;

import com.bubbletrouble.gunmod.common.proxy.CommonProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy
{
    @Override
    protected void registerEventHandlers()
    {
        super.registerEventHandlers();
    }

    @Override
    public EntityPlayer getPlayerFromContext(MessageContext ctx)
    {
        return ctx.getServerHandler().player;
    }

    @Override
    public long getTime()
    {
        return MinecraftServer.getCurrentTimeMillis();
    }

    @Override
    public long getWorldTime()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getTotalWorldTime();
    }
}
