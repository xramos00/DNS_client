package exceptions;

/*
 * Author - Martin Biolek
 * Link - https://github.com/mbio16/clientDNS
 * */
public class TimeoutException extends Exception {

	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super("Socket timeout");
	}
}
