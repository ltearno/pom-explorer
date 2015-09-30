package fr.lteconsulting.superman;

import java.util.Arrays;

public class Supermessage
{
	private final String methodId;

	private final Object[] parameters;

	private Thread callerThread;

	private volatile boolean waitingResult;

	private Object result;

	public Supermessage(String methodId, Object[] parameters)
	{
		this.methodId = methodId;
		this.parameters = parameters;
	}

	public Object getResult()
	{
		return result;
	}

	public void setResult(Object result)
	{
		synchronized (this)
		{
			this.waitingResult = false;
			this.result = result;
			this.notify();
		}
	}

	public String getMethodId()
	{
		return methodId;
	}

	public Object[] getParameters()
	{
		return parameters;
	}

	@Override
	public String toString()
	{
		return "Supermessage [methodId=" + methodId + ", parameters=" + Arrays.toString(parameters) + "]";
	}

	public Thread getCallerThread()
	{
		return callerThread;
	}

	public void setCallerThread(Thread callerThread)
	{
		this.callerThread = callerThread;
	}

	public boolean isWaitingResult()
	{
		return waitingResult;
	}

	public void setWaitingResult(boolean waitingResult)
	{
		this.result = null;
		this.waitingResult = waitingResult;
	}
}
