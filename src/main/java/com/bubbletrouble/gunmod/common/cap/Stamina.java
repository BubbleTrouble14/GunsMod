package com.bubbletrouble.gunmod.common.cap;

public class Stamina implements IStamina
{
	private float stamina;
	private float maxStamina;
	
	@Override
	public void setStamina(float valse) 
	{
		this.stamina = valse;
	}

	@Override
	public float getStamina() 
	{
		return stamina;
	}

	@Override
	public void increaseStamina(float valse)
	{
		if(stamina >= maxStamina)stamina = getMaxStatmina();
		else this.stamina += valse;
	}

	@Override
	public void decreaseStamina(float valse)
	{
		if(stamina <= 0)stamina = 0;
		else stamina -= valse;
	}

	@Override
	public void setMaxStamina(float value) 
	{
		maxStamina = value;
	}

	@Override
	public float getMaxStatmina() 
	{
		return maxStamina;
	}

}
