package fr.lteconsulting.pomexplorer.tools;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter
{
	public List<String> split( String string )
	{
		List<String> strings = new ArrayList<>();

		int curStart = 0;
		boolean inQuote = false;

		for( int i = 0; i < string.length(); i++ )
		{
			char c = string.charAt( i );

			if( !inQuote && c == ' ' )
			{
				if( i > curStart )
					strings.add( string.substring( curStart, i ) );
				curStart = i + 1;
			}
			else if( !inQuote && c == '"' )
			{
				inQuote = true;
				curStart = i + 1;
			}
			else if( inQuote && c == '"' )
			{
				inQuote = false;
				strings.add( string.substring( curStart, i ) );
				curStart = i + 1;
			}
		}
		if( curStart < string.length() )
			strings.add( string.substring( curStart, string.length() ) );

		return strings;
	}
}
