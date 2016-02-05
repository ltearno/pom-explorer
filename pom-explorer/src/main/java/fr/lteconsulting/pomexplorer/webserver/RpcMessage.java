package fr.lteconsulting.pomexplorer.webserver;

import java.util.Map;

public class RpcMessage
{
	private String service;
	private String method;
	private Map<String, Object> parameters;

	public RpcMessage( String service, String method, Map<String, Object> parameters )
	{
		this.service = service;
		this.method = method;
		this.parameters = parameters;
	}

	public RpcMessage()
	{
	}

	public String getService()
	{
		return service;
	}

	public void setService( String service )
	{
		this.service = service;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod( String method )
	{
		this.method = method;
	}

	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	public void setParameters( Map<String, Object> parameters )
	{
		this.parameters = parameters;
	}
}
