package fr.lteconsulting.pomexplorer.rpccommands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.webserver.RpcMessage;

public class RpcServices
{
	private final Map<String, Object> services = new HashMap<>();

	public void addService( Object service )
	{
		if( service == null )
			return;

		String className = service.getClass().getSimpleName();
		if( className.endsWith( "Service" ) )
			className = className.substring( 0, className.length() - "Service".length() );

		addService( className.toLowerCase(), service );
	}

	public void addService( String name, Object service )
	{
		if( name == null || service == null )
			return;

		services.put( name, service );
	}

	public Object takeCall( Client client, Log log, RpcMessage rpcMessage )
	{
		Object service = services.get( rpcMessage.getService() );
		if( service == null )
			throw new RuntimeException( "service " + rpcMessage.getService() + " not found" );

		Method methods[] = service.getClass().getMethods();
		Method method = null;
		for( Method m : methods )
		{
			if( m.getName().equalsIgnoreCase( rpcMessage.getMethod() ) )
			{
				method = m;
				break;
			}
		}
		if( method == null )
			throw new RuntimeException( "method not found" );

		Class<?> parameterTypes[] = method.getParameterTypes();
		Object parameters[] = new Object[parameterTypes.length];

		for( int i = 0; i < parameterTypes.length; i++ )
		{
			Class<?> paramType = parameterTypes[i];
			Object param = null;

			if( paramType == Client.class )
				param = client;
			else if( paramType == Session.class )
				param = client.getCurrentSession();
			else if( paramType == Log.class )
				param = log;
			else if( paramType == Gav.class )
				param = Gav.parse( (String) (rpcMessage.getParameters().get( method.getParameters()[i].getName() )) );
			else
				param = rpcMessage.getParameters().get( method.getParameters()[i].getName() );

			parameters[i] = param;
		}

		try
		{
			Object result = method.invoke( service, parameters );
			return result;
		}
		catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
		{
			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}
}
