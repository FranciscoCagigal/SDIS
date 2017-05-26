package protocols;

public interface Constants {
	
	static final int CHUNKSIZE = 64000;
	
	public static final String CRLF ="\r\n";
	
	public static final String COMMA_DELIMITER = ";";
	public static final String NEW_LINE_SEPARATOR = "\n";
	
	public static final String COMMAND_BACKUP = "BACKUP";
	public static final String COMMAND_STORED = "STORED";
	public static final String COMMAND_GET = "GETCHUNK";
	public static final String COMMAND_CHUNK = "CHUNK";
	public static final String COMMAND_DELETE = "DELETE";
	public static final String COMMAND_RESTORE = "RESTORE";
	public static final String COMMAND_DELETED = "DELETED";
	public static final String COMMAND_REMOVED = "REMOVED";
	public static final String COMMAND_FINDMASTER = "FINDMASTER";
	public static final String COMMAND_IMMASTER = "IMMASTER";
	public static final String COMMAND_BEGINELECTION = "BEGINELECTION";
	public static final String COMMAND_DISPUTEMASTER = "DISPUTEMASTER";
	public static final String COMMAND_NAMES = "NAMES";

}
