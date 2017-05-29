package com.bubbletrouble.gunmod.common.cap;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DecreaseStamina implements IMessage
{
	private float walkingValue;
	
	public DecreaseStamina()
	{
		
	}
	
	public DecreaseStamina(float walkingValue)
	{
		this.walkingValue = walkingValue;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		walkingValue = buf.readFloat(); 
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(walkingValue);
	}

	public static class Handler implements IMessageHandler<DecreaseStamina, IMessage>
	{
        @Override
        public IMessage onMessage(DecreaseStamina message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
			final EntityPlayerMP player = ctx.getServerHandler().player;

            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	processMessage(message, player);
                }
            });
            return null; 
        }
	}

	static void processMessage(DecreaseStamina message, EntityPlayerMP player)
	{
		IStamina stam = player.getCapability(StaminaCapability.Stamina, null);
		stam.decreaseStamina(message.walkingValue);		
	}
}
