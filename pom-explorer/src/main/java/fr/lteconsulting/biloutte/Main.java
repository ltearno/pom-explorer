package fr.lteconsulting.biloutte;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main
{
	public static void main(String[] args)
	{
		MonBilouSuperman bilou = new MonBilouSuperman();

		for (int t = 0; t < 10; t++)
		{
			new Thread()
			{
				@Override
				public void run()
				{
					int nb = 100;
					ArrayList<Future<String>> futures = new ArrayList<>();
					for (int i = 0; i < nb; i++)
					{
						futures.add(bilou.testMoiAsync("essai " + i + " sur thread " + Thread.currentThread().getId()));
						bilou.parleAsync();

						Thread.yield();
					}

					for (Future<String> future : futures)
					{
						Thread.yield();

						try
						{
							System.out.println(future.get());
						}
						catch (InterruptedException | ExecutionException e)
						{
							e.printStackTrace();
						}
					}
				}
			}.start();
		}

		sleep(1000);

		bilou.stop();
	}

	private static void sleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
