package fr.lteconsulting.pomexplorer.rpccommands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.lteconsulting.pomexplorer.Project;

/**
 * A quick and dirty generic json exporter, specific to pom explorer
 */
public class JsonExporter
{
	public JsonElement export( Object object ) throws IllegalArgumentException, IllegalAccessException
	{
		if( object == null )
			return JsonNull.INSTANCE;

		System.out.println( "export " + object.getClass().getSimpleName() );
		if( List.class.isAssignableFrom( object.getClass() ) )
		{
			JsonArray array = new JsonArray();
			List<?> list = (List<?>) object;

			for( Object item : list )
				array.add( export( item ) );

			return array;
		}
		else if( Map.class.isAssignableFrom( object.getClass() ) )
		{
			JsonObject jo = new JsonObject();
			Map<?, ?> map = (Map<?, ?>) object;

			for( Entry<?, ?> entry : map.entrySet() )
				jo.add( entry.getKey().toString(), export( entry.getValue() ) );

			return jo;
		}
		else if( object.getClass() == Integer.class || object.getClass() == Long.class )
		{
			return new JsonPrimitive( (Number) object );
		}
		else if( object.getClass() == String.class )
		{
			return new JsonPrimitive( (String) object );
		}
		else if( object.getClass() == Project.class )
		{
			return new JsonPrimitive( ((Project) object).getGav().toString() );
		}
		else if( object.getClass().isEnum() )
		{
			return new JsonPrimitive( ((Enum<?>) object).name() );
		}

		JsonObject jo = new JsonObject();
		jo.add( "class", new JsonPrimitive( object.getClass().getSimpleName() ) );

		for( Field field : getFields( object.getClass() ) )
		{
			if( !field.isAccessible() )
				field.setAccessible( true );
			jo.add( field.getName(), export( field.get( object ) ) );
		}

		return jo;
	}

	private List<Field> getFields( Class<?> cls )
	{
		List<Field> res = new ArrayList<Field>();
		getFields( cls, res );
		return res;
	}

	private void getFields( Class<?> cls, List<Field> list )
	{
		if( cls == null || cls == Object.class )
			return;

		for( Field f : cls.getDeclaredFields() )
		{
			if( (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC )
				continue;
			list.add( f );
		}

		getFields( cls.getSuperclass(), list );
	}
}