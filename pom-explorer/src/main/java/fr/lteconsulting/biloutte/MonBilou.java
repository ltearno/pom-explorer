package fr.lteconsulting.biloutte;

import fr.lteconsulting.superman.Superman;

@Superman
public class MonBilou
{
	public String testMoi( String caca )
	{
		return "salut, tu m'as dit " + caca + " ?";
	}
	
	public void parle()
	{
		System.out.println("voilà, je parle... (thread " + Thread.currentThread().getName() + ":"
				+ Thread.currentThread().getId() + ")");
	}
}