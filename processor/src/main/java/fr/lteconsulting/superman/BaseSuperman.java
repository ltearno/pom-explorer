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
public abstract class BaseSuperman
{
	abstract protected Object processMessage(Supermessage message);

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

	protected Object sendMessage(Supermessage message)
	{
		// TODO : optionally capture call stack...
		synchronized (message)
		{
			message.setWaitingResult(true);

			try
			{
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

		if (message.isAborted())
			throw new IllegalStateException(
					"call has been aborted because the callee has exited ! It was processing this message : " + message);
		else
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

		while (!queue.isEmpty())
		{
			try
			{
				queue.take().abort();
			}
			catch (InterruptedException e)
			{
				log("interrupted while emptying message queue");
			}
		}

		queue.clear();
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
