package fr.lteconsulting.pomexplorer.webserver;

public class Message
{
	private String guid;
	private String talkGuid;
	private String responseTo;
	private boolean isClosing;
	private String payloadFormat;
	private String payload;

	public Message( String guid, String talkGuid, String responseTo, boolean isClosing, String payloadFormat, String payload )
	{
		this.guid = guid;
		this.talkGuid = talkGuid;
		this.responseTo = responseTo;
		this.isClosing = isClosing;
		this.payloadFormat = payloadFormat;
		this.payload = payload;
	}

	public String getGuid()
	{
		return guid;
	}

	public String getTalkGuid()
	{
		return talkGuid;
	}

	public String getResponseTo()
	{
		return responseTo;
	}

	public boolean isClosing()
	{
		return isClosing;
	}

	public String getPayloadFormat()
	{
		return payloadFormat;
	}

	public String getPayload()
	{
		return payload;
	}
}
