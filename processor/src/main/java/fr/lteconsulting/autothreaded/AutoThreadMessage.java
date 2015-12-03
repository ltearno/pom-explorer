package fr.lteconsulting.autothreaded;

import java.util.Arrays;

public class AutoThreadMessage
{
	private final int methodId;

	private final Object[] parameters;

	private volatile boolean waitingResult;

	private volatile boolean aborted;

	private Object result;

	public AutoThreadMessage(int methodId, Object[] parameters)
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

	public void abort()
	{
		synchronized (this)
		{
			this.waitingResult = false;
			this.aborted = true;
			this.notify();
		}
	}

	public int getMethodId()
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
		return "AutoThreadMessage [methodId=" + methodId + ", parameters=" + Arrays.toString(parameters) + "]";
	}

	public boolean isAborted()
	{
		return aborted;
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
