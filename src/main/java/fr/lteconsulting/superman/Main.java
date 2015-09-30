package fr.lteconsulting.superman;

public class Main
{
	public static void main(String[] args)
	{
		BaseSuperman superman = new BaseSuperman();

		superman.start();

		sleep(1000);
		for (int i = 0; i < 10; i++)
		{
			Object result = superman.sendMessage(new Supermessage("getId", null));
			System.out.println("result : " + result);
		}

		sleep(3000);

		superman.stop();

		System.out.println("exited.");
	}

	private static void sleep(int x)
	{
		try
		{
			Thread.sleep(x);
		}
		catch (InterruptedException e)
		{
		}
	}
}
