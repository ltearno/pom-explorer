package fr.lteconsulting.pomexplorer.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		sb.append( "<b>List of commands</b><br/>" );
		sb.append( "<i>You can type only the first letters of commands, for example '<b>gav li</b>' instead of '<b>gavs list</b>'</i><br/><br/>" );

		List<String> cs = new ArrayList<String>( commands.keySet() );
		Collections.sort( cs );

		for( String shortcut : cs )
		{
			Object c = commands.get( shortcut );

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
					if( pCls == Client.class || pCls == WorkingSession.class || pCls == CommandOptions.class )
						continue;

					sb.append( " <b><i>" + pCls.getSimpleName() + "</i></b>" );
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

		if( "?".equals( text ) )
			text = "help";

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
		int nbParamsGiven = 0;
		for( int i = 2; i < parts.length; i++ )
		{
			if( parts[i].startsWith( "--" ) )
			{
				i++;
				continue;
			}

			if( parts[i].startsWith( "-" ) )
				continue;

			nbParamsGiven++;
		}

		Method m = findMethodWith( command, verb, nbParamsGiven );
		if( m == null )
			return "verb not found (or with wrong parameters)";

		CommandOptions options = new CommandOptions();
		WorkingSession session = null;
		Class<?>[] argTypes = m.getParameterTypes();
		Object[] args = new Object[argTypes.length];
		int curPart = 2;
		int curArg = 0;
		while( curArg < argTypes.length || curPart < parts.length )
		{
			if( curPart < parts.length )
			{
				String val = parts[curPart];
				if( val.startsWith( "--" ) )
				{
					options.setOption( val.substring( 2 ), parts[curPart + 1] );
					curPart += 2;
					continue;
				}

				if( val.startsWith( "-" ) )
				{
					options.setOption( val.substring( 1 ), true );
					curPart++;
					continue;
				}
			}

			if( curArg < argTypes.length )
			{
				if( argTypes[curArg] == Client.class )
				{
					args[curArg] = client;
					curArg++;
					continue;
				}

				if( argTypes[curArg] == WorkingSession.class )
				{
					if( session == null )
					{
						session = client.getCurrentSession();
						if( session == null )
							return "You should have a session, type 'session create'.";
					}

					args[curArg] = session;
					curArg++;
					continue;
				}

				if( argTypes[curArg] == CommandOptions.class )
				{
					args[curArg] = options;
					curArg++;
					continue;
				}
			}

			if( argTypes[curArg] == Integer.class )
				args[curArg] = Integer.parseInt( parts[curPart] );
			else
				args[curArg] = parts[curPart];

			curPart++;
			curArg++;
		}

		try
		{
			Object result = m.invoke( command, args );
			return result == null ? null : result.toString();
		}
		catch( Exception e )
		{
			StringBuilder log = new StringBuilder();
			log.append( "Error when interpreting command '<b>" + text + "</b>'<br/>" );
			log.append( "Command class : <b>" + command.getClass().getSimpleName() + "</b><br/>" );
			log.append( "Command method : <b>" + m.getName() + "</b><br/>" );
			for( Object a : args )
				log.append( "Argument : " + (a == null ? "(null)" : ("class: " + a.getClass().getName() + " toString : " + a.toString())) + "<br/>" );

			Throwable t = e;
			if( t instanceof InvocationTargetException )
				t = ((InvocationTargetException) t).getTargetException();

			log.append( "<pre>" + t.toString() + "\r\n" );
			for( StackTraceElement st : t.getStackTrace() )
			{
				log.append( st.toString() + "\r\n" );
			}
			log.append( "</pre>" );

			return Tools.errorMessage( log.toString() );
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
			if( t != Client.class && t != WorkingSession.class && t != CommandOptions.class )
				c++;
		}
		return c;
	}
}
