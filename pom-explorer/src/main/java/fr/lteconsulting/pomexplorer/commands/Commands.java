package fr.lteconsulting.pomexplorer.commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.model.Gav;

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
		sb.append( "<i>You can type only the first letters of commands, for example '<b>st co</b>' instead of '<b>stats components</b>'</i><br/><br/>" );

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

				for( int i = 0; i < m.getParameterTypes().length; i++ )
				{
					Class<?> pCls = m.getParameterTypes()[i];

					if( pCls == Client.class || pCls == Session.class || pCls == CommandOptions.class || pCls == Log.class )
						continue;

					sb.append( " <b><i>" + m.getParameters()[i].getName() + "</i></b>" );
				}

				Help help = m.getAnnotation( Help.class );
				if( help != null )
					sb.append( " : " + help.value() );

				sb.append( "<br/>" );

				Annotation[][] pass = m.getParameterAnnotations();
				for( int i = 0; i < m.getParameterTypes().length; i++ )
				{
					Help ph = getAnnotation( pass[i], Help.class );
					if( ph != null && !ph.value().isEmpty() )
					{
						sb.append( "<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>" + m.getParameters()[i].getName() + "</i></b> : " );
						sb.append( ph.value() );
						sb.append( "<br/>" );
					}
				}
			}
		}

		return sb.toString();
	}

	@SuppressWarnings( "unchecked" )
	private static <T> T getAnnotation( Annotation[] annotations, Class<?> clazz )
	{
		if( annotations == null || annotations.length == 0 )
			return (T) null;
		for( Annotation a : annotations )
			if( clazz.isAssignableFrom( a.getClass() ) )
				return (T) a;
		return (T) null;
	}

	/**
	 * Returns the error or null if success
	 */
	public void takeCommand( Client client, Log log, String text )
	{
		if( text == null || text.isEmpty() )
		{
			log.html( Tools.warningMessage( "no text" ) );
			return;
		}

		if( "?".equals( text ) )
			text = "help";

		final String parts[] = text.split( " " );
		CommandCallInfo info = findMethodForCommand( parts, log );
		if( info == null )
			return;

		CommandOptions options = new CommandOptions();
		Session session = client.getCurrentSession();
		Class<?>[] argTypes = info.method.getParameterTypes();
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

				if( argTypes[curArg] == Log.class )
				{
					args[curArg] = log;
					curArg++;
					continue;
				}

				if( argTypes[curArg] == Session.class )
				{
					if( session == null )
					{
						log.html( Tools.warningMessage( "you should have a session, type 'session create'." ) );
						return;
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

				if( argTypes[curArg] == FilteredGAVs.class )
				{
					args[curArg] = new FilteredGAVs( parts[curPart] );
					curArg++;
					curPart++;
					continue;
				}

				if( argTypes[curArg] == Gav.class )
				{
					args[curArg] = parts[curPart] == null ? null : Gav.parse( parts[curPart] );
					if( args[curArg] == null )
					{
						log.html( Tools.warningMessage( "Argument " + (curArg + 1) + " should be a GAV specified with the group:artifact:version format please" ) );
						return;
					}
					curArg++;
					curPart++;
					continue;
				}

				if( argTypes[curArg] == Project.class )
				{
					if( session == null )
					{
						log.html( Tools.warningMessage( "you should have a session, type 'session create'." ) );
						return;
					}

					args[curArg] = parts[curPart] == null ? null : session.projects().forGav( Gav.parse( parts[curPart] ) );
					if( args[curArg] == null )
					{
						log.html( Tools.warningMessage( "Argument " + (curArg + 1) + " should be a GAV specified with the group:artifact:version format please" ) );
						return;
					}
					curArg++;
					curPart++;
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
			info.method.invoke( info.command, args );
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "Error when interpreting command '<b>" + text + "</b>'" ) );
			log.html( "Command class : <b>" + info.command.getClass().getSimpleName() + "</b><br/>" );
			log.html( "Command method : <b>" + info.method.getName() + "</b><br/>" );
			for( Object a : args )
				log.html( "Argument : " + (a == null ? "(null)" : ("class: " + a.getClass().getName() + " toString : " + a.toString())) + "<br/>" );

			Tools.logStacktrace( e, log );
		}
	}

	public class CommandCallInfo
	{
		public final Object command;

		public final Method method;

		public CommandCallInfo( Object command, Method method )
		{
			this.command = command;
			this.method = method;
		}
	}

	// public for testing
	public CommandCallInfo findMethodForCommand( String[] parts, Log log )
	{
		if( parts.length < 1 )
		{
			log.html( Tools.warningMessage( "syntax error (should be 'command [verb] [parameters]')" ) );
			return null;
		}

		List<Entry<String, Object>> potentialCommands = commands.entrySet().stream()
				.filter( e -> e.getKey().toLowerCase().startsWith( parts[0].toLowerCase() ) )
				.collect( Collectors.toList() );

		if( potentialCommands == null || potentialCommands.isEmpty() )
		{
			log.html( Tools.warningMessage( "command not found: " + parts[0] ) );
			return null;
		}
		if( potentialCommands.size() != 1 )
		{
			List<String> possible = new ArrayList<>();
			potentialCommands.forEach( ( e ) -> possible.add( e.getKey() ) );
			log.html( Tools.warningMessage( "ambiguous command: " + parts[0] + " possible are " + possible ) );
			return null;
		}

		Entry<String, Object> commandEntry = potentialCommands.get( 0 );
		Object command = commandEntry.getValue();

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
		{
			log.html( Tools.warningMessage( "verb '" + verb + "' does not exist for command: " + commandEntry.getKey() ) );
			return null;
		}

		return new CommandCallInfo( command, m );
	}

	private Method findMethodWith( Object o, final String verb, final int nbParamsGiven )
	{
		List<Method> methods = new ArrayList<>();
		for( Method m : o.getClass().getMethods() )
		{
			if( !(Modifier.isPublic( m.getModifiers() ) && m.getName().toLowerCase().startsWith( verb.toLowerCase() ) && getRealParametersCount( m ) == nbParamsGiven) )
				continue;

			methods.add( m );
		}

		if( methods == null || methods.size() != 1 )
			return null;

		return methods.get( 0 );
	}

	private int getRealParametersCount( Method m )
	{
		int c = 0;
		for( Class<?> t : m.getParameterTypes() )
		{
			if( t != Client.class && t != Session.class && t != CommandOptions.class && t != Log.class )
				c++;
		}
		return c;
	}
}
