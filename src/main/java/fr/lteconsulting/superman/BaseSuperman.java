package fr.lteconsulting.superman;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A Superman has :
 * 
 * <ul>
 * <li>Un thread,
 * <li>Une file d'attente de traitement des messages,
 * <li>Une interface implémentée par : un proxy pour appeler, un proxy pour implémenter
 */
public class BaseSuperman
{
	private final Thread thread;

	private final BlockingQueue<Supermessage> queue;

	private boolean stopping;

	public BaseSuperman()
	{
		queue = new ArrayBlockingQueue<>(100);
		thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				BaseSuperman.this.run();
			}
		});
		thread.setName(toString());
	}

	public void start()
	{
		thread.start();
	}

	public void stop()
	{
		System.out.println("stopping " + toString());
		stopping = true;
		thread.interrupt();
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			log("interrupted while joining working thread");
		}
	}

	public Object sendMessage(Supermessage message)
	{
		synchronized (message)
		{
			message.setWaitingResult(true);

			try
			{
				message.setCallerThread(Thread.currentThread());
				queue.put(message);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			while (message.isWaitingResult())
			{
				try
				{
					message.wait();
				}
				catch (InterruptedException e)
				{
					log("interrupted while waiting result");
				}
			}
		}

		return message.getResult();
	}

	private void run()
	{
		while (!stopping)
		{
			try
			{
				Supermessage message = queue.take();
				if (message != null)
				{
					Object result = processMessage(message);
					message.setResult(result);
				}
			}
			catch (InterruptedException e)
			{
				log("interrupted while waiting in message loop");
			}
		}
	}

	private volatile int nb = 1;

	private Object processMessage(Supermessage message)
	{
		log("received " + message);
		return "RESULTAT NUMBER " + nb++;
	}

	private void log(String message)
	{
		System.out.println(toString() + " : " + message);
	}

	@Override
	public String toString()
	{
		return "BaseSuperman " + System.identityHashCode(this) + " on thread " + thread.getId();
	}
}
