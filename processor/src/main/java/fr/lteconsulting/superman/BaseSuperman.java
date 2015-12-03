package fr.lteconsulting.superman;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Superman has :
 * 
 * <ul>
 * <li>a thread,
 * <li>a message processing queue
 */
public abstract class BaseSuperman
{
	abstract protected Object processMessage(Supermessage message);

	protected void onEmptyMessageQueue()
	{
	}

	private static volatile int nextBaseSupermanId = 0;

	private final int baseSupermanId;

	private final Thread thread;

	private final BlockingQueue<Supermessage> queue;
	
	private final boolean processEmptyQueue;

	private boolean stopping;

	public BaseSuperman()
	{
		this( false );
	}
	
	public BaseSuperman( boolean processEmptyQueue )
	{
		this.processEmptyQueue = processEmptyQueue;
		baseSupermanId = nextBaseSupermanId++;
		queue = new ArrayBlockingQueue<>(100);
		thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				BaseSuperman.this.run();
			}
		});
		thread.setName(this.getClass().getSimpleName() + " Superman:" + baseSupermanId);
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

	public Future<Object> postMessage(final Supermessage message)
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
		}

		return new Future<Object>()
		{
			@Override
			public boolean isDone()
			{
				return message.isWaitingResult();
			}

			@Override
			public boolean isCancelled()
			{
				return message.isAborted();
			}

			@Override
			public Object get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException
			{
				throw new UnsupportedOperationException("Not yet implemented");
			}

			@Override
			public Object get() throws InterruptedException, ExecutionException
			{
				synchronized (message)
				{
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
					throw new ExecutionException(new IllegalStateException(
							"call has been aborted because the callee has exited ! It was processing this message : "
									+ message));
				else
					return message.getResult();
			}

			@Override
			public boolean cancel(boolean arg0)
			{
				message.abort();
				return false;
			}
		};
	}

	private void run()
	{
		while (!stopping)
		{
			try
			{
				if( processEmptyQueue )
				{
					while( queue.isEmpty() )
						onEmptyMessageQueue();
				}
				
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
