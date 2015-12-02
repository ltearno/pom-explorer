package fr.lteconsulting.pomexplorer.webserver;

import java.util.Date;

public class MessageFactory
{
	public static String newGuid()
	{
		return new Date().toString() + "-" + Math.random();
	}

	public static Message htmlMessage( String talkGuid, String message )
	{
		if( message == null )
			return null;

		return new Message( newGuid(), talkGuid, null, false, "html", message );
	}

	public static Message closeTalkMessage( String talkGuid )
	{
		return new Message( newGuid(), talkGuid, null, true, null, null );
	}

	public static Message hangOutTextMessage( String talkGuid, String question )
	{
		return new Message( newGuid(), talkGuid, null, false, "hangout/question", question );
	}
}
