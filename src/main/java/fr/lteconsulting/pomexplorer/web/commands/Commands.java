package fr.lteconsulting.pomexplorer.web.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class Commands
{
	private final Map<String, Object> commands = new HashMap<>();

	public void addCommand( Object command )
	{
		if( command == null )
			return;

		String className = command.getClass().getSimpleName();
		addCommand( className.substring( 0, className.length() - "Command".length() ).toLowerCase(), command );
	}

	public void addCommand( String name, Object command )
	{
		if( name == null || command == null )
			return;

		commands.put( name, command );
	}

	public String help()
	{
		StringBuilder sb = new StringBuilder();

		for( Entry<String, Object> e : commands.entrySet() )
		{
			Object c = e.getValue();
			String shortcut = e.getKey();

			for( Method m : c.getClass().getDeclaredMethods() )
			{
				if( !Modifier.isPublic( m.getModifiers() ) )
					continue;

				sb.append( "<b>" );

				String mName = m.getName();
				if( mName.equals( "main" ) )
					sb.append( shortcut );
				else
					sb.append( shortcut + " " + mName );

				sb.append( "</b>" );

				for( Class<?> pCls : m.getParameterTypes() )
				{
					if( pCls == Client.class || pCls == WorkingSession.class )
						continue;

					sb.append( " [" + pCls.getSimpleName() + "]" );
				}

				Help help = m.getAnnotation( Help.class );
				if( help != null )
					sb.append( " : " + help.value() );

				sb.append( "<br/>" );
			}
		}

		return sb.toString();
	}

	/**
	 * Returns the error or null if success
	 */
	public String takeCommand( Client client, String text )
	{
		if( text == null || text.isEmpty() )
			return "no text";

		final String parts[] = text.split( " " );
		if( parts.length < 1 )
			return "syntax error (should be 'command [verb] [parameters]')";

		List<String> potentialCommands = Tools.filter( commands.keySet(), new Func1<String, Boolean>()
		{
			@Override
			public Boolean exec( String c )
			{
				return c.startsWith( parts[0] );
			}
		} );

		if( potentialCommands == null || potentialCommands.isEmpty() )
			return "command not found: " + parts[0];
		if( potentialCommands.size() != 1 )
			return "ambiguous command: " + parts[0] + " possible are " + potentialCommands;

		Object command = commands.get( potentialCommands.get( 0 ) );

		String verb = parts.length >= 2 ? parts[1] : "main";
		int nbParamsGiven = parts.length - 2;
		if( nbParamsGiven < 0 )
			nbParamsGiven = 0;

		Method m = findMethodWith( command, verb, nbParamsGiven );
		if( m == null )
			return "verb not found (or with wrong parameters)";

		WorkingSession session = null;
		Class<?>[] argTypes = m.getParameterTypes();
		Object[] args = new Object[argTypes.length];
		int curPart = 2;
		for( int i = 0; i < argTypes.length; i++ )
		{
			if( argTypes[i] == Client.class )
			{
				args[i] = client;
			}
			else if( argTypes[i] == WorkingSession.class )
			{
				if( session == null )
				{
					session = client.getCurrentSession();
					if( session == null )
						return "You should have a session, type 'session create'.";
				}

				args[i] = session;
			}
			else
			{
				args[i] = parts[curPart];

				curPart++;
			}
		}

		try
		{
			Object result = m.invoke( command, args );
			return result == null ? null : result.toString();
		}
		catch( Exception e )
		{
			e.getCause().printStackTrace();

			return e.getCause().getMessage();
		}
	}

	private Method findMethodWith( Object o, final String verb, final int nbParamsGiven )
	{
		List<Method> methods = Tools.filter( o.getClass().getMethods(), new Func1<Method, Boolean>()
		{
			@Override
			public Boolean exec( Method m )
			{
				return Modifier.isPublic( m.getModifiers() ) && m.getName().startsWith( verb ) && getRealParametersCount( m ) == nbParamsGiven;
			}
		} );

		if( methods == null || methods.size() != 1 )
			return null;

		return methods.get( 0 );
	}

	private int getRealParametersCount( Method m )
	{
		int c = 0;
		for( Class<?> t : m.getParameterTypes() )
		{
			if( t != Client.class && t != WorkingSession.class )
				c++;
		}
		return c;
	}
}
