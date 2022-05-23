package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class NonRecordSelectedException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonRecordSelectedException() {
		super("Non record for query selected");
	}
}
